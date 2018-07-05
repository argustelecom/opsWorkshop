package ru.argustelecom.box.env.billing.bill.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Метод группировки, определяющий объект, в рамках которого будут сгрупированны начисления/списания по всем подпискам
 * клиента. Определяется два возможных способа группировки, по:
 * <ul>
 * <li>договору</li>
 * <li>лицевому счёту</li>
 * </ul>
 */
@Getter
@AllArgsConstructor
public enum GroupingMethod {

	CONTRACT(Contract.class),
	PERSONAL_ACCOUNT( PersonalAccount.class);

	private Class<? extends Identifiable> entityClass;

	public String getName() {
		BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);

		switch (this) {
			case CONTRACT:
				return messages.billGroupingMethodContract();
			case PERSONAL_ACCOUNT:
				return  messages.billGroupingMethodPersonalAccount();
			default:
				throw new SystemException("Unsupported GroupingMethod");
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}