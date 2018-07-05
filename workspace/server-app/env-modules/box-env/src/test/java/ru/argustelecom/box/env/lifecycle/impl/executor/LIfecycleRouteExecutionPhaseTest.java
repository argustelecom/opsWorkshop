package ru.argustelecom.box.env.lifecycle.impl.executor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Date;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleAction;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.context.LifecycleExecutionCtxImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class LIfecycleRouteExecutionPhaseTest extends LifecycleExecutionTestCase {

	@InjectMocks
	private LifecycleRouteExecutionPhase phase;

	private LifecycleEndpointImpl<SampleState, Sample> endpoint;
	private ValidationResult<Object> validationResult;
	private LifecycleAction<SampleState, Sample> action;

	@Before
	@Override
	public void setup() {
		super.setup();
		endpoint = route.endpoints().iterator().next();
		validationResult = ValidationResult.success();

		executor.updateEndpoint(endpoint);
		executor.updateExecutionContext(new LifecycleExecutionCtxImpl<>(businessObject, route, endpoint, new Date()));
		executor.updateValidationResult(validationResult);

		action = spy(SampleActionMock.class);
		endpoint.addAction(action);
	}

	@After
	@Override
	public void cleanup() {
		super.cleanup();
		endpoint = null;
		validationResult = null;
	}

	@Test
	public void shouldHaveExpectedPhaseIdentifier() {
		assertThat(phase.getId(), equalTo(LifecyclePhaseId.ROUTE_EXECUTION));
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfExecutorDoesNotHaveDeterminedEndpoint() {
		executor.updateEndpoint(null);
		phase.doPhase(executor);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfExecutorDoesNotHaveDeterminedExecutionContext() {
		executor.updateExecutionContext(null);
		phase.doPhase(executor);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfExecutorDoesNotHaveDeterminedValidationResult() {
		executor.updateValidationResult(null);
		phase.doPhase(executor);
		fail("Expected exception didn't thrown!");
	}

	@Test
	public void shouldPerformAction() {
		phase.doPhase(executor);
		verify(action, times(1)).execute(Mockito.any());
	}

	@Test(expected = BusinessException.class)
	public void shouldFailIfValidationResultHaveUnsuppressedWarnings() {
		validationResult.warn(businessObject, "warn");
		phase.doPhase(executor);
		fail("Expected exception didn't thrown!");
	}

	@Test
	public void shouldPerformActionIfValidationResultHaveSuppressedWarnings() {
		validationResult.warn(businessObject, "warn");
		executor.executionContext().suppressWarnings();
		phase.doPhase(executor);
		verify(action, times(1)).execute(Mockito.any());
	}

	@Test(expected = BusinessException.class)
	public void shouldFailIfValidationResultHaveErrors() {
		validationResult.error(businessObject, "error");
		phase.doPhase(executor);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = BusinessException.class)
	public void shouldFailIfValidationResultHaveSuppressedWarningsAndAnyError() {
		validationResult.warn(businessObject, "warn");
		validationResult.error(businessObject, "error");
		executor.executionContext().suppressWarnings();

		phase.doPhase(executor);
		fail("Expected exception didn't thrown!");
	}

	@Test
	public void shouldCallPhaseListener() {
		phase.doPhase(executor);
		verify(phaseListener, times(1)).beforeRouteExecution(Mockito.any(), Mockito.any());
		verify(phaseListener, times(1)).afterRouteExecution(Mockito.any());
	}

	@Test
	public void shouldNotifyObserversOnPhaseExecution() {
		phase.doPhase(executor);
		verify(notificator, times(1)).fireRoutedToEvent(businessObject);
	}

	@Test
	public void shouldFlushPersistenceContext() {
		phase.doPhase(executor);
		verify(em, times(1)).flush();
	}

	@Test
	public void shouldChangeBusinessObjectStateAfterPhaseExecution() {
		assertThat(businessObject.getState(), not(equalTo(endpoint.getDestination())));
		phase.doPhase(executor);
		assertThat(businessObject.getState(), equalTo(endpoint.getDestination()));
	}
	
	@Test(expected = UnsupportedOperationException.class)
	public void shouldFailPhaseCleaning() {
		phase.clean(executor);
		fail("Expected exception didn't thrown!");
	}

	static class SampleActionMock implements LifecycleAction<SampleState, Sample> {
		@Override
		public void execute(ExecutionCtx<SampleState, ? extends Sample> ctx) {
			// DO NOTHING
		}
	}
}
