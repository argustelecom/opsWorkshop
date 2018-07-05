package ru.argustelecom.box.env.billing.account.lifecycle;

import static ru.argustelecom.box.env.billing.account.model.PersonalAccountState.ACTIVE;
import static ru.argustelecom.box.env.billing.account.model.PersonalAccountState.CLOSED;

import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.billing.account.lifecycle.validator.MustHaveClosedSubscriptionsOnly;
import ru.argustelecom.box.env.billing.account.lifecycle.validator.MustHaveZeroBalance;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.model.PersonalAccountState;
import ru.argustelecom.box.env.billing.account.nls.PersonalAccountMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@LifecycleRegistrant
public class PersonalAccountLifecycle implements LifecycleFactory<PersonalAccountState, PersonalAccount> {

	@Override
	public void buildLifecycle(LifecycleBuilder<PersonalAccountState, PersonalAccount> lifecycle) {

		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name("Жизненный цикл лицевого счета");

		// @formatter:off
        lifecycle.route(Routes.CLOSE, Routes.CLOSE.getName())
            .from(ACTIVE)
            .to(CLOSED)
                .validate(MustHaveZeroBalance.class)
                .validate(MustHaveClosedSubscriptionsOnly.class)
            .end()
        .end();
        // @formatter:on
	}

	public enum Routes {

		CLOSE;

		public String getName() {
			PersonalAccountMessagesBundle messages = LocaleUtils.getMessages(PersonalAccountMessagesBundle.class);
			switch (this) {
				case CLOSE:
					return messages.routeClose();
				default:
					throw new SystemException("Unsupported PersonalAccountLifecycle.Routes");
			}
		}
	}
}
