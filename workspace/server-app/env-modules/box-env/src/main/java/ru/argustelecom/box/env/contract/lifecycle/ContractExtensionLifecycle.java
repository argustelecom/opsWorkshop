package ru.argustelecom.box.env.contract.lifecycle;

import static ru.argustelecom.box.env.contract.lifecycle.ContractExtensionLifecycle.Behaviors.documentDate;
import static ru.argustelecom.box.env.contract.lifecycle.ContractExtensionLifecycle.Behaviors.editEntry;
import static ru.argustelecom.box.env.contract.lifecycle.ContractExtensionLifecycle.Behaviors.order;
import static ru.argustelecom.box.env.contract.lifecycle.ContractExtensionLifecycle.Behaviors.remove;
import static ru.argustelecom.box.env.contract.model.ContractState.CANCELLED;
import static ru.argustelecom.box.env.contract.model.ContractState.INFORCE;
import static ru.argustelecom.box.env.contract.model.ContractState.REGISTRATION;
import static ru.argustelecom.box.env.contract.model.ContractState.TERMINATED;

import ru.argustelecom.box.env.contract.lifecycle.action.DoActivateOptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoActivateSubscriptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoDeactivateExcludedOptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoDeactivateOptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoTerminateActiveSubscriptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoTerminateExcludedSubscriptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoWriteComment;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHaveAnyEntryInExtension;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHaveNoEntriesInExtension;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHaveSubscriptionForEachEntry;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustStillValid;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustWarnIfHaveSubscriptions;
import ru.argustelecom.box.env.contract.model.ContractExtension;
import ru.argustelecom.box.env.contract.model.ContractExtensionType;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@LifecycleRegistrant
public class ContractExtensionLifecycle extends AbstractContractLifecycle<ContractExtensionType, ContractExtension> {

	private static final String LIFECYCLE_NAME = "Жизненный цикл доп. соглашения";

	@Override
	public void buildLifecycle(LifecycleBuilder<ContractState, ContractExtension> lifecycle) {

		//@formatter:off
		lifecycle.keyword(this.getClass().getSimpleName());
		lifecycle.name(LIFECYCLE_NAME);

		lifecycle.route(Routes.ACTIVATE, Routes.ACTIVATE.getName())
			.from(REGISTRATION)
			.to(INFORCE)
				.silent(false)
				.contextVar(AbstractContractLifecycle.Variables.COMMENT)
				.validate(MustStillValid.class)
				.validate(MustHaveAnyEntryInExtension.class)
				.validate(MustHaveSubscriptionForEachEntry.class)
				.execute(DoWriteComment.class)
				.execute(DoActivateSubscriptions.class)
				.execute(DoActivateOptions.class)
				.execute(DoTerminateExcludedSubscriptions.class)
				.execute(DoDeactivateExcludedOptions.class)
			.end()
		.end();	

		lifecycle.route(Routes.CANCEL, Routes.CANCEL.getName())
			.from(REGISTRATION)
			.to(CANCELLED)
				.silent(false)
				.contextVar(AbstractContractLifecycle.Variables.COMMENT)
				.validate(MustHaveNoEntriesInExtension.class)
				.execute(DoWriteComment.class)
			.end()
		.end();	

		lifecycle.route(Routes.TERMINATE, Routes.TERMINATE.getName())
			.from(INFORCE)
			.to(TERMINATED)
				.silent(false)
				.contextVar(AbstractContractLifecycle.Variables.COMMENT)
				.validate(MustWarnIfHaveSubscriptions.class)
				.execute(DoWriteComment.class)
				.execute(DoTerminateActiveSubscriptions.class)
				.execute(DoDeactivateOptions.class)
			.end()
		.end();	

		// запрещение действий для статуса "Оформление"
		lifecycle
			.forbid(REGISTRATION, remove);

		// запрещение действий для статуса "Аннулирован"
		lifecycle
			.forbid(CANCELLED, order)
			.forbid(CANCELLED, documentDate)
			.forbid(CANCELLED, editEntry);

		// запрещение действий для статуса "Действует"
		lifecycle
			.forbid(INFORCE, order)
			.forbid(INFORCE, documentDate)
			.forbid(INFORCE, editEntry)
			.forbid(INFORCE, remove);

		// запрещение действий для статуса "Закрыт"
		lifecycle
			.forbid(TERMINATED, order)
			.forbid(TERMINATED, documentDate)
			.forbid(TERMINATED, editEntry)
			.forbid(TERMINATED, remove);
		//@formatter:on
	}

	public enum Routes {

		ACTIVATE, CANCEL, TERMINATE;

		public String getName() {
			ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);

			switch (this) {
			case ACTIVATE:
				return messages.routeActivate();
			case CANCEL:
				return messages.routeCancel();
			case TERMINATE:
				return messages.routeTerminate();
			default:
				throw new SystemException("Unsupported ContractExtensionLifecycle.Routes");
			}
		}
	}

	public enum Behaviors {
		order, documentDate, editEntry, remove
	}
}
