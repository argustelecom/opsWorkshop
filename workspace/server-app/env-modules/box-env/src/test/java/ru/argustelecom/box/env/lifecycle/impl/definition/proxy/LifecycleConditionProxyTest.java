package ru.argustelecom.box.env.lifecycle.impl.definition.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiCondition;
import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleCondition;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.definition.proxy.LifecycleConditionProxy;

public class LifecycleConditionProxyTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfConditionIsNull() {
		LifecycleConditionProxy.createSimple((SampleConditionMock) null);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfCdiConditionClassIsNull() {
		LifecycleConditionProxy.createCdi((Class<SampleCdiConditionMock>) null);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfCdiConditionClassNotMarkedWithStereotype() {
		LifecycleConditionProxy.createCdi(SampleInvalidCdiConditionMock.class);
		fail("Expected exception didn't thrown!");
	}

	@Test
	public void shouldCreateProxy() {
		assertThat(LifecycleConditionProxy.createSimple(new SampleConditionMock()), is(notNullValue()));
		assertThat(LifecycleConditionProxy.createCdi(SampleCdiConditionMock.class), is(notNullValue()));
	}

	static class SampleConditionMock implements LifecycleCondition<SampleState, Sample> {
		@Override
		public boolean test(TestingCtx<SampleState, ? extends Sample> ctx) {
			// DO NOTHING
			return true;
		}
	}

	static class SampleInvalidCdiConditionMock implements LifecycleCdiCondition<SampleState, Sample> {
		@Override
		public boolean test(TestingCtx<SampleState, ? extends Sample> ctx) {
			// DO NOTHING
			return true;
		}
	}

	@LifecycleBean
	static class SampleCdiConditionMock implements LifecycleCdiCondition<SampleState, Sample> {
		@Override
		public boolean test(TestingCtx<SampleState, ? extends Sample> ctx) {
			// DO NOTHING
			return true;
		}
	}
}
