package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Synchronized;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

/**
 * Контекст для создания типов данных, их свойств, групп свойств, и экземпляров типов данных. НЕ ПРЕДНАЗНАЧЕН ДЛЯ
 * ИСПОЛЬЗОВАНИЯ В ПРИКЛАДНОМ КОДЕ.
 * <p>
 * Используется в {@link ru.argustelecom.box.env.type.TypeFactory TypeFactory}, LifecycleVariablesImpl, тестах
 * 
 * @param <T>
 */
public final class TypeCreationalContext<T extends Type> {

	private static Map<Class<? extends Type>, TypeCreationalContext<?>> cache = new ConcurrentHashMap<>();
	private Class<T> typeClass;

	private TypeCreationalContext(Class<T> typeClass) {
		checkRequiredArgument(typeClass, "typeClass");
		this.typeClass = typeClass;
	}

	/**
	 * Возвращает закэшированный экземпляр контекста для указанного типа
	 * 
	 * @param type
	 *            - тип данных, для которого необходимо получить контекст
	 * 
	 * @return всегда не null контекст
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Type> TypeCreationalContext<T> creationalContext(T type) {
		checkRequiredArgument(type, "type");
		return creationalContext((Class<T>) type.getClass());
	}

	/**
	 * Возвращает закэшированный экземпляр контекста для класса указанного типа
	 * 
	 * @param typeClass
	 *            - класс типа данных, для которого необходимо получить контескт
	 * 
	 * @return всегда не null контекст
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Type> TypeCreationalContext<T> creationalContext(Class<T> typeClass) {
		checkRequiredArgument(typeClass, "typeClass");
		TypeCreationalContext<?> ctx = cache.get(typeClass);
		if (ctx == null) {
			ctx = createContext(typeClass);
		}
		return (TypeCreationalContext<T>) ctx;
	}

	@Synchronized
	private static <T extends Type> TypeCreationalContext<?> createContext(Class<T> typeClass) {
		TypeCreationalContext<?> ctx = cache.get(typeClass);
		if (ctx == null) {
			ctx = new TypeCreationalContext<>(typeClass);
			cache.put(typeClass, ctx);
		}
		return ctx;
	}

	// ***************************************************************************************************************

	/**
	 * Создает новый тип данных с указанным идентификатором. Что это будет за тип данных декларирует контекст создания
	 * 
	 * @param typeId
	 *            - идентификатор нового типа данных
	 * 
	 * @return созданный, но не сохраненный в контексте персистенции тип данных.
	 */
	public T createType(Long typeId) {
		checkRequiredArgument(typeId, "typeId");
		return ReflectionUtils.newInstance(typeClass, typeId);
	}

	/**
	 * Создает новое свойство в указанном типе данных. Свойство не сохраняется в контексте персистенции явно. Весь
	 * расчет на каскадное сохранение, т.е. НЕ НУЖНО ЯВНО ПЕРСИСТИТЬ СОЗДАННОЕ СВОЙСТВО!
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий создаваемым свойством
	 * @param propertyClass
	 *            - класс нового свойства
	 * @param propertyKeyword
	 *            - ключевое слово нового свойства
	 * @param propertyId
	 *            - идентификатор нового свойства
	 * 
	 * @return созданное свойство.
	 */
	public <P extends TypeProperty<?>> P createProperty(T propertyOwner, Class<P> propertyClass, String propertyKeyword,
			Long propertyId) {

		checkRequiredArgument(propertyOwner, "propertyOwner");
		checkRequiredArgument(propertyClass, "propertyClass");
		checkRequiredArgument(propertyId, "propertyId");
		checkState(propertyOwner.isPropertyAllowed(propertyClass));

		TypePropertyHolder propertyHolder = propertyOwner.getPropertyHolder();
		P property = propertyHolder.createProperty(propertyClass, propertyKeyword, propertyId);
		property.setOrdinalNumber(propertyOwner.getPropertiesWithoutGroup().size());

		return property;
	}

