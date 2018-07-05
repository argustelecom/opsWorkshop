package ru.argustelecom.box.env.saldo.imp;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

import ru.argustelecom.box.env.saldo.imp.model.FieldProperty;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.exception.SystemException;

public class RegisterImportUtils {

	public static Object get(FieldProperty field, String value) {
		if (field.getType().isAssignableFrom(String.class))
			return value;
		if (field.getType().isAssignableFrom(Long.class))
			return getLong(value);
		else if (field.getType().isAssignableFrom(BigDecimal.class))
			return getBigDecimal(value);
		else if (field.getType().isAssignableFrom(Date.class))
			return getDate(value, field.getDateFormat());
		else if (field.getType().isAssignableFrom(LocalTime.class))
			return getLocalTime(value, field.getLocalTimeFormatter());
		else if (field.getType().isAssignableFrom(LocalDate.class))
			return getLocalDate(value, field.getLocalDateFormatter());
		else if (field.getType().isAssignableFrom(Money.class))
			return getMoney(value);
		else
			throw new SystemException("Unsupported type: " + field.getType());
	}

	public static Long getLong(String value) {
		try {
			return Long.parseLong(value);
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public static BigDecimal getBigDecimal(String value) {
		try {
			return new BigDecimal(value.replace(",", "."));
		} catch (NumberFormatException nfe) {
			return null;
		}
	}

	public static Date getDate(String value, SimpleDateFormat format) {
		try {
			return format.parse(value);
		} catch (ParseException pe) {
			return null;
		}
	}

	public static LocalTime getLocalTime(String value, DateTimeFormatter formatter) {
		try {
			return LocalTime.parse(value, formatter);
		} catch (DateTimeParseException pe) {
			return null;
		}
	}

	public static LocalDate getLocalDate(String value, DateTimeFormatter formatter) {
		try {
			return LocalDate.parse(value, formatter);
		} catch (DateTimeParseException pe) {
			return null;
		}
	}

	public static Money getMoney(String value) {
		return new Money(getBigDecimal(value));
	}

}