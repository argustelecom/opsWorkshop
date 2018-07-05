package ru.argustelecom.box.env.lifecycle.impl.definition.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleAction;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.definition.proxy.LifecycleActionProxy;

public class LifecycleActionProxyTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfActionIsNull() {
		LifecycleActionProxy.createSimple((SampleActionMock) null);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfCdiActionClassIsNull() {
		LifecycleActionProxy.createCdi((Class<SampleCdiActionMock>) null);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfCdiActionClassNotMarkedWithStereotype() {
		LifecycleActionProxy.createCdi(SampleInvalidCdiActionMock.class);
		fail("Expected exception didn't thrown!");
	}

	@Test
	public void shouldCreateProxy() {
		assertThat(LifecycleActionProxy.createSimple(new SampleActionMock()), is(notNullValue()));
		assertThat(LifecycleActionProxy.createCdi(SampleCdiActionMock.class), is(notNullValue()));
	}

	static class SampleActionMock implements LifecycleAction<SampleState, Sample> {
		@Override
		public void execute(ExecutionCtx<SampleState, ? extends Sample> ctx) {
			// DO NOTHING
		}
	}

	static class SampleInvalidCdiActionMock implements LifecycleCdiAction<SampleState, Sample> {
		@Override
		public void execute(ExecutionCtx<SampleState, ? extends Sample> ctx) {
			// DO NOTHING
		}
	}

	@LifecycleBean
	static class SampleCdiActionMock implements LifecycleCdiAction<SampleState, Sample> {
		@Override
		public void execute(ExecutionCtx<SampleState, ? extends Sample> ctx) {
			// DO NOTHING
		}
	}
}
