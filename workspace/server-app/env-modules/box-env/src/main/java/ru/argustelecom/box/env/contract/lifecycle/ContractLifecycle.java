package ru.argustelecom.box.env.contract.lifecycle;

import static ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle.Behaviors.editEntry;
import static ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle.Behaviors.editExtension;
import static ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle.Behaviors.editPaymentCondition;
import static ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle.Behaviors.order;
import static ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle.Behaviors.remove;
import static ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle.Behaviors.validFrom;
import static ru.argustelecom.box.env.contract.lifecycle.ContractLifecycle.Behaviors.validTo;
import static ru.argustelecom.box.env.contract.model.ContractState.CANCELLED;
import static ru.argustelecom.box.env.contract.model.ContractState.INFORCE;
import static ru.argustelecom.box.env.contract.model.ContractState.REGISTRATION;
import static ru.argustelecom.box.env.contract.model.ContractState.TERMINATED;

import ru.argustelecom.box.env.contract.lifecycle.action.DoActivateOptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoActivateSubscriptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoDeactivateOptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoTerminateActiveExtensions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoTerminateActiveSubscriptions;
import ru.argustelecom.box.env.contract.lifecycle.action.DoWriteComment;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHaveAnyEntryInContract;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHaveInforcedOptionAgencyContract;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHaveIntersectedPrefixesInServiceTariffs;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHaveNoEntriesInContract;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHavePaymentCondition;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustHaveSubscriptionForEachEntry;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustStillValid;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustWarnIfHaveExtensions;
import ru.argustelecom.box.env.contract.lifecycle.validator.MustWarnIfHaveSubscriptions;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleBuilder;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleRegistrant;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@LifecycleRegistrant
public class ContractLifecycle extends AbstractContractLifecycle<ContractType, Contract> {

	@Override
	public void buildLifecycle(LifecycleBuilder<ContractState, Contract> lifecycle) {

		//@formatter:off
		lifecycle.keyword(getClass().getSimpleName());
		lifecycle.name("Жизненный цикл договора");

		lifecycle.route(Routes.ACTIVATE, Routes.ACTIVATE.getName())
			.from(REGISTRATION)
			.to(INFORCE)
				.silent(false)
				.contextVar(AbstractContractLifecycle.Variables.COMMENT)
				.validate(MustHavePaymentCondition.class)
				.validate(MustStillValid.class)
				.validate(MustHaveAnyEntryInContract.class)
				.validate(MustHaveSubscriptionForEachEntry.class)
				.validate(MustHaveInforcedOptionAgencyContract.class)
				.validate(MustHaveIntersectedPrefixesInServiceTariffs.class)

// 				Раскомментируй для отладки, если не хочешь пускать объект дальше по ЖЦ
//				.validate((ExecutionCtx<ContractState, ? extends Contract> ctx, ValidationResult<Object> res) -> {
//					res.error(ctx.getBusinessObject(), "Операцию невозможно выполнить при отладке!");
//				 })
				
				.execute(DoWriteComment.class)
				.execute(DoActivateSubscriptions.class)
				.execute(DoActivateOptions.class)
			.end()
		.end();

		lifecycle.route(Routes.CANCEL, Routes.CANCEL.getName())
			.from(REGISTRATION)
			.to(CANCELLED)
				.silent(false)
				.contextVar(AbstractContractLifecycle.Variables.COMMENT)
				.validate(MustHaveNoEntriesInContract.class)
				.execute(DoWriteComment.class)
			.end()
		.end();

		lifecycle.route(Routes.TERMINATE, Routes.TERMINATE.getName())
			.from(INFORCE)
			.to(TERMINATED)
				.silent(false)
				.contextVar(AbstractContractLifecycle.Variables.COMMENT)
				.validate(MustWarnIfHaveSubscriptions.class)
				.validate(MustWarnIfHaveExtensions.class)
				.execute(DoWriteComment.class)
				.execute(DoTerminateActiveSubscriptions.class)
				.execute(DoTerminateActiveExtensions.class)
				.execute(DoDeactivateOptions.class)
			.end()
		.end();

		// запрещение действий для статуса "Оформление"
		lifecycle
			.forbid(REGISTRATION, editExtension)
			.forbid(REGISTRATION, remove);

		// запрещение действий для статуса "Аннулирован"
		lifecycle
			.forbid(CANCELLED, order)
			.forbid(CANCELLED, validFrom)
			.forbid(CANCELLED, validTo)
			.forbid(CANCELLED, editEntry)
			.forbid(CANCELLED, editExtension)
			.forbid(CANCELLED, editPaymentCondition);

		// запрещение действий для статуса "Действует"
		lifecycle
			.forbid(INFORCE, order)
			.forbid(INFORCE, validFrom)
			.forbid(INFORCE, validTo)
			.forbid(INFORCE, editEntry)
			.forbid(INFORCE, remove)
			.forbid(INFORCE, editPaymentCondition);

		// запрещение действий для статуса "Закрыт"
		lifecycle
			.forbid(TERMINATED, order)
			.forbid(TERMINATED, validFrom)
			.forbid(TERMINATED, validTo)
			.forbid(TERMINATED, editEntry)
			.forbid(TERMINATED, editExtension)
			.forbid(TERMINATED, remove)
			.forbid(TERMINATED, editPaymentCondition);

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
				throw new SystemException("Unsupported ContractLifecycle.Routes");
			}
		}
	}

	public enum Behaviors {
		validFrom, validTo, order, editEntry, editExtension, remove, editPaymentCondition
	}

}
