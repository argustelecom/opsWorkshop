package ru.argustelecom.box.env.lifecycle.impl.executor;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.event.LifecycleNotificator;

@RequestScoped
class LifecycleInitializationPhase extends LifecyclePhase {

	private static final long serialVersionUID = -6852770509495457357L;

	@Inject
	private LifecycleNotificator notificator;

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
		return LifecyclePhaseId.INITIALIZATION;
	}

	@Override
	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void execute(
			LifecycleExecutorImpl<S, O> executor) {

		O businessObject = executor.getBusinessObject();

		executor.forEachPhaseListener(listener -> listener.beforeInitialization(businessObject));
		fireRoutedFromEvent(businessObject);
	}

	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void fireRoutedFromEvent(O businessObject) {
		notificator.fireRoutedFromEvent(businessObject);
	}
}
