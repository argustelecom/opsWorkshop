package ru.argustelecom.box.env.lifecycle.api.factory;

import java.io.Serializable;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiCondition;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleAction;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleCondition;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleValidator;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable;
import ru.argustelecom.box.env.type.model.TypeProperty;

/**
 * TODO [документирование жизненного цикла]
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public interface LifecycleEndpointBuilder<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	LifecycleEndpointBuilder<S, O> silent(boolean value);

	LifecycleEndpointBuilder<S, O> when(LifecycleCondition<S, ? super O> condition);

	LifecycleEndpointBuilder<S, O> when(Class<? extends LifecycleCdiCondition<S, ? super O>> cdiConditionClass);

	<V, P extends TypeProperty<V>> LifecycleEndpointBuilder<S, O> contextVar(Class<P> variableClass, Serializable keyword,
			String displayName, V defaultValue);

	<V, P extends TypeProperty<V>> LifecycleEndpointBuilder<S, O> contextVar(Class<P> variableClass, Serializable keyword,
																			 LifecycleVariableConfigurator<P> configurator);
	
	<V, P extends TypeProperty<V>> LifecycleEndpointBuilder<S, O> contextVar(LifecycleVariable<P> var);

	LifecycleEndpointBuilder<S, O> validate(LifecycleValidator<S, ? super O> validator);

	LifecycleEndpointBuilder<S, O> validate(Class<? extends LifecycleCdiValidator<S, ? super O>> cdiValidatorClass);

	LifecycleEndpointBuilder<S, O> execute(LifecycleAction<S, ? super O> action);

	LifecycleEndpointBuilder<S, O> execute(Class<? extends LifecycleCdiAction<S, ? super O>> cdiActionClass);

	LifecycleRouteBuilder<S, O> end();

}