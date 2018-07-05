package ru.argustelecom.box.env.type.model.properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.MessageFormat;

import org.junit.Test;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleNumericValuePropertyFilterTest;

public class LongPropertyFilterTest
		extends AbstractSingleNumericValuePropertyFilterTest<Long, LongProperty, LongPropertyFilter> {

	@Override
	protected void init() {
		filter = new LongPropertyFilter(longProperty);

		value1 = 10L;
		value2 = 99L;

		matchedValue1 = "10";
		matchedValue2 = "99";

		valueQName = "LongProperty-40";
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
