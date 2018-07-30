package ru.argustelecom.box.env.type.model.properties;

import java.util.Arrays;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleNumericValuePropertyFilter;

public class LongPropertyFilter extends AbstractSingleNumericValuePropertyFilter<Long, LongProperty> {

	protected LongPropertyFilter(LongProperty property) {
		super(property);
	}

	public TypePropertyPredicate in(Long... values) {
		return values != null ? in(Arrays.asList(values)) : null;
	}
}
