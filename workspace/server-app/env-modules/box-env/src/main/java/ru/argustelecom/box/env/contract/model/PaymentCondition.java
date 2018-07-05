package ru.argustelecom.box.env.contract.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Порядок оплаты за предоставляемые услуги. Сейчас определяются следующием методы расчёта:
 * <ul>
 * <li>Предоплата</li>
 * <li>Постоплата</li>
 * </ul>
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PaymentCondition {

	PREPAYMENT,
	POSTPAYMENT;

	public String getName() {
		ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);

		switch (this) {
			case PREPAYMENT:
				return messages.paymentConditionPrepayment();
			case POSTPAYMENT:
				return messages.paymentConditionPostpayment();
			default:
				throw new SystemException("Unsupported PaymentCondition");
		}
	}

	@Override
	public String toString() {
		return getName();
	}

}