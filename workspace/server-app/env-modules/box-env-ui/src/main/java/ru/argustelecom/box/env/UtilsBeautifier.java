package ru.argustelecom.box.env;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;

public class UtilsBeautifier {
	private final static String FORMAT_PATTERN = "0.00";

	public static String toBeauty(BigDecimal value) {
		if (value == null)
			return null;

		DecimalFormat df = new DecimalFormat(FORMAT_PATTERN);
		df.setGroupingUsed(false);
		DecimalFormatSymbols dfs = new DecimalFormatSymbols();
		dfs.setDecimalSeparator('.');
		df.setDecimalFormatSymbols(dfs);
		return df.format(value);
	}
}
