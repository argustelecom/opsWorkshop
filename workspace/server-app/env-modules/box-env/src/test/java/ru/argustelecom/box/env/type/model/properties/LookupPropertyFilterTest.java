package ru.argustelecom.box.env.type.model.properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.MessageFormat;

import org.junit.Test;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleValuePropertyFilterTest;
import ru.argustelecom.box.env.type.model.lookup.LookupEntry;

public class LookupPropertyFilterTest
		extends AbstractSingleValuePropertyFilterTest<LookupEntry, LookupProperty, LookupPropertyFilter> {

	@Override
	protected void init() {
		filter = new LookupPropertyFilter(lookupProperty);

		value1 = entry1;
		value2 = entry2;

		matchedValue1 = "\"LookupEntry-100\"";
		matchedValue2 = "\"LookupEntry-200\"";

		valueQName = "LookupProperty-70";
		propertyQName = valueQName;
	}

	@Test
	public void shouldCreateIn2Predicate() {
		TypePropertyPredicate predicate = print("In2", filter.in(value1, value2));

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(),
				equalTo(MessageFormat.format("{0} IN ({1}, {2})", valueQName, matchedValue1, matchedValue2)));
	}
}
