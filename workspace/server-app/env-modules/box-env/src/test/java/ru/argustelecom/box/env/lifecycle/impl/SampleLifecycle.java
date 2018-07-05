package ru.argustelecom.box.env.lifecycle.impl;

import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleImpl;
import ru.argustelecom.box.env.lifecycle.impl.factory.LifecycleBuilderImpl;

/**
 * НЕ МЕНЯЙ ЗДЕСЬ НИЧЕГО, ИНАЧЕ УПАДУТ ТЕСТЫ!!!!
 */
public class SampleLifecycle implements LifecycleFactory<SampleState, Sample> {

	public static LifecycleImpl<SampleState, Sample> create() {
		LifecycleBuilderImpl<SampleState, Sample> builder = new LifecycleBuilderImpl<>();
		new SampleLifecycle().buildLifecycle(builder.begin());
		return (LifecycleImpl<SampleState, Sample>) builder.build();
	}

	@Override
	public void buildLifecycle(LifecycleBuilder<SampleState, Sample> builder) {
		//@formatter:off
		builder.name("Демонстрационный ЖЦ");
		builder.keyword("SampleLifecycle");
		
		builder.route("WHAITING", "Поставить на ожидание")
			.controlledByUser(false)
			.from(SampleState.DRAFT)
			.to(SampleState.WHAITING)
			.end()
		.end();
		
		builder.route("ACTIVATE_OR_CLOSE", "Активировать или закрыть")
			.from(SampleState.WHAITING)
			.to(SampleState.ACTIVE)
				.when((TestingCtx<SampleState, ? extends Sample> ctx) -> {
					return ctx.getBusinessObject().getId() % 2L == 0;
				 })
			.end()
			.to(SampleState.DEACTIVATING)
				.when((TestingCtx<SampleState, ? extends Sample> ctx) -> {
					return ctx.getBusinessObject().getId() % 3L == 0;
				 })
			.end()
			.to(SampleState.CLOSED)
			.end()
		.end();
		
		//@formatter:on
	}
}
