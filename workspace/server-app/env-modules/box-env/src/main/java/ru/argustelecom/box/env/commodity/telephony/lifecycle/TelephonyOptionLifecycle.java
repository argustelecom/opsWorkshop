package ru.argustelecom.box.env.commodity.telephony.lifecycle;

import static ru.argustelecom.box.env.commodity.telephony.lifecycle.TelephonyOptionLifecycle.Routes.ACTIVATE;
import static ru.argustelecom.box.env.commodity.telephony.lifecycle.TelephonyOptionLifecycle.Routes.DEACTIVATE;
import static ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState.ACTIVE;
import static ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState.INACTIVE;

import ru.argustelecom.box.env.commodity.telephony.lifecycle.actions.DoActivateTelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.lifecycle.actions.DoDeactivateTelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;

@LifecycleRegistrant
public class TelephonyOptionLifecycle implements LifecycleFactory<TelephonyOptionState, TelephonyOption> {

	@Override
	public void buildLifecycle(LifecycleBuilder<TelephonyOptionState, TelephonyOption> lifecycle) {
		lifecycle.keyword("TelephonyOptionLifecycle");
		lifecycle.name("Жизненный цикл опции телефонии");

		// @formatter:off
		lifecycle.route(ACTIVATE, "Активировать")
			.from(INACTIVE)
			.to(ACTIVE)
				.execute(DoActivateTelephonyOption.class)
			.end()
		.end();

		lifecycle.route(DEACTIVATE, "Деактивировать")
			.from(ACTIVE)
			.to(INACTIVE)
				.execute(DoDeactivateTelephonyOption.class)
			.end()
		.end();
		// @formatter:off
	}

	public enum Routes {
		ACTIVATE, DEACTIVATE
	}

}
