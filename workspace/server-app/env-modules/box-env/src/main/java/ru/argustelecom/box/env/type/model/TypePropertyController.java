package ru.argustelecom.box.env.type.model;

import static ru.argustelecom.box.env.type.event.qualifier.UniqueMode.DISABLE;
import static ru.argustelecom.box.env.type.event.qualifier.UniqueMode.ENABLE;
import static ru.argustelecom.box.inf.nls.LocaleUtils.getMessages;
import static ru.argustelecom.box.inf.utils.ReflectionUtils.extractAnnotation;

import java.util.List;

import javax.enterprise.event.Observes;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.type.event.DelegateUniqueEvent;
import ru.argustelecom.box.env.type.event.qualifier.MakeUniqueDelegate;
import ru.argustelecom.box.env.type.nls.TypeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.SequenceDefinition;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

@DomainService
public class TypePropertyController {

	private static final String MAKE_PROPERTY_UNIQUE = "TypePropertyController.makePropertyUnique";
	private static final String UNMAKE_PROPERTY_UNIQUE = "TypePropertyController.unmakePropertyUnique";

	@PersistenceContext
	private EntityManager em;

	@NamedNativeQuery(name = MAKE_PROPERTY_UNIQUE, query = "SELECT system.make_property_unique"
			+ "(:indexSchema, :indexTable, :sequenceName, :instanceSchema, "
			+ ":instanceTable, :idColumn, :propsColumn, :propId, :propQualifier)")
	void makePropertyUnique(@Observes @MakeUniqueDelegate(ENABLE) DelegateUniqueEvent event) {
		Class<? extends TypeInstance<?>> instanceClass = event.getInstanceClass();
		TypeProperty<?> property = event.getProperty();

		TypeInstanceDescriptor instanceDescriptor = extractAnnotation(instanceClass, TypeInstanceDescriptor.class);
		SequenceDefinition sequenceDefinition = extractAnnotation(instanceClass, SequenceDefinition.class);
		IndexTable indexTable = instanceDescriptor.indexTable();
		InstanceTable instanceTable = instanceDescriptor.instanceTable();

		//@formatter:off
		List<?> duplicateIds = em.createNamedQuery(MAKE_PROPERTY_UNIQUE)
				.setParameter("indexSchema", indexTable.schema())
				.setParameter("indexTable", indexTable.table())
				.setParameter("sequenceName", sequenceDefinition.name())
				.setParameter("instanceSchema", instanceTable.schema())
				.setParameter("instanceTable", instanceTable.table())
				.setParameter("idColumn", instanceTable.idColumn())
				.setParameter("propsColumn", instanceTable.propsColumn())
				.setParameter("propId", property.getId())
				.setParameter("propQualifier", property.getQualifiedName())
				.getResultList();
		//@formatter:on

		if (!duplicateIds.isEmpty()) {
			throw getMessages(TypeMessagesBundle.class).duplicateTypeInstancePropertyValues(
					instanceClass.getSimpleName(), property.getObjectName(), duplicateIds.toString());
		}

		property.setUnique(true);
	}

	@NamedNativeQuery(name = UNMAKE_PROPERTY_UNIQUE, query = "SELECT system.unmake_property_unique"
			+ "(:indexSchema, :indexTable, :propertyId)")
	void unmakePropertyUnique(@Observes @MakeUniqueDelegate(DISABLE) DelegateUniqueEvent event) {
		Class<? extends TypeInstance<?>> instanceClass = event.getInstanceClass();
		TypeProperty<?> property = event.getProperty();

		IndexTable indexTable = extractAnnotation(instanceClass, TypeInstanceDescriptor.class).indexTable();

		//@formatter:off
		em.createNamedQuery(UNMAKE_PROPERTY_UNIQUE)
				.setParameter("indexSchema", indexTable.schema())
				.setParameter("indexTable", indexTable.table())
				.setParameter("propertyId", property.getId())
				.getResultList();
		//@formatter:on

		property.setUnique(false);
	}
}
