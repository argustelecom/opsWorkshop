package ru.argustelecom.box.env.type.model.properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.MessageFormat;

import org.junit.Test;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleValuePropertyFilterTest;

public class TextPropertyFilterTest
		extends AbstractSingleValuePropertyFilterTest<String, TextProperty, TextPropertyFilter> {

	@Override
	protected void init() {
		filter = new TextPropertyFilter(textProperty);

		value1 = "Значение1";
		value2 = "Значение2";

		matchedValue1 = "\"Значение1\"";
		matchedValue2 = "\"Значение2\"";

		valueQName = "TextProperty-60";
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
