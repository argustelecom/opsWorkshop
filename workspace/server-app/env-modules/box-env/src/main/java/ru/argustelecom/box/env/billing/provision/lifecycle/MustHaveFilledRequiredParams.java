package ru.argustelecom.box.env.billing.provision.lifecycle;

import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveFilledRequiredParams implements LifecycleCdiValidator<RecurrentTermsState, RecurrentTerms> {

	private static final String EMPTY_PERIOD_TYPE = "Для условия предоставления \"{0}\" должен быть настроен \"Тип периода\"";
	private static final String EMPTY_PERIOD_DURATION = "Для условия предоставления \"{0}\" должно быть настроена \"Длительность периода списания\"";
	private static final String EMPTY_SUBS_LIFECYCLE_QUALIFIER = "Для условия предоставления \"{0}\" должен быть настроен \"Жизненный цик подписок\"";
	private static final String EMPTY_RESERVE_FUNDS = "Для условия предоставления \"{0}\" должно быть настроено правило \"Резервировать средства в начале периода\"";
	private static final String EMPTY_ROUNDING_POLICY = "Для условия предоставления \"{0}\" должно быть настроено правила \"Правило округления при закрытии инвойса\"";

	@Override
	public void validate(ExecutionCtx<RecurrentTermsState, ? extends RecurrentTerms> ctx,
			ValidationResult<Object> result) {
		RecurrentTerms recurrentTerms = ctx.getBusinessObject();

		checkPeriodType(recurrentTerms, result);
		checkPeriodDuration(recurrentTerms, result);
		checkSubsLifecycleQualifier(recurrentTerms, result);
		//checkReserveFunds(recurrentTerms, result);
		checkRoundingPolicy(recurrentTerms, result);
	}

	private void checkPeriodType(RecurrentTerms recurrentTerms, ValidationResult<Object> result) {
		if (recurrentTerms.getPeriodType() == null) {
			result.errorv(recurrentTerms, EMPTY_PERIOD_TYPE, recurrentTerms.getObjectName());
		}
	}

	private void checkPeriodDuration(RecurrentTerms recurrentTerms, ValidationResult<Object> result) {
		if (recurrentTerms.getChargingDuration() == null) {
			result.errorv(recurrentTerms, EMPTY_PERIOD_DURATION, recurrentTerms.getObjectName());
		}
	}

	private void checkSubsLifecycleQualifier(RecurrentTerms recurrentTerms, ValidationResult<Object> result) {
		if (recurrentTerms.getSubscriptionLifecycleQualifier() == null) {
			result.errorv(recurrentTerms, EMPTY_SUBS_LIFECYCLE_QUALIFIER, recurrentTerms.getObjectName());
		}
	}

//	private void checkReserveFunds(RecurrentTerms recurrentTerms, ValidationResult<Object> result) {
//		if (recurrentTerms.getReserveFunds() == null) {
//			result.errorv(recurrentTerms, EMPTY_RESERVE_FUNDS, recurrentTerms.getObjectName());
//		}
//	}

	private void checkRoundingPolicy(RecurrentTerms recurrentTerms, ValidationResult<Object> result) {
		if (recurrentTerms.getRoundingPolicy() == null) {
			result.errorv(recurrentTerms, EMPTY_ROUNDING_POLICY, recurrentTerms.getObjectName());
		}
	}

}