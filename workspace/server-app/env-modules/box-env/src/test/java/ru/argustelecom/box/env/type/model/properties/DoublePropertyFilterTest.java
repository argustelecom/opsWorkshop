package ru.argustelecom.box.env.type.model.properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.text.MessageFormat;

import org.junit.Test;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleNumericValuePropertyFilterTest;

public class DoublePropertyFilterTest
		extends AbstractSingleNumericValuePropertyFilterTest<Double, DoubleProperty, DoublePropertyFilter> {

	@Override
	protected void init() {
		filter = new DoublePropertyFilter(doubleProperty);

		value1 = 10.0D;
		value2 = 55.5D;

		matchedValue1 = "10.0";
		matchedValue2 = "55.5";

		valueQName = "DoubleProperty-30";
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
