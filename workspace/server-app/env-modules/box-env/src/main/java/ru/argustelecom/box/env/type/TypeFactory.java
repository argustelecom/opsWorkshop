package ru.argustelecom.box.env.type;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.type.event.qualifier.UniqueMode.DISABLE;
import static ru.argustelecom.box.env.type.event.qualifier.UniqueMode.ENABLE;
import static ru.argustelecom.box.env.type.model.TypeCreationalContext.creationalContext;
import static ru.argustelecom.box.env.type.model.TypeProperty.SUPPORT_UNIQUE_PROPERTY_CLASSES;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.utils.CDIHelper.fireEvent;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.type.event.UniqueEvent;
import ru.argustelecom.box.env.type.event.qualifier.UniqueMode;
import ru.argustelecom.box.env.type.event.qualifier.literal.MakeUniqueLiteral;
import ru.argustelecom.box.env.type.model.SupportUniqueProperty;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypeCreationalContext;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeInstanceDerivative;
import ru.argustelecom.box.env.type.model.TypeInstanceSpec;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyAccessor;
import ru.argustelecom.box.env.type.model.TypePropertyFilterContainer;
import ru.argustelecom.box.env.type.model.TypePropertyGroup;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.box.inf.security.SecurityContext;
import ru.argustelecom.box.inf.service.DomainService;

/**
 * Фабрика фреймворка EAV. Предназанчена для использования в прикладном коде. Обеспечивает создание новых типов, свойств
 * в этих типах, групп свойств и экземпляров типов. Обеспечивает правильное генерирование идентификаторов для метаданных
 * и для данных.
 */
@DomainService
public class TypeFactory implements Serializable {

	private static final long serialVersionUID = -2471392914495382683L;

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private SecurityContext security;

	/**
	 * Создает новый тип данных для указанного Java класса.
	 * 
	 * @param typeClass
	 *            - Java класс нового типа данных
	 * 
	 * @return новый тип данных
	 */
	public <T extends Type> T createType(Class<T> typeClass) {
		TypeCreationalContext<T> ctx = creationalContext(typeClass);
		Long typeId = MetadataUnit.generateId(em);
		return ctx.createType(typeId);
	}

	/**
	 * Создает новое свойство в указанном типе данных. Обеспечивает правильное генерирование идентификатора и дефолтного
	 * ключевого слова для создаваемого свойства. Созданное свойство не нужно сохранять в контексте персистенции, это
	 * сохранение будет выполнено автоматически по правилам каскадности отношений.
	 * 
	 * @param propertyOwner
	 *            - владелец создаваемого свойства
	 * @param propertyClass
	 *            - Java класс создаваемого свойства
	 * 
	 * @return созданное свойство, всегда новое.
	 */
	public <T extends Type, P extends TypeProperty<?>> P createProperty(T propertyOwner, Class<P> propertyClass) {
		TypeCreationalContext<T> ctx = creationalContext(propertyOwner);
		Long propertyId = MetadataUnit.generateId(em);
		String propertyKeyword = MetadataUnit.generateKeyword(propertyClass, propertyId);
		return ctx.createProperty(propertyOwner, propertyClass, propertyKeyword, propertyId);
	}

	/**
	 * Создает новое или находит старое и удаленное свойство по его ключевому слову. Здесь возможно несколько сценариев:
	 * <ul>
	 * <li>Если найденное по ключевому слову свойство активно, то будет ошибка уникальности.
	 * <li>Если найденное по ключевому слову свойство не активно (т.е. deprecated) и Java класс этого удаленного
	 * свойства соответствует указанному в этом методе, то свойство будет вновь активировано и им можно будет
	 * пользоваться снова. Однако же если Java класс не соответствует, то будет сгенерировано исключение.
	 * <li>Если свойство по указанному ключевому слову не найдено, то будет создано новое свойство указанного Java
	 * класса.
	 * </ul>
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий создаваемым свойством
	 * @param propertyClass
	 *            - Java класс создаваемого свойства
	 * @param propertyKeyword
	 *            - ключевое слово создаваемого свойства
	 * 
	 * @return созданное свойство, может быть как новым, так и восстановленным после удаления
	 */
	public <T extends Type, P extends TypeProperty<?>> P createProperty(T propertyOwner, Class<P> propertyClass,
			String propertyKeyword) {

		TypeCreationalContext<T> ctx = creationalContext(propertyOwner);
		Long propertyId = MetadataUnit.generateId(em);
		return ctx.createProperty(propertyOwner, propertyClass, propertyKeyword, propertyId);
	}

