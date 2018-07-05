package ru.argustelecom.box.env.lifecycle.impl.executor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.hasItem;
import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleInitializationPhase;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecyclePhase;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleRouteDefinitionPhase;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleRouteExecutionPhase;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleRouteValidationPhase;

@RunWith(MockitoJUnitRunner.class)
public class LifecycleExecutorImplTest extends LifecycleExecutionTestCase {

	@Test
	public void shouldCreateExecutionPlan() {
		List<LifecyclePhase> plan;

		plan = executor.createExecutionPlan(LifecyclePhaseId.INITIALIZATION);
		assertThat(plan.size(), equalTo(1));
		assertThat(plan, hasItem(instanceOf(LifecycleInitializationPhase.class)));

		plan = executor.createExecutionPlan(LifecyclePhaseId.ROUTE_EXECUTION);
		assertThat(plan.size(), equalTo(4));
		assertThat(plan, hasItem(instanceOf(LifecycleInitializationPhase.class)));
		assertThat(plan, hasItem(instanceOf(LifecycleRouteDefinitionPhase.class)));
		assertThat(plan, hasItem(instanceOf(LifecycleRouteValidationPhase.class)));
		assertThat(plan, hasItem(instanceOf(LifecycleRouteExecutionPhase.class)));

		executor.setCurrentPhaseId(LifecyclePhaseId.ROUTE_DEFINITION);
		plan = executor.createExecutionPlan(LifecyclePhaseId.ROUTE_EXECUTION);
		assertThat(plan.size(), equalTo(2));
		assertThat(plan, hasItem(instanceOf(LifecycleRouteValidationPhase.class)));
		assertThat(plan, hasItem(instanceOf(LifecycleRouteExecutionPhase.class)));

		plan = executor.createExecutionPlan(LifecyclePhaseId.ROUTE_DEFINITION);
		assertThat(plan.size(), equalTo(0));
	}

	@Test
	public void shouldExecutePhases() {
		List<LifecyclePhase> plan;

		plan = executor.createExecutionPlan(LifecyclePhaseId.INITIALIZATION);
		executor.initializeRouting();
		assertThatExecutionComplete(plan, LifecyclePhaseId.INITIALIZATION);

		plan = executor.createExecutionPlan(LifecyclePhaseId.ROUTE_DEFINITION);
		executor.determineRouteEndpoint();
		assertThatExecutionComplete(plan, LifecyclePhaseId.ROUTE_DEFINITION);

		plan = executor.createExecutionPlan(LifecyclePhaseId.ROUTE_VALIDATION);
		executor.validateRoute();
		assertThatExecutionComplete(plan, LifecyclePhaseId.ROUTE_VALIDATION);

		plan = executor.createExecutionPlan(LifecyclePhaseId.ROUTE_EXECUTION);
		executor.executeRoute();
		assertThatExecutionComplete(plan, LifecyclePhaseId.ROUTE_EXECUTION);

		plan = executor.createExecutionPlan(LifecyclePhaseId.FINALIZATION);
		executor.finalizeRouting();
		assertThatExecutionComplete(plan, LifecyclePhaseId.FINALIZATION);
	}

	@Test
	public void shouldExecuteEachPhaseOfExecutionPlanOnlyOnce() {
		List<LifecyclePhase> fullPlan = executor.createExecutionPlan(LifecyclePhaseId.FINALIZATION);

		executor.initializeRouting();
		executor.finalizeRouting();
		executor.initializeRouting();
		executor.finalizeRouting();

		assertThatExecutionComplete(fullPlan, LifecyclePhaseId.FINALIZATION);
	}

	@Test
	public void shouldBackToPreviousPhase() {
		executor.setCurrentPhaseId(LifecyclePhaseId.INITIALIZATION);
		assertThat(executor.canBackToPreviousPhase(), is(true));
		assertThat(executor.backToPreviousPhase(), is(true));
		assertThat(executor.getCurrentPhaseId(), is(nullValue()));

		executor.setCurrentPhaseId(LifecyclePhaseId.ROUTE_VALIDATION);
		assertThat(executor.canBackToPreviousPhase(), is(true));
		assertThat(executor.backToPreviousPhase(), is(true));
		assertThat(executor.getCurrentPhaseId(), is(LifecyclePhaseId.ROUTE_DEFINITION));

		executor.setCurrentPhaseId(LifecyclePhaseId.ROUTE_EXECUTION);
		assertThat(executor.canBackToPreviousPhase(), is(false));
		assertThat(executor.backToPreviousPhase(), is(false));
		assertThat(executor.getCurrentPhaseId(), is(LifecyclePhaseId.ROUTE_EXECUTION));

		executor.setCurrentPhaseId(null);
		assertThat(executor.canBackToPreviousPhase(), is(false));
		assertThat(executor.backToPreviousPhase(), is(false));
	}

	@Test
	public void shouldCallExecutorCleaningWhenBackingToPreviousPhase() {
		executor.setCurrentPhaseId(LifecyclePhaseId.ROUTE_VALIDATION);

		executor.backToPreviousPhase();
		verify(executor.getPhase(LifecyclePhaseId.ROUTE_VALIDATION), times(1)).clean(executor);

		executor.backToPreviousPhase();
		verify(executor.getPhase(LifecyclePhaseId.ROUTE_DEFINITION), times(1)).clean(executor);

		executor.backToPreviousPhase();
		verify(executor.getPhase(LifecyclePhaseId.INITIALIZATION), times(1)).clean(executor);
	}

	private void assertThatExecutionComplete(List<LifecyclePhase> plan, LifecyclePhaseId finalPhaseId) {
		plan.forEach(phase -> {
			verify(phase, times(1)).doPhase(executor);
			verify(phase, times(1)).execute(executor);
		});
		assertThat(executor.getCurrentPhaseId(), equalTo(finalPhaseId));
	}
}
