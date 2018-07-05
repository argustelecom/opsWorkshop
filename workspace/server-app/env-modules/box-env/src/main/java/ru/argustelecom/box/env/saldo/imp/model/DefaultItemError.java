package ru.argustelecom.box.env.saldo.imp.model;

import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

public enum DefaultItemError implements ItemError {

	//@formatter:off
	TRYING_TO_RE_IMPORT,
	IMPOSSIBLE_DETERMINE_ACCOUNT,
	INCORRECT_ACCOUNT_NUMBER,
	INCORRECT_SUM,
	INCORRECT_PAYMENT_NUMBER,
	INCORRECT_PAYMENT_DOC_DATE;
	//@formatter:on

	public String getName() {
		SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

		switch (this) {
			case TRYING_TO_RE_IMPORT:
				return messages.defaultItemErrorTryingToReimport();
			case IMPOSSIBLE_DETERMINE_ACCOUNT:
				return messages.defaultItemErrorImpossibleDetermineAccount();
			case INCORRECT_ACCOUNT_NUMBER:
				return messages.defaultItemErrorIncorrectAccountNumber();
			case INCORRECT_SUM:
				return messages.defaultItemErrorIncorrectSum();
			case INCORRECT_PAYMENT_NUMBER:
				return messages.defaultItemErrorIncorrectPaymentNumber();
			case INCORRECT_PAYMENT_DOC_DATE:
				return messages.defaultItemErrorIncorrectPaymentDocDate();
			default:
				throw new SystemException("Unsupported DefaultItemError");
		}
	}

	@Override
	public String getDescription() {
		return getName();
	}
}