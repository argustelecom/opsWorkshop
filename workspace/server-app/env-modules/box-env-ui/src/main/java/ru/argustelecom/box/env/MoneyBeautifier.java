package ru.argustelecom.box.env;

import javax.inject.Named;
import javax.inject.Singleton;

import ru.argustelecom.box.env.stl.Currency;
import ru.argustelecom.box.env.stl.Money;

@Named
@Singleton
public class MoneyBeautifier {

	private Currency defaultCurrency;

	// Не перегружается, т.к. при передачи null вылетает
	// javax.el.MethodNotFoundException: Unable to find unambiguous method
	public String toBeauty(Money value) {
		if (value == null) {
			return null;
		}
		return UtilsBeautifier.toBeauty(value.getRoundAmount());
	}

	public Currency getDefaultCurrency() {
		if (defaultCurrency == null) {
			defaultCurrency = Currency.valueOf(System.getProperty("box.currency.default"));
		}
		return defaultCurrency;
	}
}