	/**
	 * То же, что и createProperty(propertyOwner, propertyClass), только после создания новое свойство будет добавлено в
	 * указанную группу
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий создаваемым свойством
	 * @param propertyGroup
	 *            - группа, в которую необходимо добавить созданное свойство
	 * @param propertyClass
	 *            - Java класс создаваемого свойства
	 * 
	 * @return созданное свойство, всегда новое.
	 */
	public <T extends Type, P extends TypeProperty<?>> P createProperty(T propertyOwner,
			TypePropertyGroup propertyGroup, Class<P> propertyClass) {

		TypeCreationalContext<T> ctx = creationalContext(propertyOwner);
		Long propertyId = MetadataUnit.generateId(em);
		String propertyKeyword = MetadataUnit.generateKeyword(propertyClass, propertyId);
		return ctx.createProperty(propertyOwner, propertyGroup, propertyClass, propertyKeyword, propertyId);
	}

	/**
	 * То же, что и createProperty(propertyOwner, propertyClass, propertyKeyword), только после создания новое свойство
	 * (или старое свойство после восстановления) будет добавлено в указанную группу
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий создаваемым свойством
	 * @param propertyGroup
	 *            - группа, в которую необходимо добавить созданное свойство
	 * @param propertyClass
	 *            - Java класс создаваемого свойства
	 * @param propertyKeyword
	 *            - ключевое слово создаваемого свойства
	 * 
	 * @return созданное свойство, может быть как новым, так и восстановленным после удаления
	 */
	public <T extends Type, P extends TypeProperty<?>> P createProperty(T propertyOwner,
			TypePropertyGroup propertyGroup, Class<P> propertyClass, String propertyKeyword) {

		TypeCreationalContext<T> ctx = creationalContext(propertyOwner);
		Long propertyId = MetadataUnit.generateId(em);
		return ctx.createProperty(propertyOwner, propertyGroup, propertyClass, propertyKeyword, propertyId);
	}

	/**
	 * Выполняет логическое удаление указанного свойства из указанного типа. После логического удаления свойство
	 * становится deprecated, выносится из группы, в которой оно находилось и недоступно для заполнения пользователем
	 * при создании экземпляров типов
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий свойством
	 * @param propertyKeyword
	 *            - ключевое слово удаляемого свойства
	 */
	public <T extends Type> void removeProperty(T propertyOwner, String propertyKeyword) {
		creationalContext(propertyOwner).removeProperty(propertyOwner, propertyKeyword);
	}

	/**
	 * Выполняет логическое удаление указанного свойства из указанного типа. После логического удаления свойство
	 * становится deprecated, выносится из группы, в которой оно находилось и недоступно для заполнения пользователем
	 * при создании экземпляров типов
	 * 
	 * @param propertyOwner
	 *            - тип, владеющий свойством
	 * @param property
	 *            - удаляемое свойство
	 */
	public <T extends Type, P extends TypeProperty<?>> void removeProperty(T propertyOwner, P property) {
		creationalContext(propertyOwner).removeProperty(propertyOwner, property);
	}

	/**
	 * Создает в указанном типе новую группу свойств, по умолчанию - пустую. Если группа с указанным именем уже
	 * существует, то новая группа не будет создана, вместо этого будет возвращена ссылка на уже имеющуюся группу.
	 * Аналогично ситуации со свойствами, создаваему группу не нужно явно сохранять в контексте персистенции, это будет
	 * выполнено автоматически по правилам каскадности отношений
	 * 
	 * @param groupOwner
	 *            - тип, владеющий группой
	 * @param groupName
	 *            - наименование группы
	 * 
	 * @return созданную группу свойств
	 */
	public <T extends Type> TypePropertyGroup createPropertyGroup(T groupOwner, String groupName) {
		TypeCreationalContext<T> ctx = creationalContext(groupOwner);
		Long groupId = MetadataUnit.generateId(em);
		return ctx.createPropertyGroup(groupOwner, groupName, groupId);
	}

