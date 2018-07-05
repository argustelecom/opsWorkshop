package ru.argustelecom.box.env.saldo.export.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.saldo.nls.SaldoExportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Состояния описывающие результат выгрузки реестра Сальдо.
 */
@AllArgsConstructor(access = AccessLevel.MODULE)
public enum SaldoExportIssueState {

	//@formatter:off
	EXPORTED  ("icon-get_app fs20 m-bold-gray", "box.saldo.export.history.register.download.hint"),
	FAULTED   ("icon-error_outline fs20 m-red", "box.saldo.export.history.register.faulted.hint"),
	WAITING   ("icon-alarm fs20 m-blue", "box.saldo.export.history.register.waiting.hint"),
	RESTORED  ("icon-settings_backup_restore fs20 m-orange", "box.saldo.export.history.register.retry.hint");
	//@formatter:on

	@Getter
	private String iconStyle;
	@Getter
	private String title;


	public String getName() {
		SaldoExportMessagesBundle messages = LocaleUtils.getMessages(SaldoExportMessagesBundle.class);

		switch (this) {
			case EXPORTED:
				return messages.issueStateExported();
			case FAULTED:
				return messages.issueStateFaulted();
			case WAITING:
				return messages.issueStateWaiting();
			case RESTORED:
				return messages.issueStateRestored();
			default:
				throw new SystemException("Unsupported SaldoExportIssueState");
		}
	}

}