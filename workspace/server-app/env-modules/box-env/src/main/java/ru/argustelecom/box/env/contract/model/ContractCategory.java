package ru.argustelecom.box.env.contract.model;

import ru.argustelecom.box.env.contract.nls.ContractMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Категория договора
 * 
 * - двусторонний договор
 * - трехсторонний (агентский) договор
 *
 */
public enum ContractCategory {

	BILATERAL, AGENCY;

	public String getName() {
		ContractMessagesBundle messages = LocaleUtils.getMessages(ContractMessagesBundle.class);
		switch (this) {
		case BILATERAL:
			return messages.bilateralCategory();
		case AGENCY:
			return messages.agencyCategory();
		default:
			throw new SystemException("Unsupported ContractCategory");
		}
	}

}
