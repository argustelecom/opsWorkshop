package ru.argustelecom.ops.inf.util;

public class ParseUtils {

	public static Integer intValue(Object attributeValue) throws NumberFormatException {

		if (attributeValue == null) {
			return null;
		}

		if (attributeValue instanceof Number) {
			return (((Number) attributeValue).intValue());
		} else {
			return (Integer.parseInt(attributeValue.toString()));
		}

	}

	public static Double doubleValue(Object attributeValue) throws NumberFormatException {

		if (attributeValue == null) {
			return null;
		}

		if (attributeValue instanceof Number) {
			return (((Number) attributeValue).doubleValue());
		} else {
			return (Double.parseDouble(attributeValue.toString()));
		}

	}
}
