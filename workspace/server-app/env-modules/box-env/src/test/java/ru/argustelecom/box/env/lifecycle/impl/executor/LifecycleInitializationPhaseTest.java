package ru.argustelecom.box.env.lifecycle.impl.executor;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleInitializationPhase;

@RunWith(MockitoJUnitRunner.class)
public class LifecycleInitializationPhaseTest extends LifecycleExecutionTestCase {

	@InjectMocks
	private LifecycleInitializationPhase phase;

	@Test
	public void shouldHaveExpectedPhaseIdentifier() {
		assertThat(phase.getId(), equalTo(LifecyclePhaseId.INITIALIZATION));
	}

	@Test
	public void shouldNotifyObserversOnPhaseExecution() {
		phase.doPhase(executor);
		verify(notificator, times(1)).fireRoutedFromEvent(businessObject);
	}
	
	@Test
	public void shouldCallPhaseListener() {
		phase.doPhase(executor);
		verify(phaseListener, times(1)).beforeInitialization(Mockito.any());
	}

}
