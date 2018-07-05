package ru.argustelecom.box.env.lifecycle.impl.executor;

import static org.mockito.Mockito.doCallRealMethod;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;

import java.util.Date;

import javax.persistence.EntityManager;

import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.mockito.Answers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleContextConfigurator;
import ru.argustelecom.box.env.lifecycle.api.history.model.Initiator;
import ru.argustelecom.box.env.lifecycle.api.history.model.InitiatorType;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.env.lifecycle.impl.LifecycleHistoryRepository;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleLifecycle;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;
import ru.argustelecom.box.env.lifecycle.impl.event.LifecycleNotificator;

@RunWith(MockitoJUnitRunner.class)
public abstract class LifecycleExecutionTestCase {

	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	EntityManager em;

	@Mock(answer = Answers.RETURNS_SMART_NULLS)
	LifecycleNotificator notificator;

	@Mock
	LifecycleHistoryRepository history;

	@Mock
	LifecyclePhaseListener<SampleState, Sample> phaseListener;

	Sample businessObject;
	LifecycleImpl<SampleState, Sample> lifecycle;
	LifecycleRouteImpl<SampleState, Sample> route;
	LifecycleExecutorImpl<SampleState, Sample> executor;
	LifecycleContextConfigurator<SampleState, Sample> contextConfigurator;

	@Before
	public void setup() {
		this.businessObject = new Sample(1L);
		this.lifecycle = SampleLifecycle.create();
		this.route = (LifecycleRouteImpl<SampleState, Sample>) lifecycle.getMainRoute(businessObject);

		this.executor = new LifecycleExecutorMock(businessObject, lifecycle, route);
		this.executor.addPhaseListener(this.phaseListener);

		this.contextConfigurator = spy(LifecycleConfiguratorMock.class);
		this.lifecycle.addConfigurator(this.contextConfigurator);

		//@formatter:off
		LifecycleHistoryItem historyItem = LifecycleHistoryItem.builder()
			.id(100500L)
			.lifecycleObjectId(this.businessObject.getId())
			.lifecycleObjectEntity(this.businessObject.getClass().getSimpleName())
			.lifecycle(this.lifecycle.getKeyword().toString())
			.initiator(Initiator.of(InitiatorType.QUEUE, "TEST"))
			.fromState(SampleState.DRAFT.toString())
			.toState(SampleState.ACTIVE.toString())
			.transitionTime(new Date())
		.build();
		//@formatter:on

		doReturn(historyItem).when(history).saveRoutingHistory(Mockito.any(), Mockito.any(), Mockito.any());
	}

	@After
	public void cleanup() {
		this.businessObject = null;
		this.lifecycle = null;
		this.route = null;
		this.executor = null;
		this.contextConfigurator = null;
	}

	static class LifecycleExecutorMock extends LifecycleExecutorImpl<SampleState, Sample> {
		public LifecycleExecutorMock(Sample businessObject, LifecycleImpl<SampleState, Sample> lifecycle,
				LifecycleRouteImpl<SampleState, Sample> route) {
			super(businessObject, lifecycle, route, new Date());
		}

		@Override
		protected <T extends LifecyclePhase> T createPhase(Class<T> phaseClass) {
			T phaseMock = mock(phaseClass);
			doCallRealMethod().when(phaseMock).getId();
			doCallRealMethod().when(phaseMock).doPhase(Mockito.any());
			return phaseMock;
		}
	}

	static class LifecycleConfiguratorMock implements LifecycleContextConfigurator<SampleState, Sample> {

		@Override
		public void configure(ExecutionCtx<SampleState, ? extends Sample> context) {
			// DO NOTHING
		}

	}
}
