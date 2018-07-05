package ru.argustelecom.box.env.saldo.export.model;

import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.saldo.nls.SaldoExportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Типы этапов выгрузки реестра Сальдо.
 */
@AllArgsConstructor
public enum SaldoExportEventType {

	//@formatter:off
	EXPORT_DATA,
	SEND_EXPORT_DATA,
	SEND_ERROR_MESSAGE;
	//@formatter:on

	public String getName() {
		SaldoExportMessagesBundle messages = LocaleUtils.getMessages(SaldoExportMessagesBundle.class);

		switch (this) {
			case EXPORT_DATA:
				return messages.eventTypeExportData();
			case SEND_EXPORT_DATA:
				return messages.eventTypeSend();
			case SEND_ERROR_MESSAGE:
				return messages.eventTypeSendErrorMessage();
			default:
				throw new SystemException("Unsupported SaldoExportEventState");
		}
	}
}