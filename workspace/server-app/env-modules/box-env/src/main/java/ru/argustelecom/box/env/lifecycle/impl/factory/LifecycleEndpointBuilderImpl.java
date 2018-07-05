package ru.argustelecom.box.env.lifecycle.impl.factory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

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
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleEndpointBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRouteBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleVariableConfigurator;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;
import ru.argustelecom.box.env.type.model.TypeProperty;

public class LifecycleEndpointBuilderImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleEndpointBuilder<S, O> {

	protected LifecycleEndpointImpl<S, O> endpoint;
	protected LifecycleRouteBuilderImpl<S, O> parent;

	public LifecycleEndpointBuilderImpl(LifecycleRouteBuilderImpl<S, O> parent) {
		this.parent = checkNotNull(parent);
	}

	public LifecycleEndpointBuilderImpl<S, O> begin(S destination) {
		this.endpoint = new LifecycleEndpointImpl<>(destination);
		return this;
	}

	@Override
	public LifecycleEndpointBuilder<S, O> silent(boolean value) {
		checkBegined();
		endpoint.setSilent(value);
		return this;
	}

	@Override
	public LifecycleEndpointBuilder<S, O> when(LifecycleCondition<S, ? super O> condition) {
		checkBegined();
		endpoint.setRoutingCondition(condition);
		return this;
	}

	@Override
	public LifecycleEndpointBuilder<S, O> when(Class<? extends LifecycleCdiCondition<S, ? super O>> cdiConditionClass) {
		checkBegined();
		endpoint.setRoutingCondition(cdiConditionClass);
		return this;
	}

	@Override
	public <V, P extends TypeProperty<V>> LifecycleEndpointBuilder<S, O> contextVar(Class<P> variableClass,
			Serializable keyword, String displayName, V defaultValue) {
		checkBegined();
		P variableDef = endpoint.variables().defineVariable(variableClass, checkNotNull(keyword));
		variableDef.setName(displayName != null ? displayName : keyword.toString());
		variableDef.setDefaultValue(defaultValue);
		return this;
	}

	@Override
	public <V, P extends TypeProperty<V>> LifecycleEndpointBuilder<S, O> contextVar(Class<P> variableClass,
			Serializable keyword, LifecycleVariableConfigurator<P> configurator) {
		checkBegined();
		P variableDef = endpoint.variables().defineVariable(variableClass, checkNotNull(keyword));
		if (configurator != null) {
			configurator.configure(variableDef);
		}
		return this;
	}

	@Override
	public <V, P extends TypeProperty<V>> LifecycleEndpointBuilder<S, O> contextVar(LifecycleVariable<P> var) {
		checkBegined();
		P variableDef = endpoint.variables().defineVariable(var);
		if (var.conf() != null) {
			var.conf().configure(variableDef);
		}
		return this;
	}

	@Override
	public LifecycleEndpointBuilder<S, O> validate(LifecycleValidator<S, ? super O> validator) {
		checkBegined();
		endpoint.addValidator(validator);
		return this;
	}

	@Override
	public LifecycleEndpointBuilder<S, O> validate(
			Class<? extends LifecycleCdiValidator<S, ? super O>> cdiValidatorClass) {
		checkBegined();
		endpoint.addValidator(cdiValidatorClass);
		return this;
	}

	@Override
	public LifecycleEndpointBuilder<S, O> execute(LifecycleAction<S, ? super O> action) {
		checkBegined();
		endpoint.addAction(action);
		return this;
	}

	@Override
	public LifecycleEndpointBuilder<S, O> execute(Class<? extends LifecycleCdiAction<S, ? super O>> cdiActionClass) {
		checkBegined();
		endpoint.addAction(cdiActionClass);
		return this;
	}

	@Override
	public LifecycleRouteBuilder<S, O> end() {
		checkBegined();
		checkPreconditions();
		LifecycleRouteImpl<S, O> route = parent.route;
		endpoint.setRoute(route);
		boolean added = route.addEndpoint(endpoint);
		checkState(added, "Endpoint %s is allready exists in route %s", endpoint, route);
		return parent;
	}

	private void checkBegined() {
		checkState(endpoint != null);
	}

	private void checkPreconditions() {
		// Нет проверок. Endpoint может быть пустым: без условий, без валидаций, без операций и т.д.
	}

}
