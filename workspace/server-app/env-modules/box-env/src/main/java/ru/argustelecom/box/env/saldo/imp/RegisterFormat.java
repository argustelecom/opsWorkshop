package ru.argustelecom.box.env.saldo.imp;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor(access = AccessLevel.MODULE)
public enum RegisterFormat {

	//@formatter:off
	SALDO	(SaldoRegisterImportService.class, "/(\\.|\\/)txt$/"),
	ED108	(ED108ImportService.class, "/(\\.|\\/)(txt|y\\d+)$/");
	//@formatter:on

	@Getter
	private Class clazz;
	@Getter
	private String allowTypeRegexp;

	public String getName() {
		SaldoImportMessagesBundle messages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

		switch (this) {
			case ED108:
				return messages.registerFormatEd108();
			case SALDO:
				return messages.registerFormatSaldo();
			default:
				throw new SystemException("Unsupported RegisterFormat");
		}
	}

}