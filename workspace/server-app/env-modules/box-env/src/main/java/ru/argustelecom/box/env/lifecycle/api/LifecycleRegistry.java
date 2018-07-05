package ru.argustelecom.box.env.lifecycle.api;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.nls.LocaleUtils.format;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.annotation.concurrent.ThreadSafe;
import javax.enterprise.context.ApplicationScoped;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Synchronized;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.impl.factory.LifecycleBuilderImpl;
import ru.argustelecom.system.inf.configuration.ServerRuntimeProperties;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

/**
 * Единый реестр жизненных циклов всех бизнес-объектов, которые есть в системе. В этом реестре хранятся соответствия
 * типов бизнес-объектов и квалифицированных фабрик жизненных циклов этих бизнес-объектов.
 * 
 * <p>
 * Записи в реестр попадают автоматически при старте сервера. Более подробно смотри
 * {@linkplain ru.argustelecom.box.env.lifecycle.impl.LifecycleFactoryBinder LifecycleFactoryBinder}
 * 
 * <p>
 * Каждый бизнес-объект, обладающий жизненным циклом, может иметь один или несколько различных жизненных циклов,
 * применяемых к этому объекту в зависимости от некоторых внешних условий. Например, у подписки есть короткий цикл (без
 * приостановки) и полный цикл (с приостановками за неуплату или по требованию). Какой именно жизненный цикл будет
 * применяться определяется на основании условий предоставления продукта, на который оформлена эта подписка.
 * Соответственно, в этом реестре для типа объекта "Подписка" будет храниться две записи, определяющие конкретную
 * фабрику жизненного цикла. Для квалификации конкретного жизненного цикла может применяться любой Serializable объект,
 * удовлетворяющий определенным условиям ({@linkplain LifecycleObject#getLifecycleQualifier() см подробное описание}).
 * 
 * <p>
 * В упрощенном виде хранение структуры записей в фабрике можно представить следующим образом:
 * 
 * <pre>
 * LifecycleObject.class: [qualifier1 --> LifecycleFactory1.class
 *                         qualifier2 --> LifecycleFactory2.class]
 * </pre>
 * 
 * Если жизненный цикл для какого-то объекта не квалифицирован, то будет использоваться квалификатор по-умолчанию:
 * 
 * <pre>
 * LifecycleObject.class: [DEFAULT --> LifecycleFactory.class]
 * </pre>
 * 
 * <p>
 * При инициализации жизненных циклов для объектов во времени выполнения приложения, эти жизненные цилы могут быть
 * закэшированы. За это поведение отвечает свойство (ServerRuntimeProperty) box.lifecycle.cacheable. Если оно равно
 * true, то реестр будет кэшировать производимые им жизненные циклы. Если свойство не указано или указано явно как
 * false, то при каждом запросе жизненного цикла он будет инстанцироваться и конфигурироваться заново.
 * 
 * <p>
 * Реестр может быть использован как {@code ApplicationScoped ManagedBean}, если он получен при помощи инжекции. В этом
 * случае, если включено кэширование, то рано или поздно реестр закэширует все жизненные циклы и начнет отдавать их
 * вызывающему достаточно быстро. Другой вариант использования -- непосредственное инстанцирование экземпляра реестра в
 * месте, где может понадобиться жизненный цикл. В этом случае у нового экземпляра реестра будет свой кэш и запрос
 * жизненного цикла будет порождать и настраивать отдельный экземпляр. Возможность ручного инстанцирования предназначена
 * для вариантов использования в местах, из которых не доступен контейнер, например, если жизненный цикл понадобится в
 * каком-либо методе персистентной сущности (или в тесте).
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
@ThreadSafe
@ApplicationScoped
public class LifecycleRegistry implements Serializable {

	//@formatter:off

	private static final long serialVersionUID = -1257722111437529910L;
	private static final Logger log = Logger.getLogger(LifecycleRegistry.class);
	private static final Serializable DEFAULT_QUALIFIER = "DEFAULT";
	private static final Collection<RegistryItem<?, ?, ?>> REGISTRY = new ConcurrentLinkedQueue<>();

	private transient Map<LifecycleCacheKey, Lifecycle<?, ?>> cache = new ConcurrentHashMap<>();

	/**
	 * Возвращает полностью настроенный и сконфигурированный жизненный цикл для указанного бизнес-объекта. В качестве 
	 * квалификатора будет использован {@linkplain LifecycleObject#getLifecycleQualifier()} или квалификатор по умолчанию, 
	 * если на уровне бизнес-объекта квалификатор не определен. 
	 * 
	 * @param businessObject - бизнес-объект, чей жизненный цикл необходимо получить
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * 
	 * @return настроенный и сконфигурированный {@linkplain Lifecycle жизненный цикл}  
	 * 
	 * @see #getLifecycle(Class, Serializable)
	 */
	@Synchronized
	@SuppressWarnings("unchecked")
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	Lifecycle<S, O> getLifecycle(O businessObject) {
		checkRequiredArgument(businessObject, "businessObject");
		
		O unproxiedObject = initializeAndUnproxy(businessObject);
		Serializable lifecycleQualifier = unproxiedObject.hasLifecycleQualifier() 
			? unproxiedObject.getLifecycleQualifier() 
			: DEFAULT_QUALIFIER;
		return getLifecycle((Class<O>) unproxiedObject.getClass(), lifecycleQualifier);
	}

	/**
	 * Возвращает полностью настроенный, сконфигурированный и квалифицированный жизненный цикл для указанного 
	 * бизнес-объекта. Если у объекта перекрыт метод {@linkplain LifecycleObject#getLifecycleQualifier() получения 
	 * квалификатора} и полученный квалификатор отличается от указанного, то будет выброшено исключение.
	 * 
	 * @param businessObject - бизнес-объект, чей жизненный цикл необходимо получить
	 * @param qualifier - квалификатор жизненного цикла
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * 
	 * @return настроенный и сконфигурированный {@linkplain Lifecycle жизненный цикл}  
	 * 
	 * @see #getLifecycle(Class, Serializable)
	 */
	@Synchronized
	@SuppressWarnings("unchecked")
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	Lifecycle<S, O> getLifecycle(O businessObject, Serializable qualifier) {
		checkRequiredArgument(businessObject, "businessObject");
		checkRequiredArgument(qualifier, "qualifier");
		
		O unproxiedObject = initializeAndUnproxy(businessObject);
		
		checkArgument(
			!unproxiedObject.hasLifecycleQualifier() || Objects.equals(unproxiedObject.getLifecycleQualifier(), qualifier),
			"LifecycleObject has own lifecycle qualifier %s, which mismatch with specified qualifier %s",
			unproxiedObject.getLifecycleQualifier(), qualifier
		);
		return getLifecycle((Class<O>) unproxiedObject.getClass(), qualifier);
	}

	/**
	 * Возвращает полностью настроенный и сконфигурированный жизненный цикл для указанного типа бизнес-объекта. 
	 * В качестве квалификатора будет использован квалификатор по умолчанию.
	 * 
	 * @param objectClass - тип бизнес-объекта, чей жизненный цикл необходимо получить
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * 
	 * @return настроенный и сконфигурированный {@linkplain Lifecycle жизненный цикл}  
	 * 
	 * @see #getLifecycle(Class, Serializable)
	 */
	@Synchronized
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	Lifecycle<S, O> getLifecycle(Class<O> objectClass) {
		return getLifecycle(objectClass, DEFAULT_QUALIFIER);
	}

	/**
	 * Возвращает полностью настроенный, сконфигурированный и квалифицированный жизненный цикл для указанного типа
	 * бизнес-объекта. Для создания жизненного цикла используется зарегистрированная в этом реестре 
	 * {@linkplain LifecycleFactory фабрика}. Если для указанного типа бизнес-объекта не зарегистрирована ни одна 
	 * фабрика, то будет брошено соответствующее исключение. Если для указанного типа бизнес-объекта не зарегистрирована
	 * фабрика с указанным квалификатором, то будет брошено соответствующее исключение.
	 * 
	 * <p>
	 * Процесс конфигурирования жизненного цикла выполняется посредством конструирования {@linkplain LifecycleBuilder}, 
	 * передачей этого билдера в фабрику и получением конечного результата из билдера. Следовательно, непосредственным 
	 * построением жизненного цикла занимает прикладной код, написанный в фабрике. Если жизненный цикл был закэширован 
	 * ранее, то билдер и фабрика использоваться не будут, а будет сразу возвращен закэшированный результат. Если 
	 * кэширование включено, но в кэше нужного жизненного цикла еще нет, то конструирование жизненног цикла будет 
	 * выполнено по указанному выше алгоритму и, в конечном итоге, сконфигурированный жизненный цикл будет закэширован 
	 * для повторного использования в дальнейшем. Для ситуаций, когда кэш по каким-либо причинам необходимо сбросить, 
	 * предусмотрен явный метод {@linkplain #invalidate()}
	 * 
	 * @param objectClass - тип бизнес-объекта, чей жизненный цикл необходимо получить
	 * @param qualifier - квалификатор жизненного цикла
	 * 
 	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * 
	 * @return настроенный и сконфигурированный {@linkplain Lifecycle жизненный цикл}  
	 */
	@Synchronized
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>, F extends LifecycleFactory<S, O>> 
	Lifecycle<S, O> getLifecycle(Class<O> objectClass, Serializable qualifier) {
		checkRequiredArgument(objectClass, "objectClass");
		checkRequiredArgument(qualifier, "qualifier");

		log.debugv("Creating lifecycle for object {0} with qualifier {1}", objectClass, qualifier);

		Lifecycle<S, O> lifecycle = getCachedLifecycle(objectClass, qualifier);
		if (lifecycle != null) {
			log.debugv("Found cached lifecycle {0}", lifecycle);
			return lifecycle;
		}

		RegistryItem<S, O, F> registryItem = getRegistryItem(objectClass);
		checkState(registryItem != null, "Lifecycle for entity '%s' with qualifier '%s' is not registered", 
				objectClass.getSimpleName(), qualifier);

		Class<F> factoryClass = registryItem.getFactoryClass(qualifier);
		checkState(factoryClass != null, "Lifecycle factory for entity '%s' with qualifier '%s' is not registered", 
				objectClass.getSimpleName(), qualifier);

		F factory = ReflectionUtils.newInstance(factoryClass);

		// Единственная зависимость интерфейса от реализации жизненного цикла. Может быть устранена множеством различных 
		// способов, как простых, так и сложных. Однако на текущий момент времени это попахивает оверинжинирингом, 
		// поэтому отложено до лучших времен  
		LifecycleBuilderImpl<S, O> builder = new LifecycleBuilderImpl<S, O>().begin();
		factory.buildLifecycle(builder);
		lifecycle = builder.build();

		cacheLifecycle(objectClass, qualifier, lifecycle);
		log.debugv("Created and cached new lifecycle {0}", lifecycle);

		return lifecycle;
	}

	@Synchronized
	public void invalidate() {
		cache.clear();
	}

	@SuppressWarnings("unchecked")
	private <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	Lifecycle<S, O> getCachedLifecycle(Class<O> objectClass, Serializable qualifier) {
		Lifecycle<?, ?> cachedLifecycle = cache.get(new LifecycleCacheKey(objectClass, qualifier));
		if (cachedLifecycle != null) {
			return (Lifecycle<S, O>) cachedLifecycle; 
		}
		return null;
	}

	private <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	void cacheLifecycle(Class<O> objectClass, Serializable qualifier, Lifecycle<S, O> lifecycle) {
		if (isCacheable()) {
			cache.putIfAbsent(new LifecycleCacheKey(objectClass, qualifier), lifecycle);
		}
	}

	private boolean isCacheable() {
		Object prop = ServerRuntimeProperties.instance().getProperties().get("box.lifecycle.cacheable");
		String propValue = prop == null ? null : prop.toString();
		return !Strings.isNullOrEmpty(propValue) && Boolean.parseBoolean(propValue);
	}

	/**
	 * Выполняет регистрацию указанного типа фабрики жизненного цикла для указанного типа бизнес-объекта с 
	 * квалификатором по-умолчанию.
	 * 
	 * @param objectClass - тип бизнес-объекта, для которого регистрируется жизенный цикл
	 * @param factoryClass - тип фабрики жизненного цикла для указанного типа бизнес-объекта
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * @param <F> - тип фабрики жизненного цикла
	 * 
	 * @see #register(Class, Class, Serializable)
	 */
	@Synchronized
	public static <S extends LifecycleState<S>, O extends LifecycleObject<S>, F extends LifecycleFactory<S, O>> 
	void register(Class<O> objectClass, Class<F> factoryClass) {
		register(objectClass, factoryClass, DEFAULT_QUALIFIER);
	}

	/**
	 * Выполняет регистрацию указанного типа фабрики жизненного цикла для указанного типа бизнес-объекта с 
	 * указанным квалификатором. Повторная регистрация фабрики при условии совпадения всех трех параметров (тип фабрики, 
	 * тип бизнес-объекта, квалификатор) будет проигнорирована. Если же попытаться зарегистрировать для бизнес-объекта
	 * фабрику с другим квалификатором или попытаться использовать один и тот же квалификатор для регистрации двух 
	 * фабрик, то будет брошено соответствующее исключение, и, т.к. регистрация чаще всего выполняется при старте 
	 * сервера, это приведет к невозможности запуска. 
	 * 
	 * @param objectClass - тип бизнес-объекта, для которого регистрируется жизенный цикл
	 * @param factoryClass - тип фабрики жизненного цикла для указанного типа бизнес-объекта
	 * @param factoryQualifier - квалификатор жизненного цикла
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * @param <F> - тип фабрики жизненного цикла 
	 */
	@Synchronized
	public static <S extends LifecycleState<S>, O extends LifecycleObject<S>, F extends LifecycleFactory<S, O>> 
	void register(Class<O> objectClass, Class<F> factoryClass, Serializable factoryQualifier) {
		checkArgument(objectClass != null, "objectClass is required");
		checkArgument(factoryQualifier != null, "factoryQualifier is required");

		RegistryItem<S, O, F> registryItem = getRegistryItem(objectClass);
		if (registryItem == null) {
			registryItem = new RegistryItem<>(objectClass);
			REGISTRY.add(registryItem);
		}
		registryItem.addFactoryClass(factoryQualifier, factoryClass);

		log.infov(
			"Lifecycle factory {0} is registered for object {1} with qualifier {2}", 
			factoryClass, objectClass, factoryQualifier
		);
	}

	@SuppressWarnings("unchecked")
	private static <S extends LifecycleState<S>, O extends LifecycleObject<S>, F extends LifecycleFactory<S, O>> 
	RegistryItem<S, O, F> getRegistryItem(Class<O> objectClass) {
		for (RegistryItem<?, ?, ?> registryItem : REGISTRY) {
			if (Objects.equals(objectClass, registryItem.getObjectClass())) {
				return (RegistryItem<S, O, F>) registryItem;
			}
		}
		return null;
	}

	private static class RegistryItem<S extends LifecycleState<S>, O extends LifecycleObject<S>, F extends LifecycleFactory<S, O>> 
			implements Serializable {

		private static final long serialVersionUID = 5955767620825084072L;

		@Getter
		private Class<O> objectClass;

		private Map<String, Class<F>> factories = new ConcurrentHashMap<>();

		public RegistryItem(Class<O> objectClass) {
			this.objectClass = checkNotNull(objectClass);
		}

		public Class<F> getFactoryClass(Serializable factoryQualifier) {
			return factories.get(factoryQualifier.toString());
		}

		public void addFactoryClass(Serializable factoryQualifier, Class<F> factoryClass) {
			if (factories.containsKey(factoryQualifier.toString())) {
				Class<F> registeredFactoryClass = factories.get(factoryQualifier.toString());
				if (!Objects.equals(registeredFactoryClass, factoryClass)) {
					String message = format("Factory with qualifier {0} is already registered", factoryQualifier);
					throw new IllegalStateException(message);
				} else {
					log.warnv("Attempt to re-register the factory with the qualifier {0}", factoryQualifier);
				}
			} else {
				factories.put(factoryQualifier.toString(), factoryClass);
			}
		}
		
	}

	@Getter
	@EqualsAndHashCode
	private static class LifecycleCacheKey implements Serializable {

		private static final long serialVersionUID = 5427869710305248704L;

		private Class<?> objectClass;
		private String qualifier;

		public LifecycleCacheKey(Class<?> objectClass, Serializable qualifier) {
			this.objectClass = objectClass;
			this.qualifier = qualifier.toString();
		}
	}

	//@formatter:on
}
