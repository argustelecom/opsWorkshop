package ru.argustelecom.box.env.type.model.properties;

import java.util.Arrays;

import ru.argustelecom.box.env.measure.model.MeasuredValue;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleNumericValuePropertyFilter;

public class MeasuredPropertyFilter extends AbstractSingleNumericValuePropertyFilter<MeasuredValue, MeasuredProperty> {

	private String valueQualifiedName;

	protected MeasuredPropertyFilter(MeasuredProperty property) {
		super(property);
		valueQualifiedName = getProperty().getQualifiedName() + "." + MeasuredProperty.STORED_VALUE_TOKEN;
	}

	@Override
	protected <X extends MeasuredValue> boolean isValid(X value) {
		return super.isValid(value) && getProperty().getMeasureUnit().isConvertibleFrom(value.getMeasureUnit());
	}

	@Override
	protected String getValueQualifiedName() {
		return valueQualifiedName;
	}

	@Override
	protected <X extends MeasuredValue> String convertValue(X value) {
		return String.valueOf(value.getStoredValue());
	}

	public TypePropertyPredicate in(MeasuredValue... values) {
		return values != null ? in(Arrays.asList(values)) : null;
	}
}
