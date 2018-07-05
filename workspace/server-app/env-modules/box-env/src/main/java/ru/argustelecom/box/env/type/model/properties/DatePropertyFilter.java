package ru.argustelecom.box.env.type.model.properties;

import java.util.Arrays;
import java.util.Date;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleNumericValuePropertyFilter;

public class DatePropertyFilter extends AbstractSingleNumericValuePropertyFilter<Date, DateProperty> {

	protected DatePropertyFilter(DateProperty property) {
		super(property);
	}

	@Override
	protected <X extends Date> String convertValue(X value) {
		return String.valueOf(value.getTime());
	}

	public TypePropertyPredicate in(Date... values) {
		return values != null ? in(Arrays.asList(values)) : null;
	}
}
