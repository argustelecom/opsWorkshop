package ru.argustelecom.box.env.lifecycle.impl.executor;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleExecutorImpl;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecyclePhase;

@RunWith(MockitoJUnitRunner.class)
public class LifecyclePhaseTest extends LifecycleExecutionTestCase {
	
	@Spy
	private LifecyclePhaseMock phase;
	
	@Test
	public void shouldNeverCallExecuteMethodIfExecutorIsAlreadyInAppropriatePhase() {
		executor.setCurrentPhaseId(LifecyclePhaseId.INITIALIZATION);
		phase.doPhase(executor);
		verify(phase, never()).execute(executor);
	}
	
	private static class LifecyclePhaseMock extends LifecyclePhase {

		private static final long serialVersionUID = 1L;

		@Override
		public LifecyclePhaseId getId() {
			return LifecyclePhaseId.INITIALIZATION;
		}

		@Override
		protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void execute(
				LifecycleExecutorImpl<S, O> executor) {
			// DO NOTHING
		}
	}
	
}
