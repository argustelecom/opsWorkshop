package ru.argustelecom.box.env.lifecycle.impl.definition;

import java.util.ArrayList;
import java.util.List;

import lombok.EqualsAndHashCode;
import lombok.Setter;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiCondition;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleAction;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleCondition;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleEndpoint;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleValidator;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariables;
import ru.argustelecom.box.env.lifecycle.impl.definition.proxy.LifecycleActionProxy;
import ru.argustelecom.box.env.lifecycle.impl.definition.proxy.LifecycleConditionProxy;
import ru.argustelecom.box.env.lifecycle.impl.definition.proxy.LifecycleValidatorProxy;

@EqualsAndHashCode(of = { "route", "destination" })
public class LifecycleEndpointImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleEndpoint<S> {

	@Setter
	private S destination;

	@Setter
	private LifecycleRouteImpl<S, O> route;

	@Setter
	private boolean silent = true;

	private LifecycleVariablesImpl variables = new LifecycleVariablesImpl();
	private LifecycleCondition<S, ? super O> routingCondition;
	private List<LifecycleValidator<S, ? super O>> validators = new ArrayList<>();
	private List<LifecycleAction<S, ? super O>> actions = new ArrayList<>();

	public LifecycleEndpointImpl(S destination) {
		this.destination = destination;
	}

	public LifecycleRouteImpl<S, O> route() {
		return route;
	}

	public LifecycleVariablesImpl variables() {
		return variables;
	}

	public LifecycleCondition<S, ? super O> routingCondition() {
		return routingCondition;
	}

	public List<LifecycleValidator<S, ? super O>> validators() {
		return validators;
	}

	public List<LifecycleAction<S, ? super O>> actions() {
		return actions;
	}

	public void setRoutingCondition(LifecycleCondition<S, ? super O> condition) {
		this.routingCondition = LifecycleConditionProxy.createSimple(condition);
	}

	public void setRoutingCondition(Class<? extends LifecycleCdiCondition<S, ? super O>> cdiConditionClass) {
		this.routingCondition = LifecycleConditionProxy.createCdi(cdiConditionClass);
	}

	public void addValidator(LifecycleValidator<S, ? super O> validator) {
		this.validators.add(LifecycleValidatorProxy.createSimple(validator));
	}

	public void addValidator(Class<? extends LifecycleCdiValidator<S, ? super O>> cdiValidatorClass) {
		this.validators.add(LifecycleValidatorProxy.createCdi(cdiValidatorClass));
	}

	public void addAction(LifecycleAction<S, ? super O> action) {
		this.actions.add(LifecycleActionProxy.createSimple(action));
	}

	public void addAction(Class<? extends LifecycleCdiAction<S, ? super O>> cdiActionClass) {
		this.actions.add(LifecycleActionProxy.createCdi(cdiActionClass));
	}

	@Override
	public String toString() {
		return String.format("LifecycleEndpoint[%s%s]", destination, isDefault() ? ",default" : "");
	}

	// ****************************************************************************************************************
	// PULBIC API
	// ****************************************************************************************************************

	@Override
	public S getDestination() {
		return destination;
	}

	@Override
	public LifecycleVariables getVariables() {
		return variables();
	}

	@Override
	public boolean isSilent() {
		return silent;
	}

	@Override
	public boolean isDefault() {
		return routingCondition() == null;
	}

	@Override
	public boolean hasRoutingCondition() {
		return routingCondition() != null;
	}

	@Override
	public boolean hasValidators() {
		return !validators().isEmpty();
	}

	@Override
	public boolean hasActions() {
		return !actions().isEmpty();
	}
}
