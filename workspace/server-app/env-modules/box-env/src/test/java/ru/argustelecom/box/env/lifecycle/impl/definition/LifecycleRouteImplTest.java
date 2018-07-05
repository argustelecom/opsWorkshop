package ru.argustelecom.box.env.lifecycle.impl.definition;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;

public class LifecycleRouteImplTest {

	@Test
	public void shouldDetermineDefaultEndpoint() {
		LifecycleRouteImpl<SampleState, Sample> route = new LifecycleRouteImpl<>("Route", "Route");
		LifecycleEndpointImpl<SampleState, Sample> endpoint = new LifecycleEndpointImpl<>(SampleState.ACTIVE);

		assertThat(endpoint.isDefault(), is(true));

		endpoint.setRoute(route);
		route.addEndpoint(endpoint);

		assertThat(route.hasDefaultEndpoint(), is(true));
		assertThat(route.getDefaultEndpoint(), equalTo(endpoint));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfDefaultEndpointAlreadyDefined() {
		LifecycleRouteImpl<SampleState, Sample> route = new LifecycleRouteImpl<>("Route", "Route");

		LifecycleEndpointImpl<SampleState, Sample> endpoint1 = new LifecycleEndpointImpl<>(SampleState.ACTIVE);

		assertThat(endpoint1.isDefault(), is(true));

		endpoint1.setRoute(route);
		route.addEndpoint(endpoint1);

		LifecycleEndpointImpl<SampleState, Sample> endpoint2 = new LifecycleEndpointImpl<>(SampleState.WHAITING);

		assertThat(endpoint2.isDefault(), is(true));

		endpoint1.setRoute(route);
		route.addEndpoint(endpoint1);

		fail("Could add two default endpoints to one route");
	}

}
