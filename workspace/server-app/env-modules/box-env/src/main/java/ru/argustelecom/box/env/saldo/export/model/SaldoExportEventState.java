package ru.argustelecom.box.env.saldo.export.model;

import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.saldo.nls.SaldoExportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor
public enum SaldoExportEventState {

	//@formatter:off
	SUCCESSFULLY,
	UNSUCCESSFUL;
	//@formatter:on

	public String getName() {
		SaldoExportMessagesBundle messages = LocaleUtils.getMessages(SaldoExportMessagesBundle.class);

		switch (this) {
			case SUCCESSFULLY:
				return messages.eventStateSuccessfully();
			case UNSUCCESSFUL:
				return messages.eventStateUnsuccessfully();
			default:
				throw new SystemException("Unsupported SaldoExportEventState");
		}
	}

}