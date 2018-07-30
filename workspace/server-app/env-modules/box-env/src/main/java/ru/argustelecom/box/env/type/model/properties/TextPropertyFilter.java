package ru.argustelecom.box.env.type.model.properties;

import static org.apache.commons.lang3.StringUtils.wrapIfMissing;

import java.util.Arrays;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleValuePropertyFilter;

public class TextPropertyFilter extends AbstractSingleValuePropertyFilter<String, TextProperty> {

	protected TextPropertyFilter(TextProperty property) {
		super(property);
	}

	@Override
	protected <X extends String> String convertValue(X value) {
		return wrapIfMissing(value, '"');
	}

	public TypePropertyPredicate in(String... values) {
		return values != null ? in(Arrays.asList(values)) : null;
	}
}
