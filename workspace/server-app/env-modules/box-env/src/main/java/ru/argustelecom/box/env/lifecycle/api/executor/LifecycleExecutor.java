package ru.argustelecom.box.env.lifecycle.api.executor;

import java.util.Date;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleEndpoint;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;
import ru.argustelecom.system.inf.validation.ValidationResult;

/**
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public interface LifecycleExecutor<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	LifecyclePhaseId getCurrentPhaseId();

	O getBusinessObject();

	S getBusinessObjectState();

	void addPhaseListener(LifecyclePhaseListener<S, ? super O> phaseListener);

	void removePhaseListener(LifecyclePhaseListener<S, ? super O> phaseListener);
	
	Date getExecutionDate();

	Lifecycle<S, O> getLifecycle();

	LifecycleRoute<S, O> getRoute();

	LifecycleEndpoint<S> getEndpoint();

	ExecutionCtx<S, O> getExecutionContext();

	ValidationResult<Object> getValidationResult();

	boolean initializeRouting();

	boolean determineRouteEndpoint();

	boolean validateRoute();

	boolean executeRoute();

	boolean finalizeRouting();

	boolean canBackToPreviousPhase();

	boolean backToPreviousPhase();
}
