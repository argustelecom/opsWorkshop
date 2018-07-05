package ru.argustelecom.box.env.billing.subscription.lifecycle.validator;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution.ALLOWED;
import static ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution.ALLOWED_WITH_DEBT;
import static ru.argustelecom.box.inf.nls.LocaleUtils.format;

import java.util.Date;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResult;
import ru.argustelecom.box.env.billing.subscription.SubscriptionProcessingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveBalanceForInvoice implements LifecycleCdiValidator<SubscriptionState, Subscription> {

	// FIXME Локализация после мержа
	private static final String MESSAGE_PREFIX = "Недостаточно средств на лицевом счете.";
	private static final String MESSAGE_RESERVING = "Схема тарификации с резервированием. Доступно: {0}, требуется: {1}, порог: {2}.";
	private static final String MESSAGE_SIMPLE = "Схема тарификации без резервирования. Доступно: {0}, порог: {1}.";

	@Inject
	private SubscriptionProcessingService processingSvc;

	@Override
	public void validate(ExecutionCtx<SubscriptionState, ? extends Subscription> ctx, ValidationResult<Object> result) {
		Subscription subscription = ctx.getBusinessObject();
		SubscriptionState toState = ctx.getEndpoint().getDestination();
		Date executionDate = ctx.getExecutionDate();

		checkState(toState.isChargeable());

		BalanceCheckingResult bcr = processingSvc.checkBalanceOnRouting(subscription, toState, executionDate, false);
		checkState(bcr != null);

		if (bcr.getResolution() != ALLOWED) {
			String message = explain(bcr);
			if (bcr.getResolution() == ALLOWED_WITH_DEBT && bcr.isTrustInDebt()) {
				result.warn(subscription, message);
			} else {
				result.error(subscription, message);
			}
		}
	}

	private String explain(BalanceCheckingResult bcr) {
		StringBuilder sb = new StringBuilder();
		sb.append(MESSAGE_PREFIX).append(" ");

		if (bcr.isReservingScheme()) {
			sb.append(format(MESSAGE_RESERVING, bcr.getAvailable(), bcr.getRequired(), bcr.getThreshold()));
		} else {
			sb.append(format(MESSAGE_SIMPLE, bcr.getAvailable(), bcr.getThreshold()));
		}

		return sb.toString();
	}
}
