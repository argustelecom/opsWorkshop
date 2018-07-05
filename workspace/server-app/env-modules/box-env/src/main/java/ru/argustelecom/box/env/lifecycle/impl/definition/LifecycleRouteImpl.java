package ru.argustelecom.box.env.lifecycle.impl.definition;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleEndpoint;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;

@EqualsAndHashCode(of = "keyword")
public class LifecycleRouteImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleRoute<S, O> {

	private Serializable keyword;
	private String name;

	@Setter
	private boolean controlledByUser = true;

	private Set<S> startpoints = new LinkedHashSet<>();
	private Set<LifecycleEndpointImpl<S, O>> endpoints = new LinkedHashSet<>();

	@Getter
	private LifecycleEndpointImpl<S, O> defaultEndpoint;

	public LifecycleRouteImpl(Serializable keyword, String name) {
		this.keyword = checkRequiredArgument(keyword, "keyword");
		this.name = checkRequiredArgument(name, "name");
	}

	public boolean canBeginIn(S startpoint) {
		return startpoints.contains(startpoint);
	}

	public boolean canEndIn(S destination) {
		for (LifecycleEndpointImpl<S, O> endpoint : endpoints()) {
			if (Objects.equals(destination, endpoint.getDestination())) {
				return true;
			}
		}
		return false;
	}

	public boolean addStartpoint(S startpoint) {
		return startpoints.add(startpoint);
	}

	public boolean addEndpoint(LifecycleEndpointImpl<S, O> endpoint) {
		if (endpoint.isDefault()) {
			checkState(defaultEndpoint == null,
					"Endpoint '%s' is marked as default, but route '%s' already defines an default endpoint '%s'",
					endpoint, this, defaultEndpoint);
			defaultEndpoint = endpoint;
		}
		return endpoints.add(endpoint);
	}

	public Collection<LifecycleEndpointImpl<S, O>> endpoints() {
		return endpoints;
	}

	public boolean hasDefaultEndpoint() {
		return defaultEndpoint != null;
	}

	public boolean hasEndpoint(LifecycleEndpointImpl<S, O> endpoint) {
		return Objects.equals(endpoint.route(), this);
	}

	@Override
	public String toString() {
		return String.format("LifecycleRoute[%s]", keyword);
	}

	// ****************************************************************************************************************
	// PULBIC API
	// ****************************************************************************************************************

	@Override
	public Serializable getKeyword() {
		return keyword;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isControlledByUser() {
		return controlledByUser;
	}

	@Override
	public Collection<S> getStartpoints() {
		return Collections.unmodifiableSet(startpoints);
	}

	@Override
	public Collection<LifecycleEndpoint<S>> getEndpoints() {
		ArrayList<LifecycleEndpoint<S>> result = new ArrayList<>(endpoints.size());
		result.addAll(endpoints);
		return Collections.unmodifiableList(result);
	}
}
