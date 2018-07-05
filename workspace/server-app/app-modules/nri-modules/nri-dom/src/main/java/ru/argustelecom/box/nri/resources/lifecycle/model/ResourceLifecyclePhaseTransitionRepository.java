package ru.argustelecom.box.nri.resources.lifecycle.model;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.inf.service.Repository;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;

/**
 * Репозиторий доступа к переходам между фазами ЖЦ
 * Created by s.kolyada on 08.11.2017.
 */
@Repository
public class ResourceLifecyclePhaseTransitionRepository implements Serializable {

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
	 * Создать переход
	 * @param from из фазы
	 * @param to в фазу
	 * @param comment название перехода
	 * @return созданный переход
	 */
	public ResourceLifecyclePhaseTransition createTransition(ResourceLifecyclePhase from,
															 ResourceLifecyclePhase to,
															 String comment) {
		ResourceLifecyclePhaseTransition transition = ResourceLifecyclePhaseTransition.builder()
				.id(idSequenceService.nextValue(ResourceLifecyclePhaseTransition.class))
				.incomingPhase(from)
				.outcomingPhase(to)
				.comment(comment)
				.build();
		em.persist(transition);

		from.getOutcomingPhases().add(transition);
		to.getIncomingPhases().add(transition);
		em.merge(from);
		em.merge(to);

		return transition;
	}

	/**
	 * Удалить переход
	 * @param id идентификатор перехода
	 */
	public void removeTransition(Long id) {
		ResourceLifecyclePhaseTransition transition = em.find(ResourceLifecyclePhaseTransition.class, id);
		if (transition == null) {
			return;
		}
		// не проверяем на null, тк поля объявленны notNull в модели и в БД
		ResourceLifecyclePhase incomingPhase = transition.getIncomingPhase();
		incomingPhase.getOutcomingPhases().remove(transition);
		ResourceLifecyclePhase outcomingPhase = transition.getOutcomingPhase();
		outcomingPhase.getIncomingPhases().remove(transition);
		em.remove(transition);
		em.persist(incomingPhase);
		em.persist(outcomingPhase);
	}

	/**
	 * Переименовать переход
	 * @param id идентификатор перехода
	 * @param comment название перехода
	 * @return обновлённый переход
	 */
	public ResourceLifecyclePhaseTransition rename(Long id, String comment) {
		ResourceLifecyclePhaseTransition transition = em.find(ResourceLifecyclePhaseTransition.class, id);
		if (transition == null) {
			return null;
		}
		transition.setComment(comment);
		em.persist(transition);
		return transition;
	}
}
