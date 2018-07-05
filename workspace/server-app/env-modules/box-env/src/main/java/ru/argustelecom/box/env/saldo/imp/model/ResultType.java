package ru.argustelecom.box.env.saldo.imp.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum ResultType {

	//@formatter:off
	NOT_SUITABLE        ("icon-close m-red"),
	REQUIRED_CORRECTION ("fa fa-question m-orange"),
	SUITABLE            ("icon-check m-green");
	//@formatter:on

	private String iconStyleClass;

	public String getName() {
		SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

		switch (this) {
			case NOT_SUITABLE:
				return messages.notSuitable();
			case REQUIRED_CORRECTION:
				return messages.requiredCorrections();
			case SUITABLE:
				return messages.suitable();
			default:
				throw new SystemException("Unsupported ResultType");
		}
	}

	public String getIconStyleClass() {
		return iconStyleClass;
	}

}