package ru.argustelecom.box.env.type.model.properties;

import ru.argustelecom.box.env.measure.model.MeasuredIntervalValue;
import ru.argustelecom.box.env.measure.model.MeasuredValue;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractIntervalNumericValuePropertyFilter;
import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;

public class MeasuredIntervalPropertyFilter
		extends AbstractIntervalNumericValuePropertyFilter<MeasuredIntervalValue, MeasuredIntervalProperty> {

	private String loValueQualifiedName;
	private String hiValueQualifiedName;

	protected MeasuredIntervalPropertyFilter(MeasuredIntervalProperty property) {
		super(property);
		String propertyQName = property.getQualifiedName();
		loValueQualifiedName = propertyQName + "." + MeasuredIntervalProperty.START_STORED_VALUE_TOKEN;
		hiValueQualifiedName = propertyQName + "." + MeasuredIntervalProperty.END_STORED_VALUE_TOKEN;
	}

	@Override
	protected <X extends MeasuredIntervalValue> boolean isValid(X value) {
		return super.isValid(value) && getProperty().getMeasureUnit().isConvertibleFrom(value.getMeasureUnit());
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
	protected <X extends MeasuredIntervalValue> String convertLoValue(X value) {
		return String.valueOf(value.getStartStoredValue());
	}

	@Override
	protected <X extends MeasuredIntervalValue> String convertHiValue(X value) {
		return String.valueOf(value.getEndStoredValue());
	}

	public TypePropertyPredicate contains(MeasuredValue value) {
		if (value == null) {
			return null;
		}
		String convertedValue = String.valueOf(value.getStoredValue());

		//@formatter:off
		return PredicateFactory.and(
			PredicateFactory.lessOrEqual(getLoValueQualifiedName(), convertedValue),
			PredicateFactory.greaterOrEqual(getHiValueQualifiedName(), convertedValue)
		);
		//@formatter:on
	}
}
