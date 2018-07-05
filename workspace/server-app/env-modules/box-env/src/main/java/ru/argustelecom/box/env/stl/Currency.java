package ru.argustelecom.box.env.stl;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.stl.nls.CurrencyMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Валюта
 * <p>
 *
 */
 @AllArgsConstructor(access = AccessLevel.MODULE)
public enum Currency {

	//@formatter:off
	RUB("руб.", "643", "fa-ruble"),
	EUR("€", "978", "fa-euro"),
	USD("$", "840", "fa-dollar");
	//@formatter:on

	@Getter
	private String shortName;
	@Getter
	private String code;
	@Getter
	private String icon;

	public String getName() {
		CurrencyMessagesBundle messages = LocaleUtils.getMessages(CurrencyMessagesBundle.class);

		switch (this) {
			case EUR:
				return messages.currencyEuro();
			case RUB:
				return messages.currencyRussianRuble();
			case USD:
				return messages.currencyUsDollar();

			default:
				throw new SystemException("Unsupported Currency");
		}
	}

	public static Currency getDefault() {
		return Currency.valueOf(System.getProperty("box.currency.default"));
	}

}
