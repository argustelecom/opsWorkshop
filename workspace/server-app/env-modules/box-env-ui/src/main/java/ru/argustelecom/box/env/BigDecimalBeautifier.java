package ru.argustelecom.box.env;

import javax.inject.Named;
import java.math.BigDecimal;

@Named
public class BigDecimalBeautifier {

	public String toBeauty(BigDecimal value) {
		return UtilsBeautifier.toBeauty(value);
	}
}
