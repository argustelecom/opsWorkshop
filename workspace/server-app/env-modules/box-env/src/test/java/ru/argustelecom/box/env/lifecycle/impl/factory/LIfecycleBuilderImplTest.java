package ru.argustelecom.box.env.lifecycle.impl.factory;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import org.junit.Test;

import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.impl.Sample;
import ru.argustelecom.box.env.lifecycle.impl.SampleState;
import ru.argustelecom.box.env.lifecycle.impl.factory.LifecycleBuilderImpl;

public class LIfecycleBuilderImplTest {

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfNotBeginned() {
		LifecycleBuilderImpl<SampleState, Sample> builder = new LifecycleBuilderImpl<>();
		builder.build();
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfKeywordIsEmpty() {
		LifecycleBuilderImpl<SampleState, Sample> builder = new LifecycleBuilderImpl<>();
		builder.begin();
		builder.name("Some Lifecycle");
		builder.route("SomeRoute", "Some Route").from(SampleState.DRAFT).to(SampleState.ACTIVE).end().end();
		builder.build();
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfNameIsEmpty() {
		LifecycleBuilderImpl<SampleState, Sample> builder = new LifecycleBuilderImpl<>();
		builder.begin();
		builder.keyword("SomeLifecycle");
		builder.route("SomeRoute", "Some Route").from(SampleState.DRAFT).to(SampleState.ACTIVE).end().end();
		builder.build();
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfDontHaveRoutes() {
		LifecycleBuilderImpl<SampleState, Sample> builder = new LifecycleBuilderImpl<>();
		builder.begin();
		builder.keyword("SomeLifecycle");
		builder.name("Some Lifecycle");
		builder.build();
		fail("Expected exception didn't thrown!");
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailIfDefineDuplicateRoutes() {
		LifecycleBuilderImpl<SampleState, Sample> builder = new LifecycleBuilderImpl<>();
		builder.begin();
		builder.keyword("SomeLifecycle");
		builder.name("Some Lifecycle");
		builder.route("SomeRoute", "Some Route").from(SampleState.DRAFT).to(SampleState.ACTIVE).end().end();
		builder.route("SomeRoute", "Some Route").from(SampleState.DRAFT).to(SampleState.ACTIVE).end().end();
		builder.build();
		fail("Expected exception didn't thrown!");
	}

	@Test
	public void shouldBuildLifecycle() {
		LifecycleBuilderImpl<SampleState, Sample> builder = new LifecycleBuilderImpl<>();
		builder.begin();
		builder.keyword("SomeLifecycle");
		builder.name("Some Lifecycle");
		builder.route("SomeRoute", "Some Route").from(SampleState.DRAFT).to(SampleState.ACTIVE).end().end();

		Lifecycle<SampleState, Sample> lifecycle = builder.build();
		assertThat(lifecycle.hasRoutes(SampleState.DRAFT), is(true));
		assertThat(lifecycle.getMainRoute(SampleState.DRAFT), is(notNullValue()));
		assertThat(lifecycle.getRoutes(SampleState.DRAFT).size(), is(1));
		assertThat(lifecycle.getSecondaryRoutes(SampleState.DRAFT).size(), is(0));
	}
}
