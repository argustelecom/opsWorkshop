package ru.argustelecom.box.nri.resources;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.resources.spec.model.ParameterSpecification;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Репозиторий ресурсов
 *
 * @author d.khekk
 * @since 21.09.2017
 */
@Repository
public class ParameterSpecificationRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Найти ресурс
	 * @param id айди ресурса
	 * @return найденный ресурс
	 */
	public ParameterSpecification findOne(Long id) {
		return em.find(ParameterSpecification.class, id);
	}

	/**
	 * Найти все спецификации параметров
	 *
	 * @return список
	 */
	public List<ParameterSpecification> findAll(){
		return new ParameterSpecification.ParameterSpecificationTypeQuery().createTypedQuery(em).getResultList();
	}
}
