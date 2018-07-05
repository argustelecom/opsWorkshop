package ru.argustelecom.box.env.type.model.properties;

import static org.apache.commons.lang3.StringUtils.wrap;

import java.util.Arrays;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleValuePropertyFilter;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;

public class LookupPropertyFilter extends AbstractSingleValuePropertyFilter<LookupEntry, LookupProperty> {

	protected LookupPropertyFilter(LookupProperty property) {
		super(property);
	}

	@Override
	protected <X extends LookupEntry> boolean isValid(X value) {
		return super.isValid(value) && getProperty().isSameCategory(getProperty().getCategory(), value);
	}

	@Override
	protected <X extends LookupEntry> String convertValue(X value) {
		return wrap(getEntityConverter().convertToString(value), '"');
	}

	public TypePropertyPredicate in(LookupEntry... values) {
		return values != null ? in(Arrays.asList(values)) : null;
	}
}