	/**
	 * Создает новое свойство в указаннй группе указанного типа данных. Важно, указанная группа принадлежала типу
	 * данных. Свойство не сохраняется в контексте персистенции явно. Весь расчет на каскадное сохранение, т.е. НЕ НУЖНО
	 * ЯВНО ПЕРСИСТИТЬ СОЗДАННОЕ СВОЙСТВО!
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий создаваемым свойством
	 * @param propertyGroup
	 *            - группа свойств, должна принадлежать типу. Новое свойство будет автоматически добавлено к этой группе
	 * @param propertyClass
	 *            - класс нового свойства
	 * @param propertyKeyword
	 *            - ключевое слово нового свойства
	 * @param propertyId
	 *            - идентификатор нового свойства
	 * 
	 * @return созданное свойство.
	 */
	public <P extends TypeProperty<?>> P createProperty(T propertyOwner, TypePropertyGroup propertyGroup,
			Class<P> propertyClass, String propertyKeyword, Long propertyId) {

		checkRequiredArgument(propertyOwner, "propertyOwner");
		checkRequiredArgument(propertyGroup, "propertyGroup");
		checkRequiredArgument(propertyClass, "propertyClass");
		checkRequiredArgument(propertyId, "propertyId");
		checkState(propertyOwner.isPropertyAllowed(propertyClass));

		TypePropertyHolder holder = propertyGroup.getHolder();
		checkArgument(Objects.equals(holder, propertyOwner.getPropertyHolder()));

		P result = holder.createProperty(propertyClass, propertyKeyword, propertyId);
		propertyGroup.addProperty(result);

		return result;
	}

	/**
	 * Выполняет логическое удаление указанного свойства из указанного типа. После логического удаления свойство
	 * становится deprecated, выносится из группы, в которой оно находилось
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий свойством
	 * @param propertyKeyword
	 *            - ключевое слово "удаляемого" свойства
	 */
	public void removeProperty(T propertyOwner, String propertyKeyword) {
		checkRequiredArgument(propertyOwner, "propertyOwner");
		checkRequiredArgument(propertyKeyword, "propertyKeyword");

		propertyOwner.getPropertyHolder().removeProperty(propertyKeyword);
	}

	/**
	 * Выполняет логическое удаление указанного свойства из указанного типа. После логического удаления свойство
	 * становится deprecated, выносится из группы, в которой оно находилось
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий свойством
	 * @param property
	 *            - свойство для удаления
	 */
	public <P extends TypeProperty<?>> void removeProperty(T propertyOwner, P property) {
		checkRequiredArgument(propertyOwner, "propertyOwner");
		checkRequiredArgument(property, "property");

		propertyOwner.getPropertyHolder().removeProperty(property);
	}

	/**
	 * Создает в указанном типе новую группу свойств, по умолчанию - пустую. Если группа с указанным именем уже
	 * существует, то новая группа не будет создана
	 * 
	 * @param groupOwner
	 *            - тип, владеющий создаваемой группой свойств
	 * @param groupName
	 *            - имя группы
	 * @param groupId
	 *            - идентификатор группы
	 * 
	 * @return созданну или найденную по имени группу
	 */
	public TypePropertyGroup createPropertyGroup(T groupOwner, String groupName, Long groupId) {
		checkRequiredArgument(groupOwner, "groupOwner");
		return groupOwner.getPropertyHolder().createPropertyGroup(groupName, groupId);
	}

	/**
	 * Создает в указанном типе новую группу свойств, по умолчанию - пустую. Если группа с указанным именем уже
	 * существует, то новая группа не будет создана
	 *
	 * @param groupOwner
	 *            - тип, владеющий создаваемой группой свойств
	 * @param groupName
	 *            - имя группы
	 * @param groupId
	 *            - идентификатор группы
	 * @param ordinalNumber
	 *            - порядковый номер группы
	 *
	 * @return созданну или найденную по имени группу
	 */
	public TypePropertyGroup createPropertyGroup(T groupOwner, String groupName, Long groupId, Integer ordinalNumber) {
		checkRequiredArgument(groupOwner, "groupOwner");
		return groupOwner.getPropertyHolder().createPropertyGroup(groupName, groupId, ordinalNumber);
	}

	/**
	 * Удаляет указанную группу из типа. Удаление группы возможно только в том случае, если в этой группе не содержится
	 * активных свойств. Удаление группы выполняется физически, т.е. группа реально будет удалена из типа
	 * 
	 * @param groupOwner
	 *            - тип, владеющий группой свойств
	 * @param group
	 *            - группа, которую необходимо удалить
	 * 
	 * @return true, если группу удалось удалить
	 */
	public boolean removePropertyGroup(T groupOwner, TypePropertyGroup group) {
		checkRequiredArgument(groupOwner, "groupOwner");
		checkRequiredArgument(group, "group");

		return groupOwner.getPropertyHolder().removePropertyGroup(group);
	}

