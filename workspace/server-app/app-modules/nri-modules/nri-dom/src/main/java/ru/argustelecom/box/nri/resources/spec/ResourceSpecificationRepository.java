package ru.argustelecom.box.nri.resources.spec;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Репозиторий спецификаций ресурса
 * @author d.khekk
 * @since 22.09.2017
 */
@Repository
public class ResourceSpecificationRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Найти спецификацию ресурса
	 * @param id айди спецификации
	 * @return найденная спецификация
	 */
	public ResourceSpecification findOne(Long id) {
		return em.find(ResourceSpecification.class, id);
	}

	/**
	 * найти все спецификации ресурсов
	 * @return все спецификации ресурсов
	 */
	public List<ResourceSpecification> findAll() {
		return em.createQuery("from ResourceSpecification", ResourceSpecification.class).getResultList();
	}
}
