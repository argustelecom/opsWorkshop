package ru.argustelecom.box.env.commodity.lifecycle;

import static ru.argustelecom.box.env.commodity.model.ServiceState.ACTIVE;
import static ru.argustelecom.box.env.commodity.model.ServiceState.INACTIVE;

import ru.argustelecom.box.env.commodity.lifecycle.action.DoActivateOptions;
import ru.argustelecom.box.env.commodity.lifecycle.action.DoDeactivateOptions;
import ru.argustelecom.box.env.commodity.lifecycle.action.DoScheduleServiceCharging;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;

@LifecycleRegistrant
public class ServiceLifecycle implements LifecycleFactory<ServiceState, Service> {

	@Override
	public void buildLifecycle(LifecycleBuilder<ServiceState, Service> lifecycle) {
		lifecycle.keyword("ServiceLifecycle");
		lifecycle.name("Жизненный цикл услуги");

		// @formatter:off
		lifecycle.route(Routes.ACTIVATE, "Активировать")
			.from(INACTIVE)
			.to(ACTIVE)
				.execute(DoScheduleServiceCharging.class)
				.execute(DoActivateOptions.class)
			.end()
		.end();

		lifecycle.route(Routes.DEACTIVATE, "Деактивировать")
			.from(ACTIVE)
			.to(INACTIVE)
				.execute(DoDeactivateOptions.class)
			.end()
		.end();

	}

	public enum Routes {
		ACTIVATE, DEACTIVATE
	}
	
}
