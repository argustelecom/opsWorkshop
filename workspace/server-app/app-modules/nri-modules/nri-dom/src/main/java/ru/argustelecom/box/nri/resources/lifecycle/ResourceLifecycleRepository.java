package ru.argustelecom.box.nri.resources.lifecycle;

import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecycle;
import ru.argustelecom.box.nri.resources.lifecycle.model.ResourceLifecyclePhase;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.box.nri.resources.spec.model.ResourceSpecification;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.util.List;

/**
 * Репозиторий доступа к ЖЦ
 * Created by s.kolyada on 02.11.2017.
 */
@Repository
public class ResourceLifecycleRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Заппрос на получаение всех фаз ЖЦ
	 */
	private static final String FIND_ALL_LIFECYCLE_PHASES = "ResourceLifecycleRepository.findAllPhases";

	/**
	 * Запрос на поиск всех спецификация ресурос, с данных ЖЦ
	 */
	private static final String FIND_ALL_RESSPECS_WITH_LIFECYCLE = "ResourceLifecycleRepository.findResourceSpecificationsWithLifecycle";

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Найти все ЖЦ
	 */
	public List<ResourceLifecycle> findAll(){
		return new ResourceLifecycle.ResourceLifecycleQuery().createTypedQuery(em).getResultList();
	}

	/**
	 * Найти ЖЦ по его идентификатору
	 */
	public ResourceLifecycle findById(Long id) {
		return em.find(ResourceLifecycle.class, id);
	}

	/**
	 * Получаение всех фаз ЖЦ
	 * @param lifecycle ЖЦ
	 * @return все фазы заданного ЖЦ
	 */
	@NamedQuery(name = FIND_ALL_LIFECYCLE_PHASES,
			query = "FROM ResourceLifecyclePhase p WHERE p.currentLifecycle = :lifecycle")
	public List<ResourceLifecyclePhase> findAllPhases(ResourceLifecycle lifecycle) {
		return em.createNamedQuery(FIND_ALL_LIFECYCLE_PHASES, ResourceLifecyclePhase.class)
				.setParameter("lifecycle", lifecycle)
				.getResultList();
	}

	/**
	 * Поиск всех спецификаций ресурсов, с заданным ЖЦ
	 * @param lifecycle ЖЦ
	 * @return список спецификация ресурсов
	 */
	@NamedQuery(name = FIND_ALL_RESSPECS_WITH_LIFECYCLE,
			query = "FROM ResourceSpecification spec WHERE spec.lifecycle = :lifecycle")
	public List<ResourceSpecification> findResourceSpecificationsWithLifecycle(ResourceLifecycle lifecycle) {
		return em.createNamedQuery(FIND_ALL_RESSPECS_WITH_LIFECYCLE, ResourceSpecification.class)
				.setParameter("lifecycle", lifecycle)
				.getResultList();
	}

	/**
	 * Обновить информацию о текущей фазе ЖЦ ресурса
	 * @param resourceInstance  ресурс, у которого обновляем фазу
	 * @param newPhase новая фаза ЖЦ
	 * @return обновлённый ресурс
	 */
	public ResourceInstance updateResourcePhase(ResourceInstance resourceInstance, ResourceLifecyclePhase newPhase) {
		resourceInstance.setCurrentLifecyclePhase(newPhase);
		em.persist(resourceInstance);
		return resourceInstance;
	}

	/**
	 * Обновить изначальный статус ЖЦ
	 * @param lifecycleId идентификатор ЖЦ
	 * @param phaseId идентификатор фазы ЖЦ
	 * @return обновлённый ЖЦ
	 */
	public ResourceLifecycle updateInitialPhase(Long lifecycleId, Long phaseId) {
		ResourceLifecycle lifecycle = findById(lifecycleId);
		if (lifecycle == null) {
			throw new IllegalStateException("Жизненный цикл не найден по идентификатору " + lifecycleId);
		}

		ResourceLifecyclePhase phase = em.find(ResourceLifecyclePhase.class, phaseId);
		if (phase == null) {
			throw new IllegalStateException("Фаза жизненного цикла не найдена по идентификатору " + phaseId);
		}

		lifecycle.setInitialPhase(phase);
		em.merge(lifecycle);
		return lifecycle;
	}
}
