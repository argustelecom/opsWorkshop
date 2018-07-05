package ru.argustelecom.box.nri.logicalresources.phone.lifecycle;

import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.action.UpdateStateChangeDateAction;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.nls.PhoneNumberLifecycleMessagesBundle;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.validator.LockedPeriodMustBeExpired;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;

import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.AVAILABLE;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.DELETED;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.LOCKED;
import static ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState.OCCUPIED;

/**
 * Жизненный цикл телефонного номера
 * Created by s.kolyada on 27.10.2017.
 */
@LifecycleRegistrant
public class PhoneNumberLifecycle implements LifecycleFactory<PhoneNumberState, PhoneNumber> {

	/**
	 * Создать ЖЦ для телефонного номера
	 *
	 * @param lifecycle билдер ЖЦ
	 */
	@Override
	public void buildLifecycle(LifecycleBuilder<PhoneNumberState, PhoneNumber> lifecycle) {
		PhoneNumberLifecycleMessagesBundle messages = LocaleUtils.getMessages(PhoneNumberLifecycleMessagesBundle.class);
		lifecycle.keyword("PhoneNumberLifecycle");
		lifecycle.name(messages.lifecycleName());

		lifecycle.route(Routes.OCCUPY, messages.occupy())
				.from(AVAILABLE)
				.to(OCCUPIED)
				.execute(UpdateStateChangeDateAction.class)
				.end()
				.end();

		lifecycle.route(Routes.LOCK, messages.lock())
				.from(OCCUPIED)
				.to(LOCKED)
				.execute(UpdateStateChangeDateAction.class)
				.end()
				.end();

		lifecycle.route(Routes.UNLOCK, messages.unlock())
				.from(LOCKED)
				.to(AVAILABLE)
				.execute(UpdateStateChangeDateAction.class)
				.validate(LockedPeriodMustBeExpired.class)
				.end()
				.end();

		lifecycle.route(Routes.DELETE, messages.delete())
				.from(AVAILABLE)
				.from(LOCKED)
				.to(DELETED)
				.execute(UpdateStateChangeDateAction.class)
				.end()
				.end();
	}

	public enum Routes {
		OCCUPY, LOCK, UNLOCK, HOLD, DELETE
	}
}
