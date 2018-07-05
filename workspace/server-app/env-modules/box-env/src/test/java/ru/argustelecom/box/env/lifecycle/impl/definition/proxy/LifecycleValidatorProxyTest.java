package ru.argustelecom.box.env.lifecycle.impl.definition.proxy;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleValidator;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.definition.proxy.LifecycleValidatorProxy;
import ru.argustelecom.system.inf.validation.ValidationResult;

public class LifecycleValidatorProxyTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfValidatorIsNull() {
		LifecycleValidatorProxy.createSimple((SampleValidatorMock) null);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfCdiValidatorClassIsNull() {
		LifecycleValidatorProxy.createCdi((Class<SampleCdiValidatorMock>) null);
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailIfCdiValidatorClassNotMarkedWithStereotype() {
		LifecycleValidatorProxy.createCdi(SampleInvalidCdiValidatorMock.class);
		fail("Expected exception didn't thrown!");
	}

	@Test
	public void shouldCreateProxy() {
		assertThat(LifecycleValidatorProxy.createSimple(new SampleValidatorMock()), is(notNullValue()));
		assertThat(LifecycleValidatorProxy.createCdi(SampleCdiValidatorMock.class), is(notNullValue()));
	}

	static class SampleValidatorMock implements LifecycleValidator<SampleState, Sample> {
		@Override
		public void validate(ExecutionCtx<SampleState, ? extends Sample> ctx, ValidationResult<Object> result) {
			// DO NOTHING
		}
	}

	static class SampleInvalidCdiValidatorMock implements LifecycleCdiValidator<SampleState, Sample> {
		@Override
		public void validate(ExecutionCtx<SampleState, ? extends Sample> ctx, ValidationResult<Object> result) {
			// DO NOTHING
		}
	}

	@LifecycleBean
	static class SampleCdiValidatorMock implements LifecycleCdiValidator<SampleState, Sample> {
		@Override
		public void validate(ExecutionCtx<SampleState, ? extends Sample> ctx, ValidationResult<Object> result) {
			// DO NOTHING
		}
	}
}
