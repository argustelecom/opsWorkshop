package ru.argustelecom.box.env.billing.provision.lifecycle;

import static ru.argustelecom.box.env.billing.provision.lifecycle.RecurrentTermsLifecycle.Routes.ACTIVATE;
import static ru.argustelecom.box.env.billing.provision.lifecycle.RecurrentTermsLifecycle.Routes.ARCHIVATION;
import static ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState.ACTIVE;
import static ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState.ARCHIVE;
import static ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState.FORMALIZATION;

import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState;
import ru.argustelecom.box.env.billing.provision.nls.ProvisionTermsMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleRegistrant
public class RecurrentTermsLifecycle implements LifecycleFactory<RecurrentTermsState, RecurrentTerms> {

	private static final String LIFECYCLE_NAME = "Жизненный цикл переодического условия предоставления";

	@Override
	public void buildLifecycle(LifecycleBuilder<RecurrentTermsState, RecurrentTerms> lifecycle) {
		ProvisionTermsMessagesBundle messages = LocaleUtils.getMessages(ProvisionTermsMessagesBundle.class);

		lifecycle.keyword(this.getClass().getSimpleName());
		lifecycle.name(LIFECYCLE_NAME);

		// @formatter:off
		lifecycle.route(ACTIVATE, ACTIVATE.getName())
			.from(FORMALIZATION)
			.to(ACTIVE)
				.validate(MustHaveFilledRequiredParams.class)
			.end()
		.end();

		lifecycle.route(ARCHIVATION, ARCHIVATION.getName())
			.from(ACTIVE)
			.to(ARCHIVE)
				.validate((ExecutionCtx<RecurrentTermsState, ? extends RecurrentTerms> ctx, ValidationResult<Object> result) -> {
					result.warn(null, messages.warnBeforeClose());
				})
			.end()
		.end();
		// @formatter:on
	}

	public enum Routes {
		// @formatter:off
		ACTIVATE,
		ARCHIVATION;
		// @formatter:on

		public String getName() {
			ProvisionTermsMessagesBundle messages = LocaleUtils.getMessages(ProvisionTermsMessagesBundle.class);

			switch (this) {
				case ACTIVATE:
					return messages.routeActivate();
				case ARCHIVATION:
					return messages.routeClose();
				default:
					throw new SystemException("Unsupported lifecycle route");
			}
		}
	}

}