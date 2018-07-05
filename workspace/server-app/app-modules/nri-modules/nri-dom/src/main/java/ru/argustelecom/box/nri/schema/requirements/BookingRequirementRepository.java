package ru.argustelecom.box.nri.schema.requirements;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Репозиторий доступа к общей информации о требованиях к бронированию
 * Created by s.kolyada on 08.02.2018.
 */
@Repository
public class BookingRequirementRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Найти требование по id
	 *
	 * @param id идентификационный номер
	 * @return требование
	 */
	public ResourceRequirement findOne(Long id) {
		return em.find(ResourceRequirement.class, id);
	}
}
