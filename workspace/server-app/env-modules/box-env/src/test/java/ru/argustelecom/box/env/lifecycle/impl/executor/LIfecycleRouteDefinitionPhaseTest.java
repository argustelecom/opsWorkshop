package ru.argustelecom.box.env.lifecycle.impl.executor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleRouteDefinitionPhase;

@RunWith(MockitoJUnitRunner.class)
public class LIfecycleRouteDefinitionPhaseTest extends LifecycleExecutionTestCase {

	@InjectMocks
	private LifecycleRouteDefinitionPhase phase;

	@Test
	public void shouldHaveExpectedPhaseIdentifier() {
		assertThat(phase.getId(), equalTo(LifecyclePhaseId.ROUTE_DEFINITION));
	}

	@Test
	public void shouldDefinePrefferedEndpointByCondition() {

		// Условие первое в списке условий (businessObject#id делится нацело на 2)
		{
			businessObject = new Sample(2L);
			businessObject.setState(SampleState.WHAITING);
			route = (LifecycleRouteImpl<SampleState, Sample>) lifecycle.getMainRoute(businessObject);
			executor = new LifecycleExecutorMock(businessObject, lifecycle, route);

			phase.doPhase(executor);

			assertThatEndpointHaveExpectedDestinationAndStatus(SampleState.ACTIVE, false);
		}

		// Условие второе в списке условий (businessObject#id делится нацело на 3)
		{
			businessObject = new Sample(3L);
			businessObject.setState(SampleState.WHAITING);
			route = (LifecycleRouteImpl<SampleState, Sample>) lifecycle.getMainRoute(businessObject);
			executor = new LifecycleExecutorMock(businessObject, lifecycle, route);

			phase.doPhase(executor);

			assertThatEndpointHaveExpectedDestinationAndStatus(SampleState.DEACTIVATING, false);
		}

		// Когда подходит несколько условий, будет использована конечная точка, объявленная первой
		{
			businessObject = new Sample(6L);
			businessObject.setState(SampleState.WHAITING);
			route = (LifecycleRouteImpl<SampleState, Sample>) lifecycle.getMainRoute(businessObject);
			executor = new LifecycleExecutorMock(businessObject, lifecycle, route);

			phase.doPhase(executor);

			assertThatEndpointHaveExpectedDestinationAndStatus(SampleState.ACTIVE, false);
		}
	}

	@Test
	public void shouldDefinePrefferedEndpointByDefault() {
		businessObject.setState(SampleState.WHAITING);
		route = (LifecycleRouteImpl<SampleState, Sample>) lifecycle.getMainRoute(businessObject);
		executor = new LifecycleExecutorMock(businessObject, lifecycle, route);

		phase.doPhase(executor);

		assertThatEndpointHaveExpectedDestinationAndStatus(SampleState.CLOSED, true);
	}

	@Test
	public void shouldCallPhaseListener() {
		phase.doPhase(executor);
		verify(phaseListener, times(1)).beforeRouteDefinition(Mockito.any());
		verify(phaseListener, times(1)).afterRouteDefinition(Mockito.any());
	}

	@Test
	public void shouldCallContextConfigurator() {
		phase.doPhase(executor);
		verify(contextConfigurator, times(1)).configure(Mockito.any());
	}

	@Test
	public void shouldChangeExecutionContext() {
		assertThat(executor.endpoint(), is(nullValue()));
		assertThat(executor.executionContext(), is(nullValue()));

		phase.doPhase(executor);

		assertThat(executor.endpoint(), is(notNullValue()));
		assertThat(executor.executionContext(), is(notNullValue()));
	}

	@Test
	public void shouldCleanExecutorWhenBackingToPreviousPhase() {
		phase.doPhase(executor);
		assertThat(executor.endpoint(), is(notNullValue()));
		assertThat(executor.executionContext(), is(notNullValue()));

		phase.clean(executor);
		assertThat(executor.endpoint(), is(nullValue()));
		assertThat(executor.executionContext(), is(nullValue()));
	}

	private void assertThatEndpointHaveExpectedDestinationAndStatus(SampleState expectedState, boolean isDefault) {
		assertThat(executor.endpoint(), is(notNullValue()));
		assertThat(executor.endpoint().isDefault(), is(isDefault));
		assertThat(executor.endpoint().hasRoutingCondition(), is(!isDefault));
		assertThat(executor.endpoint().getDestination(), equalTo(expectedState));
	}
}
