package ru.argustelecom.box.nri.resources.lifecycle.model;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Репозиторий доступа к фазам ЖЦ
 * Created by s.kolyada on 08.11.2017.
 */
@Repository
public class ResourceLifecyclePhaseRepository implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Сервис доступа в БД
	 */
	@PersistenceContext
	private EntityManager em;

	/**
	 * Сервис генерации айдишников
	 */
	@Inject
	private IdSequenceService idSequenceService;

	/**
	 * Найти фазу по идентификатору
	 * @param id идентификатор
	 * @return фаза ЖЦ
	 */
	public ResourceLifecyclePhase findById(Long id) {
		return em.find(ResourceLifecyclePhase.class, id);
	}

	/**
	 * Создание фазы ЖЦ
	 * @param lifecycle ЖЦ
	 * @param newPhaseName имя новой фазы
	 * @return созданную фазу ЖЦ
	 */
	public ResourceLifecyclePhase createPhase(ResourceLifecycle lifecycle, String newPhaseName) {
		ResourceLifecyclePhase phase = ResourceLifecyclePhase.builder()
				.id(idSequenceService.nextValue(ResourceLifecyclePhase.class))
				.phaseName(newPhaseName)
				.currentLifecycle(lifecycle)
				.build();

		em.persist(phase);

		lifecycle.getPhases().add(phase);
		em.persist(lifecycle);

		return phase;
	}

	/**
	 * Обновить координаты фазы ЖЦ на графе
	 * @param id идентификатор ЖЦ
	 * @param x координата Х
	 * @param y координата У
	 * @return обновлённую фазу ЖЦ
	 */
	public ResourceLifecyclePhase updateCoordinates(Long id, String x, String y) {
		ResourceLifecyclePhase phase = findById(id);
		phase.setX(x);
		phase.setY(y);
		em.merge(phase);
		return phase;
	}

	/**
	 * Сохранить изменённую фазу ЖЦ
	 * @param phase фаза ЖЦ
	 * @return обновлённая фаза ЖЦ
	 */
	public ResourceLifecyclePhase savePhase(ResourceLifecyclePhase phase) {
		em.merge(phase);
		return phase;
	}

	/**
	 * Удалить фазу ЖЦ
	 * @param id идентификатор фазы
	 * @param newId новый идентификатор фазы для ресурсов
	 */
	public void remove(Long id, Long newId) {
		ResourceLifecyclePhase phase = findById(id);
		if (phase == null) {
			throw new IllegalStateException("Передан невалидный идентификатор фазы жизненного цикла " + id);
		}

		ResourceLifecyclePhase newPhase = findById(newId);
		if (newPhase == null) {
			throw new IllegalStateException("Передан невалидный идентификатор фазы жизненного цикла " + newId);
		}

		ResourceLifecycle lifecycle = phase.getCurrentLifecycle();
		lifecycle.getPhases().remove(phase);
		em.merge(lifecycle);

		for (ResourceLifecyclePhaseTransition outTransition : phase.getOutcomingPhases()) {
			ResourceLifecyclePhase outPhase = outTransition.getOutcomingPhase();
			outPhase.getIncomingPhases().remove(outTransition);
			em.merge(outPhase);
			em.remove(outTransition);
		}

		for (ResourceLifecyclePhaseTransition inTransition : phase.getIncomingPhases()) {
			ResourceLifecyclePhase inPhase = inTransition.getIncomingPhase();
			inPhase.getOutcomingPhases().remove(inTransition);
			em.merge(inPhase);
			em.remove(inTransition);
		}


		em.createQuery("update ResourceInstance r " +
						"set r.currentLifecyclePhase = :newPhase " +
						"where r.currentLifecyclePhase = :phase")
				.setParameter("phase", phase)
				.setParameter("newPhase", newPhase)
				.executeUpdate();

		em.remove(phase);
	}
}
