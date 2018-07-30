package ru.argustelecom.box.env.type;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static ru.argustelecom.box.env.type.model.TypeProperty.SUPPORT_UNIQUE_PROPERTY_CLASSES;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.box.inf.utils.ReflectionUtils.extractAnnotation;

import java.math.BigInteger;
import java.util.List;
import java.util.Set;

import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import ru.argustelecom.box.env.type.event.TypeInstanceEvent;
import ru.argustelecom.box.env.type.event.qualifier.OnChange;
import ru.argustelecom.box.env.type.event.qualifier.OnRemove;
import ru.argustelecom.box.env.type.model.IndexTable;
import ru.argustelecom.box.env.type.model.InstanceTable;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeInstanceDescriptor;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.inf.modelbase.SequenceDefinition;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

/**
 * Доменный сервис, предназначенный для решения следующих глобальных задач
 * <ul>
 * <li>Ответа на вопрос, является ли значение свойства уникальным (для свойств и типов, поддерживающих уникальность)
 * <li>Ответа на вопрос, какие именно экземпляры типа нарушают уникальность
 * <li>Обработчик триггера на изменение свойств экземпляра. Для уникальных свойств поддерживает индекс уникальности
 * </ul>
 * 
 * Т.к. этот сервис слушает JPA события, которые возникают в момент флаша EntityManager, то на этот класс накладываются
 * дополнительные ограничения: этот класс не должен получать доступ к RequestScoped бинам. Если сущность меняется
 * вследствие обработчика очереди или при вызове из веб-сервиса, т.е. в случаях, когда транзакцией управляет контейнер,
 * то флаш будет производиться в момент уже закрытого RequestScoped после выхода из EJB слоя. см BOX-2353 и более
 * подробный древний TASK-57689 с детализированным рабором ситуации
 */
@DomainService
public class TypeInstanceService {

	private static final String IS_VALUE_UNIQUE = "TypeInstanceService.isValueUnique";
	private static final String FIND_DUPLICATES = "TypeInstanceService.findInstancesWithDuplicates";
	private static final String ON_CHANGE = "TypeInstanceService.onPersist";
	private static final String ON_REMOVE = "TypeInstanceService.onRemove";

	@PersistenceContext
	private EntityManager em;

	//@formatter:off
	
	/**
	 * Проверяет, является ли переданное значение уникальным для свойства
	 *
	 * @param instance
	 *            - экземпляр типа
	 * @param property
	 *            - свойство, для которого проверяется уникальность
	 * @param value
	 *            - новое значение свойства
	 * 
	 * @return является ли новое значение уникальным для данного свойства
	 */
	@NamedNativeQuery(name = IS_VALUE_UNIQUE, query = 
		"SELECT system.is_value_unique(:indexSchema, :indexTable, :instanceId, :propertyId, :value)"
	)
	public boolean isValueUnique(TypeInstance<?> instance, TypeProperty<?> property, Object value) {
		checkRequiredArgument(instance, "instance");
		checkRequiredArgument(property, "property");

		if (value == null) {
			return true;
		}

		IndexTable indexTable = extractAnnotation(instance.getClass(), TypeInstanceDescriptor.class).indexTable();

		return (boolean) em.createNamedQuery(IS_VALUE_UNIQUE)
			.setParameter("indexSchema", indexTable.schema())
			.setParameter("indexTable", indexTable.table())
			.setParameter("instanceId", instance.getId())
			.setParameter("propertyId", property.getId())
			.setParameter("value", value.toString())
			.getSingleResult();
	}

	/**
	 * Возвращает список id для TypeInstance, у которых значение свойства повторяется
	 *
	 * @param instanceClass
	 *            тип TypeInstance, для когорого проверяется уникальность значений для переданного свойства
	 * @param property
	 *            свойство, у которого проверяется значения на уникальность
	 * @return список идентификаторов экземпляров TypeInstance, у которых значение переданного свойства неуникально
	 */
	@NamedNativeQuery(name = FIND_DUPLICATES, query = 
		"SELECT system.find_instance_with_duplicate_values(:schema, :table, :idColumn, :propColumn, :propId)"
	)
	public List<Long> findInstancesWithDuplicates(Class<? extends TypeInstance<?>> instanceClass,
			TypeProperty<?> property) {
		
		checkRequiredArgument(property, "property");
		checkArgument(SUPPORT_UNIQUE_PROPERTY_CLASSES.contains(property.getClass()), "Unsupported property");

		InstanceTable instanceTable = extractAnnotation(instanceClass, TypeInstanceDescriptor.class).instanceTable();
		
		@SuppressWarnings("unchecked")
		List<BigInteger> resultRaw = em.createNamedQuery(FIND_DUPLICATES)
				.setParameter("schema", instanceTable.schema())
				.setParameter("table", instanceTable.table())
				.setParameter("idColumn", instanceTable.idColumn())
				.setParameter("propColumn", instanceTable.propsColumn())
				.setParameter("propId", property.getQualifiedName())
				.getResultList();
		
		return resultRaw.stream().map(BigInteger::longValue).collect(toList());
	}

	@NamedNativeQuery(name = ON_CHANGE, query 
		= "SELECT system.property_index("
		+ "  :indexSchema, :indexTable, :sequenceName, :instanceId, :propertyId, CAST (:value AS VARCHAR)"
		+ ")"
	)
	void onChange(@Observes @OnChange TypeInstanceEvent event) {
		TypeInstance<?> instance = event.getInstance();

		IndexTable indexTable = extractAnnotation(instance.getClass(), TypeInstanceDescriptor.class).indexTable();
		SequenceDefinition sequenceDefinition = extractAnnotation(instance.getClass(), SequenceDefinition.class);

		Query query = em.createNamedQuery(ON_CHANGE);

		getSupportedProperties(instance).forEach(property ->
			query.setParameter("indexSchema", indexTable.schema())
					.setParameter("indexTable", indexTable.table())
					.setParameter("sequenceName", sequenceDefinition.name())
					.setParameter("instanceId", instance.getId())
					.setParameter("propertyId", property.getId())
					.setParameter("value", property.getAsString(instance))
					.getSingleResult()
		);

	}

	@NamedNativeQuery(name = ON_REMOVE, query = 
		"SELECT system.remove_property_index(:indexSchema, :indexTable, :instanceId)"
	)
	void onRemove(@Observes @OnRemove TypeInstanceEvent event) {
		TypeInstance<?> instance = event.getInstance();

		IndexTable indexTable = extractAnnotation(instance.getClass(), TypeInstanceDescriptor.class).indexTable();

		em.createNamedQuery(ON_REMOVE)
				.setParameter("indexSchema", indexTable.schema())
				.setParameter("indexTable", indexTable.table())
				.setParameter("instanceId", instance.getId())
				.getSingleResult();
	}

	private Set<? extends TypeProperty<?>> getSupportedProperties(TypeInstance<?> instance) {
		return instance.getType().getProperties().stream()
			.filter(typeProperty -> SUPPORT_UNIQUE_PROPERTY_CLASSES.contains(typeProperty.getClass()))
			.filter(TypeProperty::isUnique)
			.collect(toSet());
	}
}