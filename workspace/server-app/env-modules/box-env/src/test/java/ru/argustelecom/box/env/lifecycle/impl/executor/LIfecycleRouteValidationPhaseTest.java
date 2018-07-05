package ru.argustelecom.box.env.lifecycle.impl.executor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
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
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleValidator;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.context.LifecycleExecutionCtxImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.system.inf.validation.ValidationResult;

@RunWith(MockitoJUnitRunner.class)
public class LIfecycleRouteValidationPhaseTest extends LifecycleExecutionTestCase {

	@InjectMocks
	private LifecycleRouteValidationPhase phase;

	private LifecycleEndpointImpl<SampleState, Sample> endpoint;
	private LifecycleValidator<SampleState, Sample> validator;

	@Before
	@Override
	public void setup() {
		super.setup();
		endpoint = route.endpoints().iterator().next();
		executor.updateEndpoint(endpoint);
		executor.updateExecutionContext(new LifecycleExecutionCtxImpl<>(businessObject, route, endpoint, new Date()));

		validator = spy(SampleValidatorMock.class);
		endpoint.addValidator(validator);
	}

	@After
	@Override
	public void cleanup() {
		super.cleanup();
		endpoint = null;
		validator = null;
	}

	@Test
	public void shouldHaveExpectedPhaseIdentifier() {
		assertThat(phase.getId(), equalTo(LifecyclePhaseId.ROUTE_VALIDATION));
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

	@Test
	public void shouldPerformValidation() {
		phase.doPhase(executor);
		verify(validator, times(1)).validate(Mockito.any(), Mockito.any());
	}

	@Test
	public void shouldGlueValidationResultsOfDifferentValidators() {
		endpoint.validators().clear();
		endpoint.addValidator((ExecutionCtx<SampleState, ? extends Sample> ctx, ValidationResult<Object> result) -> {
			result.info(businessObject, "Info");
		});
		endpoint.addValidator((ExecutionCtx<SampleState, ? extends Sample> ctx, ValidationResult<Object> result) -> {
			result.warn(businessObject, "Warn");
		});
		endpoint.addValidator((ExecutionCtx<SampleState, ? extends Sample> ctx, ValidationResult<Object> result) -> {
			result.error(businessObject, "Error");
		});

		assertThat(executor.getValidationResult(), is(nullValue()));

		phase.doPhase(executor);

		assertThat(executor.getValidationResult(), is(notNullValue()));
		assertThat(executor.getValidationResult().getIssues().size(), equalTo(3));
		assertThat(executor.getValidationResult().hasInfos(), is(true));
		assertThat(executor.getValidationResult().hasWarnings(), is(true));
		assertThat(executor.getValidationResult().hasErrors(), is(true));
	}

	@Test
	public void shouldCallPhaseListener() {
		phase.doPhase(executor);
		verify(phaseListener, times(1)).beforeRouteValidation(Mockito.any());
		verify(phaseListener, times(1)).afterRouteValidation(Mockito.any(), Mockito.any());
	}

	@Test
	public void shouldCleanExecutorWhenBackingToPreviousPhase() {
		phase.doPhase(executor);
		assertThat(executor.getValidationResult(), is(notNullValue()));

		phase.clean(executor);
		assertThat(executor.getValidationResult(), is(nullValue()));
	}

	static class SampleValidatorMock implements LifecycleValidator<SampleState, Sample> {
		@Override
		public void validate(ExecutionCtx<SampleState, ? extends Sample> ctx, ValidationResult<Object> result) {
			// DO NOTHING
		}
	}

}
