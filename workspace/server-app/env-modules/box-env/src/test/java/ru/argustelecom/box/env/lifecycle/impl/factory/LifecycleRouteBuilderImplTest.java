package ru.argustelecom.box.env.lifecycle.impl.factory;

import static org.junit.Assert.fail;

import org.junit.Test;

import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.factory.LifecycleBuilderImpl;
import ru.argustelecom.box.env.lifecycle.impl.factory.LifecycleRouteBuilderImpl;

public class LifecycleRouteBuilderImplTest {

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfNotBeginned() {
		LifecycleRouteBuilderImpl<SampleState, Sample> builder = createRouteBuilder();
		builder.end();
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfNotDefineStartpoints() {
		LifecycleRouteBuilderImpl<SampleState, Sample> builder = createRouteBuilder();
		builder.to(SampleState.ACTIVE).end();
		builder.end();
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfNotDefineEndpoints() {
		LifecycleRouteBuilderImpl<SampleState, Sample> builder = createRouteBuilder();
		builder.from(SampleState.DRAFT);
		builder.end();
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfNotDefineDefaultEndpoint() {
		LifecycleRouteBuilderImpl<SampleState, Sample> builder = createRouteBuilder();
		builder.from(SampleState.DRAFT);
		builder.to(SampleState.ACTIVE).when((TestingCtx<SampleState, ? extends Sample> ctx) -> false).end();
		builder.end();
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfDefineDuplicateEndpoints() {
		LifecycleRouteBuilderImpl<SampleState, Sample> builder = createRouteBuilder();
		builder.from(SampleState.DRAFT);
		builder.to(SampleState.ACTIVE).when((TestingCtx<SampleState, ? extends Sample> ctx) -> true).end();
		builder.to(SampleState.ACTIVE).when((TestingCtx<SampleState, ? extends Sample> ctx) -> true).end();
		fail("Expected exception didn't thrown!");
	}

	private LifecycleRouteBuilderImpl<SampleState, Sample> createRouteBuilder() {
		LifecycleBuilderImpl<SampleState, Sample> parent = new LifecycleBuilderImpl<>();
		LifecycleRouteBuilderImpl<SampleState, Sample> builder = new LifecycleRouteBuilderImpl<>(parent.begin());
		return builder;
	}

}
