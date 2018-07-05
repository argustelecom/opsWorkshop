package ru.argustelecom.box.nri.service;

import ru.argustelecom.box.env.commodity.model.ServiceSpec;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.service.nls.ServiceSpecificationRepositoryMessagesBundle;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import java.io.Serializable;
import java.util.List;
import java.util.Optional;

/**
 * Репозиторий доступа к хранилищу спецификаций услуг
 *
 * @author d.khekk
 * @since 09.10.2017
 */
@Repository
public class ServiceSpecificationRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Сервис генерации ID
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Найти все спецификации услуг
	 *
	 * @return список всех спецификаций
	 */
	public List<ServiceSpec> findAll() {
		CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		CriteriaQuery<ServiceSpec> query = criteriaBuilder.createQuery(ServiceSpec.class);
		query.from(ServiceSpec.class);
		return em.createQuery(query).getResultList();
	}


	/**
	 * Найти спецификаци.
	 *
	 * @param id ID нужной спецификации
	 * @return найденная спецификация
	 */
	public ServiceSpec findOne(Long id) {
		ServiceSpecificationRepositoryMessagesBundle messages = LocaleUtils.getMessages(ServiceSpecificationRepositoryMessagesBundle.class);
		return Optional.ofNullable(em.find(ServiceSpec.class, id))
				.orElseThrow(() -> new IllegalArgumentException(messages.objectWithId() + id + messages.didNotFind()));
	}
}
