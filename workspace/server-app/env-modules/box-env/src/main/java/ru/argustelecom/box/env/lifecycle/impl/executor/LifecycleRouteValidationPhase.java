package ru.argustelecom.box.env.lifecycle.impl.executor;

import static com.google.common.base.Preconditions.checkState;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleValidator;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.context.LifecycleExecutionCtxImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.system.inf.validation.ValidationResult;

@RequestScoped
class LifecycleRouteValidationPhase extends LifecyclePhase {

	private static final long serialVersionUID = 602858645603231359L;

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
		return LifecyclePhaseId.ROUTE_VALIDATION;
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void clean(LifecycleExecutorImpl<S, O> executor) {
		super.clean(executor);
		executor.updateValidationResult(null);
	}

	@Override
	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void execute(
			LifecycleExecutorImpl<S, O> executor) {

		checkState(executor.endpoint() != null);
		checkState(executor.executionContext() != null);

		LifecycleEndpointImpl<S, O> endpoint = executor.endpoint();
		LifecycleExecutionCtxImpl<S, O> executionContext = executor.executionContext();
		ValidationResult<Object> validationResult = ValidationResult.success();

		executor.forEachPhaseListener(listener -> listener.beforeRouteValidation(executionContext));

		if (endpoint.hasValidators()) {
			log.debugv("Validate lifecycle transition to endpoint {0}", endpoint);
			for (LifecycleValidator<S, ? super O> validator : endpoint.validators()) {
				validator.validate(executionContext, validationResult);
			}
		} else {
			log.debugv("Lifecycle transition to endpoint {0} does not require any validation. Skip it.", endpoint);
		}

		executor.forEachPhaseListener(listener -> listener.afterRouteValidation(executionContext, validationResult));
		executor.updateValidationResult(validationResult);
	}

}
