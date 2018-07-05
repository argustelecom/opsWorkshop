package ru.argustelecom.box.env.lifecycle.impl.executor;

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.Transactional;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleAction;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.context.LifecycleExecutionCtxImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.box.env.lifecycle.impl.event.LifecycleNotificator;
import ru.argustelecom.box.env.lifecycle.nls.LifecycleMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.validation.ValidationResult;

@Transactional
@RequestScoped
class LifecycleRouteExecutionPhase extends LifecyclePhase {

	private static final long serialVersionUID = -2181855947138018969L;

	@Inject
	private transient LifecycleNotificator notificator;

	@PersistenceContext
	private transient EntityManager em;

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
		return LifecyclePhaseId.ROUTE_EXECUTION;
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void clean(LifecycleExecutorImpl<S, O> executor) {
		throw new UnsupportedOperationException("LifecyclePhase#clean is not supported in phase " + getId());
	}

	@Override
	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void execute(
			LifecycleExecutorImpl<S, O> executor) {

		checkState(executor.endpoint() != null);
		checkState(executor.executionContext() != null);
		checkState(executor.getValidationResult() != null);

		O businessObject = executor.getBusinessObject();
		LifecycleEndpointImpl<S, O> endpoint = executor.endpoint();
		LifecycleExecutionCtxImpl<S, O> executionContext = executor.executionContext();
		ValidationResult<Object> validationResult = executor.getValidationResult();

		executor.forEachPhaseListener(listener -> listener.beforeRouteExecution(executionContext, validationResult));

		if (!validationResult.isSuccess(executionContext.isIgnoreWarnings())) {
			LifecycleMessagesBundle messages = LocaleUtils.getMessages(LifecycleMessagesBundle.class);
			throw new BusinessException(messages.transitionIsBlocked(validationResult.explain()));
		}

		if (endpoint.hasActions()) {
			log.debugv("Execute lifecycle transition to endpoint {0}", endpoint);
			for (LifecycleAction<S, ? super O> action : endpoint.actions()) {
				action.execute(executionContext);
			}
		} else {
			log.debugv("Lifecycle transition to endpoint {0} does not require any action. Skip it.", endpoint);
		}

		businessObject.setState(endpoint.getDestination());
		flushPersistenceContext();
		fireRoutedToEvent(businessObject);

		executor.forEachPhaseListener(listener -> listener.afterRouteExecution(executionContext));
	}

	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void fireRoutedToEvent(O businessObject) {
		notificator.fireRoutedToEvent(businessObject);
	}

	protected void flushPersistenceContext() {
		em.flush();
	}
}
