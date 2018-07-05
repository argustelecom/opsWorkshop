package ru.argustelecom.box.nri.schema.requirements.resources;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.schema.requirements.resources.model.RequiredItem;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import javax.annotation.Nonnull;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * репозиторий для работы с требованиями
 * b.bazarov
 */
@Repository
public class RequiredItemRepository implements Serializable {

	private static final long serialVersionUID = 1L;
	/**
	 * служба
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Создать схему
	 *
	 * @param schema имя схемы
	 * @param spec   Спецификация
	 * @return требование
	 */
	public RequiredItem create(@Nonnull ResourceSpecification spec, @Nonnull ResourceSchema schema) {

		RequiredItem item = RequiredItem.builder().id(idSequenceService.nextValue(RequiredItem.class)).resourceSchema(schema).resourceSpecification(spec).build();
		em.persist(item);
		schema.addRequirement(item);
		em.merge(schema);
		return item;
	}


	/**
	 * Создать схему
	 *
	 * @param parent родительское требование
	 * @param spec   Спецификация
	 * @return требование
	 */
	public RequiredItem create(ResourceSpecification spec, RequiredItem parent) {

		RequiredItem item = RequiredItem.builder().id(idSequenceService.nextValue(RequiredItem.class))
				.resourceSpecification(spec).build();
		em.persist(item);
		parent.addChild(item);
		em.merge(parent);
		return item;
	}


	/**
	 * Удалить требование
	 *
	 * @param item требование
	 */
	public void delete(RequiredItem item) {
		if (item != null) {
			RequiredItem parent = item.getParent();
			if (parent != null) {
				parent.removeChild(item);
				em.merge(parent);
			}
		}
		em.remove(item);
	}

	/**
	 * Найти требование по идентификатору
	 *
	 * @param id идентификатор
	 * @return треование
	 */
	public RequiredItem findById(Long id) {
		return em.find(RequiredItem.class, id);
	}
}
