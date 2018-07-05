package ru.argustelecom.box.env.lifecycle.impl.factory;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleEndpointBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRouteBuilder;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;

public class LifecycleRouteBuilderImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleRouteBuilder<S, O> {

	protected LifecycleBuilderImpl<S, O> parent;
	protected LifecycleRouteImpl<S, O> route;

	public LifecycleRouteBuilderImpl(LifecycleBuilderImpl<S, O> parent) {
		this.parent = checkNotNull(parent);
	}

	public LifecycleRouteBuilderImpl<S, O> begin(Serializable routeKeyword, String routeName) {
		route = new LifecycleRouteImpl<>(routeKeyword, routeName);
		return this;
	}

	@Override
	public LifecycleRouteBuilder<S, O> controlledByUser(boolean value) {
		checkBegined();
		route.setControlledByUser(value);
		return this;
	}

	@Override
	public LifecycleRouteBuilder<S, O> from(S startpoint) {
		checkBegined();
		route.addStartpoint(startpoint);
		return this;
	}

	@Override
	public LifecycleEndpointBuilder<S, O> to(S endpoint) {
		checkBegined();
		LifecycleEndpointBuilderImpl<S, O> endpointBuilder = new LifecycleEndpointBuilderImpl<>(this);
		return endpointBuilder.begin(endpoint);
	}

	@Override
	public LifecycleBuilder<S, O> end() {
		checkBegined();
		checkPreconditions();
		boolean added = parent.lifecycle.addRoute(route);
		checkState(added, "Route with keyword %s is allready exists. Change keyword", route.getKeyword());
		return parent;
	}

	private void checkBegined() {
		checkState(route != null);
	}

	private void checkPreconditions() {
		checkState(!route.getStartpoints().isEmpty(), "Route must have at least one start point");
		checkState(!route.getEndpoints().isEmpty(), "Route must have at least one end point");
		checkState(route.hasDefaultEndpoint(), "Route must have one and only one default end point");
	}
}