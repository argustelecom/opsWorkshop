package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.Sets.newHashSet;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;
import static java.util.Optional.of;
import static java.util.Optional.ofNullable;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MappedSuperclass;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import com.google.common.collect.Sets;

import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class Type extends MetadataUnit<Long> implements Identifiable {

	private static final long serialVersionUID = 940571031621196294L;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = { CascadeType.ALL })
	@JoinColumn(name = "property_holder_id")
	private TypePropertyHolder propertyHolder;

	@Transient
	private Set<TypePropertyRef> supportedPropertyTypes;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected Type() {
		super();
	}

	/**
	 * Создает тип данных. Т.к. тип данных является метаданными, то для его идентификации необходимо использовать
	 * генератор {@link MetadataUnit#generateId()} или {@link MetadataUnit#generateId(EntityManager)}). Этот же
	 * идентификатор распространяется на холдера свойств типа данных. Только использование единого генератора для всех
	 * потомков типа может гарантированно уберечь от наложения идентификаторов в холдерах.
	 * <p>
	 * Отныне создавать тип напрямую вызовом конструктора запрещено. Для создания типа данных необходимо использовать
	 * {@link ru.argustelecom.box.env.type.TypeFactory#createType(Class) TypeFactory#createType}. Не делай этот метод
	 * публичным ни здесь, ни в потомках!!!
	 * 
	 * @param id
	 *            - идентификатор, полученный из генератора идентификаторов метаданных. Правильный ID гарантируется
	 *            TypeFactory
	 */
	protected Type(Long id) {
		super(id);
		propertyHolder = new TypePropertyHolder(id);
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	@Override
	protected Long checkId(Long id) {
		return checkNotNull(id);
	}

	// ***************************************************************************************************************

	/**
	 * Возвращает объект, предназначенный для хранения метаданных свойств текущего типа. Всегда не null. Не должен
	 * выноситься на уровень public, т.к. представляет собой элемент внутренней структуры типа данных
	 * 
	 * @return хранилище метаданных свойств
	 */
	protected TypePropertyHolder getPropertyHolder() {
		return propertyHolder;
	}

	/**
	 * Возвращает коллекцию всех доступных для текущего типа данных свойств. Свойства в этой коллекции всегда активны
	 * (не deprecated), могут редактироваться пользователем (редактирование метаданных), на уровне экземпляров могут
	 * быть указаны значения
	 * 
	 * @return коллекцию свойств или пустую коллекцию, если свойства не определены или если все они неактивны
	 */
	public Set<TypeProperty<?>> getProperties() {
		return propertyHolder.getProperties();
	}

	/**
	 * Возвращает все активные свойства указанного класса. Свойства в этой коллекции всегда активны (не deprecated),
	 * могут редактироваться пользователем (редактирование метаданных), на уровне экземпляров могут быть указаны
	 * значения
	 * 
	 * @param propertyClass
	 *            - класс свойств для фильтрации
	 * 
	 * @return коллекцию свойств или пустую коллекцию, если свойства указанного класса не определены или если все они
	 *         неактивны
	 */
	public <P extends TypeProperty<?>> Set<P> getProperties(Class<P> propertyClass) {
		return propertyHolder.getProperties(propertyClass);
	}

	/**
	 * Возвращает все неактивные, устаревшие (deprecated) свойства. Эти свойства нельзя редактировать и для них нельзя
	 * указывать новые значения на уровне экземпляров. Однако уже указанные значения по прежнему хранятся на уровне
	 * экземпляра и, в будущем, могут быть прочитаны.
	 * 
	 * @return коллекцию устаревших свойств или пустую коллекцию, если таких свойств нет
	 */
	public Set<TypeProperty<?>> getDeprecatedProperties() {
		return propertyHolder.getDeprecatedProperties();
	}

	/**
	 * Возвращает коллекцию всех свойств, как активных, так и устаревших
	 * 
	 * @return коллекцию всех свойств или пустую коллекцию, если не определено ни одного свойства
	 */
	public Set<TypeProperty<?>> getAllProperties() {
		return propertyHolder.getAllProperties();
	}

	/**
	 * Возвращает свойство по его ключевому слову. Если ключевое слово не определено для свойства, то это свойство не
	 * может быть найдено с использованием указанного метода. Если ключевое свойство определено, то для него
	 * гарантируется уникальность в пределах текущей спецификации.
	 * 
	 * @param keyword
	 *            - ключевое слово свойство, уникальное в пределах текущей спецификации
	 * 
	 * @return найденное свойство или null если свойства с таким ключевым словом нет
	 */
	public TypeProperty<?> getProperty(String keyword) {
		return propertyHolder.getProperty(keyword);
	}

	/**
	 * Возвращает свойство по его ключевому слову и выполняет приведение типа этого свойства к классу, указанному в
	 * качестве входного аргрумента. Если свойства с таким ключевым словом нет или если тип этого свойства отличается от
	 * укзанного, то будет возвращен null.
	 * 
	 * @param propertyClass
	 *            - класс свойства с указанным ключевым словом
	 * @param keyword
	 *            - ключевое слово свойства
	 * 
	 * @return свойство, приведенное к указанному типу или null, если свойство с таким ключевым словом не определено или
	 *         если его типо отличается от указанного
	 */
	public <P extends TypeProperty<?>> P getProperty(Class<P> propertyClass, String keyword) {
		return propertyHolder.getProperty(propertyClass, keyword);
	}

	/**
	 * FIXME переименовать в hasProperties и осмыслить как "имеются ли свойства", а не как "пусто"
	 * <p>
	 * Пусто что, пусто где, пусто почему?
	 * 
	 * @return
	 */
	public boolean isEmpty() {
		return !propertyHolder.hasProperties();
	}

	/**
	 * Определяет, есть ли свойство с указанным ключевым словом в коллекции свойств текущего типа.
	 * 
	 * @param keyword
	 *            - ключевое слово свойства
	 * 
	 * @return true, если в текущем типе не определено свойство с указанным ключевым словом
	 */
	public boolean hasProperty(String keyword) {
		return getProperty(keyword) != null;
	}

	/**
	 * Определяет, принадлежит ли указанное свойство текущему типу. Используется для проверки допустимости присвоения
	 * значения свойство экземпляру типа, а так же в других кейсах
	 * 
	 * @param property
	 *            - свойство
	 * 
	 * @return true, если указанное свойство принадлежит текущему типу
	 */
	public <P extends TypeProperty<?>> boolean hasProperty(P property) {
		return propertyHolder.hasProperty(property);
	}

	/**
	 * Возвращает список групп свойств, объявленных в этом типе данных
	 * 
	 * @return список групп или пустую коллекцию, если групп нет
	 */
	public List<TypePropertyGroup> getPropertyGroups() {
		return propertyHolder.getPropertyGroups();
	}

	/**
	 * Определяет одну единственную группу по ее имени
	 * 
	 * @param groupName
	 *            - имя искомой группы
	 * 
	 * @return найденную группу или null, если группы с таким именем нет
	 */
	public TypePropertyGroup getPropertyGroup(String groupName) {
		return propertyHolder.getPropertyGroup(groupName);
	}

	/**
	 * Перемещает характеристики из одной группы в другую.
	 *
	 * @param from
	 *            - группа, из которой необходимо перенести характеристики
	 * @param to
	 *            - группа, в которую необходимо добавить группу
	 * @param propertiesToMove
	 *            - характеристики, которые неоходимо переместить
	 */
	public void moveProperties(TypePropertyGroup from, TypePropertyGroup to, List<TypeProperty<?>> propertiesToMove) {
		checkRequiredArgument(propertiesToMove, "propertiesToMove");

		if (Objects.equals(from, to)) {
			return;
		}

		Function<TypePropertyGroup, BiConsumer<TypeProperty<?>, Consumer<TypePropertyGroup>>> worker = group -> (
				property, consumer) -> ofNullable(group).ifPresent(consumer);

		propertiesToMove.forEach(property -> {
			worker.apply(from).accept(property, group -> group.removeProperty(property));
			worker.apply(to).accept(property, group -> group.addProperty(property));
		});

		Consumer<TypePropertyGroup> groupConsumer = group -> of(
				nonNull(group) ? group.getProperties() : getPropertiesWithoutGroup()).ifPresent(Ordinal::normalize);
		groupConsumer.accept(from);
		groupConsumer.accept(to);
	}

	/**
	 * Возвращает характеристики без группы (группы по-умолчанию)
	 * 
	 * @return характеристики без группы
	 */
	public List<TypeProperty<?>> getPropertiesWithoutGroup() {
		return propertyHolder.getPropertiesWithoutGroup();
	}

	/**
	 * Возвращает поддерживаемые характеристики
	 *
	 * @return поддерживаемые характеристики
	 */
	public Set<TypePropertyRef> getSupportedPropertyTypes() {
		if (isNull(supportedPropertyTypes)) {
			//@formatter:off
			supportedPropertyTypes = unmodifiableSet(ofNullable(getClass().getAnnotation(SupportedProperties.class))
					.map(SupportedProperties::value)
					.map(Sets::newHashSet)
					.orElse(newHashSet(TypePropertyRef.values())));
			//@formatter:on
		}
		return supportedPropertyTypes;
	}

	/**
	 * Проверяет, разрешена ли характеристика для данного типа
	 * 
	 * @param propertyClass
	 *            класс характеристики
	 * @return разрешена ли характеристика для данного типа
	 */
	public boolean isPropertyAllowed(Class<? extends TypeProperty<?>> propertyClass) {
		return getSupportedPropertyTypes().contains(TypePropertyRef.forClass(propertyClass));
	}

	/**
	 * Базовый запрос для поиска метаданных какого-либо типа. Все запросы любого типа должны наследоваться от этого
	 * класса
	 */
	public abstract static class TypeQuery<T extends Type> extends EntityQuery<T> {

		private EntityQueryStringFilter<T> name;
		private EntityQueryStringFilter<T> description;
		private EntityQueryStringFilter<T> keyword;
		private EntityQuerySimpleFilter<T, MetadataUnitStatus> status;

		public TypeQuery(Class<T> entityClass) {
			super(entityClass);
			name = createStringFilter(Type_.name);
			description = createStringFilter(Type_.description);
			keyword = createStringFilter(Type_.keyword);
			status = createFilter(Type_.status);
		}

		public EntityQuerySimpleFilter<T, MetadataUnitStatus> status() {
			return status;
		}

		public EntityQueryStringFilter<T> name() {
			return name;
		}

		public EntityQueryStringFilter<T> description() {
			return description;
		}

		public EntityQueryStringFilter<T> keyword() {
			return keyword;
		}
	}
}
