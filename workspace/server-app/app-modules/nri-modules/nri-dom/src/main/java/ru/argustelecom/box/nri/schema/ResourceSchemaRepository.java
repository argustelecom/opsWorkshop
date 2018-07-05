package ru.argustelecom.box.nri.schema;

import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Репозиторий доступа к хранилищу схем подключений
 *
 * @author s.kolyada
 * @since 04.10.2017
 */
@Repository
public class ResourceSchemaRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис генерации ID
	 */
	@Inject
	private IdSequenceService idSequenceService;
	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Поиск по идентификатьору
	 *
	 * @param id идентификатор
	 * @return схема
	 */
	public ResourceSchema findById(Long id) {
		return em.find(ResourceSchema.class, id);
	}

	/**
	 * Найти по спецификации услуги
	 *
	 * @param serviceSpecification спецификация услуги
	 * @return список схем подключения
	 */
	public List<ResourceSchema> findByServiceSpecification(ServiceSpec serviceSpecification) {
		try {
			ResourceSchema.ResourceSchemaQuery query = new ResourceSchema.ResourceSchemaQuery();
			query.and(query.serviceSpecification().equal(serviceSpecification));
			return query.createTypedQuery(em).getResultList();
		} catch (NoResultException e) {
			return Collections.emptyList();
		}
	}


	/**
	 * Создать схему
	 *
	 * @param name имя схемы
	 * @param ss   Спецификация службы
	 * @return схема
	 */
	public ResourceSchema create(String name, ServiceSpec ss) {

		ResourceSchema schema = ResourceSchema.builder().id(idSequenceService.nextValue(ResourceSchema.class)).serviceSpecification(ss).name(name).build();
		em.persist(schema);
		return schema;
	}

	/**
	 * Удалить схему
	 *
	 * @param id идентификатор схемы
	 */
	public void delete(Long id) {
		em.remove(findById(id));
	}

	/**
	 * Сохранение изменений
	 *
	 * @param schema изменяемое щзначение
	 */
	public void save(ResourceSchema schema) {
		em.merge(schema);
	}
}
