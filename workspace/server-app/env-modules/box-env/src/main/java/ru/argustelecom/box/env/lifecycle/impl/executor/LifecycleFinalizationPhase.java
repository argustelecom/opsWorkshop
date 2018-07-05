package ru.argustelecom.box.env.lifecycle.impl.executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.env.lifecycle.impl.LifecycleHistoryRepository;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleImpl;
import ru.argustelecom.box.env.lifecycle.impl.event.LifecycleNotificator;

@RequestScoped
class LifecycleFinalizationPhase extends LifecyclePhase {

	private static final long serialVersionUID = 5469790209197303397L;

	@Inject
	private LifecycleNotificator notificator;

	@Inject
	private LifecycleHistoryRepository history;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
	}

	@Override
	@PreDestroy
	protected void preDestroy() {
		super.preDestroy();
	}

	@Override
	public LifecyclePhaseId getId() {
		return LifecyclePhaseId.FINALIZATION;
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void clean(LifecycleExecutorImpl<S, O> executor) {
		throw new UnsupportedOperationException("LifecyclePhase#clean is not supported in phase " + getId());
	}

	@Override
	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void execute(
			LifecycleExecutorImpl<S, O> executor) {

		O businessObject = executor.getBusinessObject();
		S businessObjectState = executor.getBusinessObjectState();
		LifecycleImpl<S, O> lifecycle = executor.lifecycle();

		LifecycleHistoryItem historyItem = saveRoutingHistory(lifecycle, businessObject, businessObjectState);
		log.debugv("Lifecycle route saved in history {0}", historyItem);

		fireRoutingCompletedEvent(businessObject, businessObjectState);
		executor.forEachPhaseListener(listener -> listener.afterFinalization(businessObject, businessObjectState));
		
		businessObject.onStateChanged(businessObjectState, businessObject.getState());
	}

	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> LifecycleHistoryItem saveRoutingHistory(
			LifecycleImpl<S, O> lifecycle, O businessObject, S initialState) {
		return history.saveRoutingHistory(lifecycle, businessObject, initialState);
	}

	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void fireRoutingCompletedEvent(O businessObject,
			S initialState) {
		notificator.fireRoutingCompletedEvent(businessObject, initialState);
	}
}
