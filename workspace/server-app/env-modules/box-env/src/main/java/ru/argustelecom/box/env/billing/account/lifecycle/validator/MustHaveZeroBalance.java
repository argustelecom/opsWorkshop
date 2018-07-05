package ru.argustelecom.box.env.billing.account.lifecycle.validator;

import javax.inject.Inject;

import lombok.val;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.account.model.PersonalAccountState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.validation.ValidationResult;

@LifecycleBean
public class MustHaveZeroBalance implements LifecycleCdiValidator<PersonalAccountState, PersonalAccount> {

	private static final String MESSAGE = "Невозможно закрыть лицевой счет: баланс на счете должен быть нулевым. Текущий {0} р.";

	@Inject
	private PersonalAccountBalanceService balanceService;

	@Override
	public void validate(ExecutionCtx<PersonalAccountState, ? extends PersonalAccount> ctx,
			ValidationResult<Object> result) {

		val personalAccount = ctx.getBusinessObject();
		val balance = balanceService.getBalance(personalAccount);

		if (balance.compareTo(Money.ZERO) > 0) {
			result.errorv(personalAccount, MESSAGE, balance.getRoundAmount());
		}
	}
}
