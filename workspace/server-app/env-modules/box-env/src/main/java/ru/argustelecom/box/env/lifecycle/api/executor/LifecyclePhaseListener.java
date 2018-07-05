package ru.argustelecom.box.env.lifecycle.api.executor;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;
import ru.argustelecom.system.inf.validation.ValidationResult;

/**
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public abstract class LifecyclePhaseListener<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	public void beforeInitialization(O businessObject) {
	}

	public void beforeRouteDefinition(TestingCtx<S, ? extends O> ctx) {
	}

	public void afterRouteDefinition(ExecutionCtx<S, ? extends O> ctx) {
	}

	public void beforeRouteValidation(ExecutionCtx<S, ? extends O> ctx) {
	}

	public void afterRouteValidation(ExecutionCtx<S, ? extends O> ctx, ValidationResult<Object> result) {
	}

	public void beforeRouteExecution(ExecutionCtx<S, ? extends O> ctx, ValidationResult<Object> result) {
	}

	public void afterRouteExecution(ExecutionCtx<S, ? extends O> ctx) {
	}

	public void afterFinalization(O businessObject, S oldState) {
	}

}
