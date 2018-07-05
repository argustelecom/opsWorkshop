package ru.argustelecom.box.env.saldo.imp.model;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class FieldProperty {

	private Field field;
	private Element element;

	private SimpleDateFormat dateFormat;
	private DateTimeFormatter localDateFormatter;
	private DateTimeFormatter localTimeFormatter;

	public FieldProperty(Field field, Element element) {
		this.field = field;
		this.element = element;

		if (!element.dateFormat().isEmpty()) {
			if (field.getType().isAssignableFrom(Date.class))
				dateFormat = new SimpleDateFormat(element.dateFormat());
			if (field.getType().isAssignableFrom(LocalDate.class))
				localDateFormatter = DateTimeFormatter.ofPattern(element.dateFormat());
			if (field.getType().isAssignableFrom(LocalTime.class))
				localTimeFormatter = DateTimeFormatter.ofPattern(element.dateFormat());
		}
	}

	public String getName() {
		return field.getName();
	}

	public Class<?> getType() {
		return field.getType();
	}

	public SimpleDateFormat getDateFormat() {
		return dateFormat;
	}

	public DateTimeFormatter getLocalDateFormatter() {
		return localDateFormatter;
	}

	public DateTimeFormatter getLocalTimeFormatter() {
		return localTimeFormatter;
	}

}