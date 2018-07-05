package ru.argustelecom.box.env.telephony.tariff.lifecycle;

import lombok.val;
import java.util.Date;

import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleFactory;
import ru.argustelecom.box.env.pricing.nls.PricelistMessagesBundle;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TariffState;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffMessagesBundle;
import ru.argustelecom.box.env.type.model.properties.TextProperty;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.validation.ValidationResult;

import static ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable.of;
import static ru.argustelecom.box.env.telephony.tariff.lifecycle.AbstractTariffLifecycle.Behaviors.editAttributes;
import static ru.argustelecom.box.env.telephony.tariff.lifecycle.AbstractTariffLifecycle.Behaviors.editEntry;
import static ru.argustelecom.box.env.telephony.tariff.lifecycle.AbstractTariffLifecycle.Behaviors.modifyEntries;
import static ru.argustelecom.box.env.telephony.tariff.lifecycle.AbstractTariffLifecycle.Behaviors.remove;
import static ru.argustelecom.box.env.telephony.tariff.lifecycle.AbstractTariffLifecycle.Behaviors.validFrom;
import static ru.argustelecom.box.env.telephony.tariff.lifecycle.AbstractTariffLifecycle.Behaviors.validTo;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.ACTIVE;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.ARCHIVE;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.CANCELLED;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.FORMALIZATION;

public abstract class AbstractTariffLifecycle<P extends AbstractTariff> implements LifecycleFactory<TariffState, P> {
	@Override
	public void buildLifecycle(LifecycleBuilder<TariffState, P> lifecycle) {
		TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);

		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name("Жизненный цикл тарифа");

		// @formatter:off

		lifecycle.route(Routes.ACTIVATE, Routes.ACTIVATE.getName())
				.from(FORMALIZATION)
				.to(ACTIVE)
				.silent(false)
				.contextVar(Variables.COMMENT)
				.validate((ExecutionCtx<TariffState, ? extends AbstractTariff> ctx, ValidationResult<Object> result) -> {
					val tariff = EntityManagerUtils.initializeAndUnproxy(ctx.getBusinessObject());
					Date tariffEndDate = tariff.getValidTo();
					Date currentDate = new Date();

					if (tariff.getEntries().isEmpty()) {
						result.errorv(tariff, messages.notContainsEntries());
					}

					if (tariffEndDate != null && tariffEndDate.before(currentDate)) {
						result.errorv(tariff, messages.validToBeforeCurrentDate());
					}
				})
				.end()
				.end();

		lifecycle.route(Routes.CANCEL, Routes.CANCEL.getName())
				.from(FORMALIZATION)
				.to(CANCELLED)
				.silent(false)
				.end()
				.end();

		lifecycle.route(Routes.ARCHIVE, Routes.ARCHIVE.getName())
				.from(ACTIVE)
				.to(ARCHIVE)
				.silent(false)
				.contextVar(Variables.COMMENT)
				.end()
				.end();
		//@formatter:on

		lifecycle.forbid(FORMALIZATION, remove);
		lifecycle.forbid(ACTIVE, remove, editAttributes, validFrom, validTo);
		lifecycle.forbid(ARCHIVE, remove, editAttributes, modifyEntries, editEntry, validFrom, validTo);
		lifecycle.forbid(CANCELLED, editAttributes, modifyEntries, editEntry, validFrom, validTo);
	}

	public enum Routes {
		ACTIVATE, ARCHIVE, CANCEL;

		public String getName() {

			TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);

			switch (this) {
				case ACTIVATE:
					return messages.routeActivate();
				case CANCEL:
					return messages.routeCancel();
				case ARCHIVE:
					return messages.routeClose();
				default:
					throw new SystemException("Unsupported AbstractTariffLifecycle.Route");
			}
		}
	}

	public static final class Variables {

		public static final LifecycleVariable<TextProperty> COMMENT = of(TextProperty.class, var -> {
			PricelistMessagesBundle messages = LocaleUtils.getMessages(PricelistMessagesBundle.class);

			var.setName(messages.lifecycleVariableComment());
			var.setHint(messages.lifecycleVariableCommentHint());
			var.setLinesCount(5);
		});

		private Variables() {
		}
	}

	public enum Behaviors {
		remove, editAttributes, modifyEntries, editEntry, validFrom, validTo
	}
}