	/**
	 * Создает в указанном типе новую группу свойств, по умолчанию - пустую. Если группа с указанным именем уже
	 * существует, то новая группа не будет создана, вместо этого будет возвращена ссылка на уже имеющуюся группу.
	 * Аналогично ситуации со свойствами, создаваему группу не нужно явно сохранять в контексте персистенции, это будет
	 * выполнено автоматически по правилам каскадности отношений
	 *
	 * @param groupOwner
	 *            - тип, владеющий группой
	 * @param groupName
	 *            - наименование группы
	 * @param ordinalNumber
	 *            - порядковый номер группы
	 *
	 * @return созданную группу свойств
	 */
	public <T extends Type> TypePropertyGroup createPropertyGroup(T groupOwner, String groupName,
			Integer ordinalNumber) {
		TypeCreationalContext<T> ctx = creationalContext(groupOwner);
		Long groupId = MetadataUnit.generateId(em);
		return ctx.createPropertyGroup(groupOwner, groupName, groupId, ordinalNumber);
	}

	/**
	 * Удаляет указанную группу из типа. Удаление группы возможно только в том случае, если в этой группе не содержится
	 * активных свойств. Удаление группы выполняется физически, т.е. группа реально будет удалена из типа
	 * 
	 * @param groupOwner
	 *            - тип, владеющий группой
	 * @param group
	 *            - группа для удаления
	 * 
	 * @return true, если группу удалось удалить
	 */
	public <T extends Type> boolean removePropertyGroup(T groupOwner, TypePropertyGroup group) {
		return creationalContext(groupOwner).removePropertyGroup(groupOwner, group);
	}

	/**
	 * Создает экземпляр указанного типа, обеспечивает генерирование идентификатора для этого экземпляра
	 * 
	 * @param instanceType
	 *            - тип, для которого нужно создать экземпляр
	 * @param instanceClass
	 *            - Java класс экземпляра
	 * 
	 * @return созданный и проинициализированный экземпляр указанного типа данных
	 */
	public <T extends Type, I extends TypeInstance<T>> I createInstance(T instanceType, Class<I> instanceClass) {
		checkRequiredArgument(instanceClass, "instanceClass");
		Long instanceId = idSequence.nextValue(instanceClass);
		return createInstance(instanceType, instanceClass, instanceId);
	}

	/**
	 * Создает экземпляр указанного типа, требует явного указания идентификатора
	 * 
	 * @param instanceType
	 *            - тип, для которого нужно создать экземпляр
	 * @param instanceClass
	 *            - Java класс экземпляра
	 * @param instanceId
	 *            - идентификатор экземпляра
	 * 
	 * @return созданный и проинициализированный экземпляр указанного типа данных
	 */
	public <T extends Type, I extends TypeInstance<T>> I createInstance(T instanceType, Class<I> instanceClass,
			Long instanceId) {

		return creationalContext(instanceType).createInstance(instanceType, instanceClass, instanceId);
	}

	/**
	 * Создает экземпляр указанного типа по его прототипу, обеспечивает генерирование идентификатора для создаваемого
	 * экземпляра. Копирует из прототипа в экземпляр значения свойств, указанных явно и не являющихся при этом
	 * значениями по умолчанию. Свойства, унаследованные от прототипа, становятся заблокированными и их изменение
	 * возможно только при помощи привилегированного режима
	 * 
	 * @param prototype
	 *            - прототип для создания нового производного экземпляра
	 * @param instanceClass
	 *            - Java класс производного экземпляра
	 * 
	 * @return созданный и проинициализированный экземпляр указанного типа
	 */
	public <T extends Type, S extends TypeInstance<T> & TypeInstanceSpec<T>, D extends TypeInstance<T> & TypeInstanceDerivative<T, S>> D createInstanceByProto(
			S prototype, Class<D> instanceClass) {

		checkRequiredArgument(instanceClass, "instanceClass");
		Long instanceId = idSequence.nextValue(instanceClass);
		return creationalContext(prototype.getType()).createInstanceByProto(prototype, instanceClass, instanceId);
	}

	/**
	 * Создает экземпляр указанного типа по его прототипу, требует явного указания адекватного идентификатора для
	 * создаваемого экземпляра. Копирует из прототипа в экземпляр значения свойств, указанных явно и не являющихся при
	 * этом значениями по умолчанию. Свойства, унаследованные от прототипа, становятся заблокированными и их изменение
	 * возможно только при помощи привилегированного режима
	 * 
	 * @param prototype
	 *            - прототип для создания нового производного экземпляра
	 * @param instanceClass
	 *            - Java класс производного экземпляра
	 * @param instanceId
	 *            - идентификатор экземпляра
	 * 
	 * @return созданный и проинициализированный экземпляр указанного типа
	 */
	public <T extends Type, S extends TypeInstance<T> & TypeInstanceSpec<T>, D extends TypeInstance<T> & TypeInstanceDerivative<T, S>> D createInstanceByProto(
			S prototype, Class<D> instanceClass, Long instanceId) {

		return creationalContext(prototype.getType()).createInstanceByProto(prototype, instanceClass, instanceId);
	}