	/**
	 * Создает экземпляр указанного типа
	 * 
	 * @param instanceType
	 *            - тип для которого создается экземпляр
	 * @param instanceClass
	 *            - Java класс экземпляра
	 * @param instanceId
	 *            - идентификатор нового экземпляра данных
	 * 
	 * @return созданный экземпляр указанного типа
	 */
	public <I extends TypeInstance<T>> I createInstance(T instanceType, Class<I> instanceClass, Long instanceId) {
		checkRequiredArgument(instanceType, "instanceType");
		checkRequiredArgument(instanceClass, "instanceClass");
		checkRequiredArgument(instanceId, "instanceId");

		I instance = ReflectionUtils.newInstance(instanceClass, instanceId);
		instance.setType(instanceType);
		initDefaults(instanceType, instance);
		return instance;
	}

	/**
	 * Создает экземпляр указанного типа по его прототипу. Копирует из прототипа значения свойств, указанных явно и не
	 * являющихся при этом значениями по умолчанию. Свойства, унаследованные от прототипа становятся заблокированными и
	 * их изменение возможно только при помощи привилегированного режима
	 * 
	 * @param prototype
	 *            - прототип для создания нового производного экземпляра
	 * @param instanceClass
	 *            - Java класс экземпляра
	 * @param instanceId
	 *            - идентификатор нового экземпляра данных
	 * 
	 * @return созданный и проинициализированный экземпляр указанного типа
	 */
	public <S extends TypeInstance<T> & TypeInstanceSpec<T>, D extends TypeInstance<T> & TypeInstanceDerivative<T, S>> D createInstanceByProto(
			S prototype, Class<D> instanceClass, Long instanceId) {

		D derivative = createInstance(prototype.getType(), instanceClass, instanceId);
		derivative.setPrototype(prototype);
		initInstanceWithProto(derivative, prototype);
		return derivative;
	}

	/**
	 * Выполняет инициализацию свойств значениями по умолчанию, если они указаны при конфигурировании типа данных
	 * 
	 * @param instanceType
	 *            - тип экземпляра
	 * @param instance
	 *            - экземпляр, для которого необходимо проинициализировать значения по умолчанию
	 */
	public <I extends TypeInstance<T>> void initDefaults(T instanceType, I instance) {
		instanceType.getPropertyHolder().getProperties().forEach(property -> property.initDefaults(instance));
	}

	/**
	 * Выполняет инициализацию производного экземпляра типа по его прототипу. Для копирования на текущий момент
	 * выбираются только те свойства, которые явно указаны и при этом не являются значениями по умолчанию
	 * 
	 * @param derivative
	 *            - производный экземпляр, созданный по прототипу
	 * @param prototype
	 *            - прототип, по образу которого создали и инициализируют производный экземпляр
	 */
	public <S extends TypeInstance<T> & TypeInstanceSpec<T>, D extends TypeInstance<T> & TypeInstanceDerivative<T, S>> void initInstanceWithProto(
			D derivative, S prototype) {

		T type = prototype.getType();
		for (TypeProperty<?> property : type.getProperties()) {
			boolean needCopyNLock = !property.isValueDefault(prototype) && property.isValuePresent(prototype);
			if (needCopyNLock) {
				property.copyValue(prototype, derivative);
				property.lockValue(derivative);
			}
		}
	}

	/**
	 * Создает accessor для указанного свойства в пределах указанного экземпляра. Accessor может быть создан в
	 * привилегированном режиме, что позволит устанавливать значения для свойств, заблокированных на уровне текущего
	 * экземпляра
	 * 
	 * @param instance
	 *            - экземпляр, для которого необходимо создать Accessor свойства
	 * @param property
	 *            - свойство, для которого необходимо создать Accessor
	 * @param privileged
	 *            - true, если Accessor должен быть привилегированным
	 * 
	 * @return созданный и проинициализированный PropertyAccessor для указанных свойства и экземпляра
	 */
	public <V, P extends TypeProperty<V>, I extends TypeInstance<T>> TypePropertyAccessor<V> createAccessor(I instance,
			P property, boolean privileged) {

		checkRequiredArgument(instance, "instance");
		checkRequiredArgument(property, "property");

		return new TypePropertyAccessor<>(property, instance, privileged);
	}

}
