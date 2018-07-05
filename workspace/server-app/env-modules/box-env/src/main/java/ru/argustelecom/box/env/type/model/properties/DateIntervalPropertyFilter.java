package ru.argustelecom.box.env.type.model.properties;

import java.util.Date;

import ru.argustelecom.box.env.datetime.model.DateIntervalValue;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractIntervalNumericValuePropertyFilter;
import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;

public class DateIntervalPropertyFilter
		extends AbstractIntervalNumericValuePropertyFilter<DateIntervalValue, DateIntervalProperty> {

	private String loValueQualifiedName;
	private String hiValueQualifiedName;

	protected DateIntervalPropertyFilter(DateIntervalProperty property) {
		super(property);
		String propertyQName = property.getQualifiedName();
		loValueQualifiedName = propertyQName + "." + DateIntervalProperty.START_DATE_TOKEN;
		hiValueQualifiedName = propertyQName + "." + DateIntervalProperty.END_DATE_TOKEN;
	}

	@Override
	protected String getLoValueQualifiedName() {
		return loValueQualifiedName;
	}

	@Override
	protected String getHiValueQualifiedName() {
		return hiValueQualifiedName;
	}

	@Override
	protected <X extends DateIntervalValue> String convertLoValue(X value) {
		return String.valueOf(value.getStartDate().getTime());
	}

	@Override
	protected <X extends DateIntervalValue> String convertHiValue(X value) {
		return String.valueOf(value.getEndDate().getTime());
	}

	public TypePropertyPredicate contains(Date value) {
		if (value == null) {
			return null;
		}
		String convertedValue = String.valueOf(value.getTime());

		//@formatter:off
		return PredicateFactory.and(
			PredicateFactory.lessOrEqual(getLoValueQualifiedName(), convertedValue),
			PredicateFactory.greaterOrEqual(getHiValueQualifiedName(), convertedValue)
		);
		//@formatter:on
	}
}
