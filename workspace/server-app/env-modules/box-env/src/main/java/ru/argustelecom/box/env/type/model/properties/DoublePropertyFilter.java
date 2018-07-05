package ru.argustelecom.box.env.type.model.properties;

import java.util.Arrays;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleNumericValuePropertyFilter;

public class DoublePropertyFilter extends AbstractSingleNumericValuePropertyFilter<Double, DoubleProperty> {

	protected DoublePropertyFilter(DoubleProperty property) {
		super(property);
	}

	public TypePropertyPredicate in(Double... values) {
		return values != null ? in(Arrays.asList(values)) : null;
	}
}