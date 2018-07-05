package ru.argustelecom.box.env.numerationpattern;

import ru.argustelecom.box.env.numerationpattern.parser.NumerationPatternLexeme;

public class NumerationPatternUtils {

	private NumerationPatternUtils() {
	}

	public static String getVariableName(String value) {
		return value.split(":")[0];
	}

	public static String getVariableName(NumerationPatternLexeme lexeme) {
		return getVariableName(lexeme.getValue());
	}

	public static String getVariableFormat(String value) {
		String[] varNameFormat = value.split(":");
		if (varNameFormat.length == 2) {
			return varNameFormat[1];
		}
		return null;
	}

	public static String getVariableFormat(NumerationPatternLexeme lexeme) {
		return getVariableFormat(lexeme.getValue());
	}
}
