package ru.argustelecom.box.env.lifecycle.impl.definition;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import lombok.Setter;
import lombok.Synchronized;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleContextConfigurator;
import ru.argustelecom.system.inf.exception.SystemException;

public class LifecycleImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>> implements Lifecycle<S, O> {

	@Setter
	private Serializable keyword;

	@Setter
	private String name;

	private Map<S, Set<Serializable>> forbiddenBehaviors = new HashMap<>();
	private Set<LifecycleRouteImpl<S, O>> routes = new LinkedHashSet<>();
	private List<LifecycleContextConfigurator<S, O>> configurators = new ArrayList<>();
	private ControlledByUserDelegate delegate;

	public void forbid(S state, Serializable... behavior) {
		Set<Serializable> behaviors = forbiddenBehaviors.get(state);
		if (behaviors == null) {
			behaviors = new HashSet<>();
			forbiddenBehaviors.put(state, behaviors);
		}
		for (Serializable b : behavior) {
			behaviors.add(b);
		}
	}

	public Set<LifecycleRouteImpl<S, O>> routes() {
		return routes;
	}

	public List<LifecycleRouteImpl<S, O>> findRoutes(S startPoint) {
		ArrayList<LifecycleRouteImpl<S, O>> result = new ArrayList<>();
		for (LifecycleRouteImpl<S, O> route : routes()) {
			if (filter(route, startPoint)) {
				result.add(route);
			}
		}
		return result;
	}

	public LifecycleRouteImpl<S, O> findRoute(S startPoint, Serializable routeKeyword) {
		List<LifecycleRouteImpl<S, O>> routesFrom = findRoutes(startPoint);
		for (LifecycleRouteImpl<S, O> route : routesFrom) {
			if (Objects.equals(routeKeyword, route.getKeyword())) {
				return route;
			}
		}

		throw new SystemException(String.format("Lifecycle '%s' does not have route with keyword '%s' from state '%s'",
				keyword, routeKeyword, startPoint));
	}

	public LifecycleRouteImpl<S, O> findRoute(S startPoint, S destination) {
		List<LifecycleRouteImpl<S, O>> routesFrom = findRoutes(startPoint);
		for (LifecycleRouteImpl<S, O> route : routesFrom) {
			if (route.canEndIn(destination)) {
				return route;
			}
		}

		throw new SystemException(String.format("Lifecycle '%s' does not have route with endpoint '%s' from state '%s'",
				keyword, destination, startPoint));
	}

	public boolean addRoute(LifecycleRouteImpl<S, O> route) {
		return routes.add(route);
	}

	public List<LifecycleContextConfigurator<S, O>> configurators() {
		return configurators;
	}

	public boolean addConfigurator(LifecycleContextConfigurator<S, O> configurator) {
		return configurators.add(configurator);
	}

	@Override
	public String toString() {
		return String.format("Lifecycle[%s]", getKeyword());
	}

	protected boolean filter(LifecycleRouteImpl<S, O> route, S startPoint) {
		return route.canBeginIn(startPoint);
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
	@Synchronized
	public Lifecycle<S, O> controlledByUser() {
		if (delegate == null) {
			delegate = new ControlledByUserDelegate();
		}
		return delegate;
	}

	@Override
	public boolean isForbidden(S state, Serializable behavior) {
		Set<Serializable> behaviors = forbiddenBehaviors.get(state);
		return behaviors != null && behaviors.contains(behavior);
	}

	@Override
	public boolean hasRoutes(S startPoint) {
		boolean result = false;
		for (LifecycleRouteImpl<S, O> route : routes()) {
			if (filter(route, startPoint)) {
				result = true;
				break;
			}
		}
		return result;
	}

	@Override
	public Collection<LifecycleRoute<S, O>> getRoutes(S startPoint) {
		List<LifecycleRouteImpl<S, O>> routesFrom = findRoutes(startPoint);
		if (!routesFrom.isEmpty()) {
			List<LifecycleRoute<S, O>> result = new ArrayList<>();
			result.addAll(routesFrom);
			return Collections.unmodifiableList(result);
		}
		return Collections.emptyList();
	}

	@Override
	public LifecycleRoute<S, O> getMainRoute(S startPoint) {
		List<LifecycleRouteImpl<S, O>> routesFrom = findRoutes(startPoint);
		return !routesFrom.isEmpty() ? routesFrom.get(0) : null;
	}

	@Override
	public Collection<LifecycleRoute<S, O>> getSecondaryRoutes(S startPoint) {
		List<LifecycleRouteImpl<S, O>> routesFrom = findRoutes(startPoint);
		if (routesFrom.size() > 1) {
			return Collections.unmodifiableList(routesFrom.subList(1, routesFrom.size()));
		}
		return Collections.emptyList();
	}

	private class ControlledByUserDelegate extends LifecycleImpl<S, O> {

		@Override
		public Set<LifecycleRouteImpl<S, O>> routes() {
			return LifecycleImpl.this.routes();
		}

		@Override
		public boolean addRoute(LifecycleRouteImpl<S, O> route) {
			throw new UnsupportedOperationException();
		}

		@Override
		public List<LifecycleContextConfigurator<S, O>> configurators() {
			return LifecycleImpl.this.configurators();
		}

		@Override
		public boolean addConfigurator(LifecycleContextConfigurator<S, O> configurator) {
			throw new UnsupportedOperationException();
		}

		@Override
		protected boolean filter(LifecycleRouteImpl<S, O> route, S startPoint) {
			return LifecycleImpl.this.filter(route, startPoint) && route.isControlledByUser();
		}

		@Override
		public Serializable getKeyword() {
			return LifecycleImpl.this.getKeyword();
		}

		@Override
		public void setKeyword(Serializable keyword) {
			throw new UnsupportedOperationException();
		}

		@Override
		public String getName() {
			return LifecycleImpl.this.getName();
		}

		@Override
		public void setName(String name) {
			throw new UnsupportedOperationException();
		}

		@Override
		public boolean isForbidden(S state, Serializable behavior) {
			return LifecycleImpl.this.isForbidden(state, behavior);
		}

		@Override
		public void forbid(S state, Serializable... behavior) {
			throw new UnsupportedOperationException();
		}

		@Override
		public Lifecycle<S, O> controlledByUser() {
			return this;
		}
	}
}
