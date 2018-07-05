package ru.argustelecom.box.env.lifecycle.impl.definition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.util.Collection;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;
import ru.argustelecom.system.inf.exception.SystemException;

public class LifecycleImplTest {

	private LifecycleImpl<SampleState, Sample> lifecycle;

	@Before
	public void setup() {
		this.lifecycle = createLifecycle();
	}

	@After
	public void cleanup() {
		this.lifecycle = null;
	}

	@Test
	public void shouldFindRoutes() {
		List<LifecycleRouteImpl<SampleState, Sample>> routes = lifecycle.findRoutes(SampleState.DRAFT);
		assertThat(routes.size(), equalTo(2));

		routes = lifecycle.findRoutes(SampleState.ACTIVE);
		assertThat(routes.size(), equalTo(1));

		routes = lifecycle.findRoutes(SampleState.DEACTIVATING);
		assertThat(routes.size(), equalTo(2));

		routes = lifecycle.findRoutes(SampleState.CLOSED);
		assertThat(routes.size(), equalTo(0));
	}

	@Test
	public void shouldFindExistingRouteWithKeyword() {
		assertThat(lifecycle.findRoute(SampleState.DRAFT, "Route #1"), is(notNullValue()));
		assertThat(lifecycle.findRoute(SampleState.DEACTIVATING, "Route #4"), is(notNullValue()));
	}

	@Test(expected = SystemException.class)
	public void shouldFailWhenFindingNotExistedRouteWithKeyword() {
		lifecycle.findRoute(SampleState.DRAFT, "Route #3");
	}

	@Test
	public void shouldFindMainRoute() {
		LifecycleRoute<SampleState, Sample> mainRoute = lifecycle.getMainRoute(SampleState.DRAFT);
		assertThat(mainRoute, is(notNullValue()));
		assertThat(mainRoute.getKeyword(), equalTo("Route #1"));

		mainRoute = lifecycle.getMainRoute(SampleState.DEACTIVATING);
		assertThat(mainRoute, is(notNullValue()));
		assertThat(mainRoute.getKeyword(), equalTo("Route #4"));
	}

	@Test
	public void shouldFindSecondaryRoutes() {
		Collection<LifecycleRoute<SampleState, Sample>> secondaryRoutes = lifecycle.getSecondaryRoutes(SampleState.DRAFT);
		assertThat(secondaryRoutes, is(notNullValue()));
		assertThat(secondaryRoutes.size(), equalTo(1));
		assertThat(secondaryRoutes.iterator().next().getKeyword(), equalTo("Route #2"));

		secondaryRoutes = lifecycle.getSecondaryRoutes(SampleState.ACTIVE);
		assertThat(secondaryRoutes, is(notNullValue()));
		assertThat(secondaryRoutes.size(), equalTo(0));
	}

	@Test
	public void shouldForbidAndTestBehavior() {
		lifecycle.forbid(SampleState.ACTIVE, "CLICK1", "CLICK2");
		lifecycle.forbid(SampleState.DRAFT, "CLICK1", "CLICK3");

		assertThat(lifecycle.isForbidden(SampleState.ACTIVE, "CLICK1"), is(true));
		assertThat(lifecycle.isForbidden(SampleState.ACTIVE, "CLICK2"), is(true));
		assertThat(lifecycle.isForbidden(SampleState.ACTIVE, "CLICK3"), is(false));

		assertThat(lifecycle.isForbidden(SampleState.DRAFT, "CLICK1"), is(true));
		assertThat(lifecycle.isForbidden(SampleState.DRAFT, "CLICK3"), is(true));
		assertThat(lifecycle.isForbidden(SampleState.DRAFT, "CLICK2"), is(false));
	}

	private LifecycleImpl<SampleState, Sample> createLifecycle() {
		LifecycleImpl<SampleState, Sample> lifecycle = new LifecycleImpl<>();
		lifecycle.addRoute(createSimpleRoute("Route #1", SampleState.DRAFT, SampleState.WHAITING));
		lifecycle.addRoute(createSimpleRoute("Route #2", SampleState.DRAFT, SampleState.ACTIVE));
		lifecycle.addRoute(createSimpleRoute("Route #3", SampleState.ACTIVE, SampleState.DEACTIVATING));
		lifecycle.addRoute(createSimpleRoute("Route #4", SampleState.DEACTIVATING, SampleState.ACTIVE));
		lifecycle.addRoute(createSimpleRoute("Route #5", SampleState.DEACTIVATING, SampleState.CLOSED));
		return lifecycle;
	}

	private LifecycleRouteImpl<SampleState, Sample> createSimpleRoute(String keyword, SampleState from, SampleState to) {
		LifecycleRouteImpl<SampleState, Sample> route = new LifecycleRouteImpl<>(keyword, keyword);
		route.addStartpoint(from);
		LifecycleEndpointImpl<SampleState, Sample> endpoint = new LifecycleEndpointImpl<>(to);
		endpoint.setRoute(route);
		route.addEndpoint(endpoint);
		return route;
	}

}