	/**
	 * Создает PropertyAccessor для указанной пары "экземпляр - свойство". Определяет, обладает ли текущий пользователь
	 * привилегией редактирования значения свойств, заблокированных на уровне эуказанного экземпляра. Знание о
	 * привилегированности пользователя распространяется на создаваемый PropertyAccessor.
	 * 
	 * @param instance
	 *            - некоторый экземпляр какого-то типа данных
	 * @param property
	 *            - некоторое свойство, объявленное на уровне того же типа, что и указанный экземпляр
	 * 
	 * @return PropertyAccessor для доступа к значению указанного свойства в контексте указанного экземпляра
	 */
	public <V, P extends TypeProperty<V>, T extends Type, I extends TypeInstance<T>> TypePropertyAccessor<V> createAccessor(
			I instance, P property) {

		checkRequiredArgument(instance, "instance");
		checkRequiredArgument(property, "property");

		return creationalContext(instance.getType()).createAccessor(instance, property, canModifyLockedProperties());
	}

	/**
	 * Создает коллекцию PropertyAccessor для всех свойст, объявленных в типе указанного экземпляра. Каждый созданный
	 * PropertyAccessor инициализируется аналогично {@link #createAccessor(TypeInstance, TypeProperty)}
	 * 
	 * @param instance
	 *            - некоторый экземпляр какого-то типа данных
	 * 
	 * @return PropertyAccessor для доступа к значению указанного свойства в контексте указанного экземпляра
	 */
	public <T extends Type, I extends TypeInstance<T>> List<TypePropertyAccessor<?>> createAccessors(I instance) {
		checkRequiredArgument(instance, "instance");

		TypeCreationalContext<T> ctx = creationalContext(instance.getType());
		boolean privileged = canModifyLockedProperties();
		T type = instance.getType();

		//@formatter:off
		return Collections.unmodifiableList(type.getProperties().stream()
			.map(property -> ctx.createAccessor(instance, property, privileged))
			.collect(toList())
		);
		//@formatter:on
	}

	/**
	 * Делает переданное свойство уникальным для всех экземпляров переданного класса TypeInstance
	 *
	 * @param typeClass
	 *            тип, для всех экземпляров которого необходимо установить уникальность свойства
	 * @param property
	 *            свойство, которое должно стать уникальным
	 */
	public void makePropertyUnique(Class<? extends Type> typeClass, TypeProperty<?> property) {
		firePropertyUniqueEvent(typeClass, property, ENABLE);
	}

	/**
	 * Снимает уникальность с переданного свойства для всех экземпляров переданного класса TypeInstance
	 * 
	 * @param typeClass
	 *            тип, для всех экземпляров которого необходимо снять уникальность свойства
	 * @param property
	 *            свойство, которое должно стать неуникальным
	 */
	public void unmakePropertyUnique(Class<? extends Type> typeClass, TypeProperty<?> property) {
		firePropertyUniqueEvent(typeClass, property, DISABLE);
	}

	public <T extends Type> TypePropertyFilterContainer createFilterContainer(List<T> types) {
		TypePropertyFilterContainer result = new TypePropertyFilterContainer();
		for (T type : types) {
			type.getProperties().stream().filter(TypeProperty::isFiltered).forEach(property -> {
				result.addProperty(type, property);
			});
		}
		return result;
	}

	private void firePropertyUniqueEvent(Class<? extends Type> typeClass, TypeProperty<?> property, UniqueMode mode) {
		checkRequiredArgument(typeClass, "typeClass");
		checkRequiredArgument(property, "property");
		checkState(SUPPORT_UNIQUE_PROPERTY_CLASSES.contains(property.getClass()), "Unsupported property");
		checkState(typeClass.isAnnotationPresent(SupportUniqueProperty.class),
				"Type does not support unique properties");

		if (ENABLE.equals(mode) == property.isUnique()) {
			return;
		}

		fireEvent(new UniqueEvent(property), new MakeUniqueLiteral(typeClass, mode));
	}

	/**
	 * true, если текущий пользователь обладает привилегией редактирования заблокированных свойств
	 */
	private boolean canModifyLockedProperties() {
		return security.granted("System_LockedTypePropertiesEdit");
	}
}