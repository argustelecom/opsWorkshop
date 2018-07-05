package ru.argustelecom.box.env.lifecycle.impl.executor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleFinalizationPhase;

@RunWith(MockitoJUnitRunner.class)
public class LifecycleFinalizationPhaseTest extends LifecycleExecutionTestCase {

	@InjectMocks
	private LifecycleFinalizationPhase phase;

	@Test
	public void shouldHaveExpectedPhaseIdentifier() {
		assertThat(phase.getId(), equalTo(LifecyclePhaseId.FINALIZATION));
	}

	@Test
	public void shouldNotifyObserversOnPhaseExecution() {
		phase.doPhase(executor);
		verify(notificator, times(1)).fireRoutingCompletedEvent(businessObject, executor.getBusinessObjectState());
	}

	@Test
	public void shouldSaveHistoryOnPhaseExecution() {
		phase.doPhase(executor);
		verify(history, times(1)).saveRoutingHistory(lifecycle, businessObject, executor.getBusinessObjectState());
	}

	@Test
	public void shouldCallPhaseListener() {
		phase.doPhase(executor);
		verify(phaseListener, times(1)).afterFinalization(Mockito.any(), Mockito.any());
	}

	@Test(expected = UnsupportedOperationException.class)
	public void shouldFailPhaseCleaning() {
		phase.clean(executor);
		fail("Expected exception didn't thrown!");
	}
}
