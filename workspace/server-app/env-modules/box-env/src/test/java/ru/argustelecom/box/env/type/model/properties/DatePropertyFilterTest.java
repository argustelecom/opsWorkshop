package ru.argustelecom.box.env.type.model.properties;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToDate;

import java.text.MessageFormat;
import java.util.Date;

import org.junit.Test;

import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.filter.AbstractSingleNumericValuePropertyFilterTest;

public class DatePropertyFilterTest
		extends AbstractSingleNumericValuePropertyFilterTest<Date, DateProperty, DatePropertyFilter> {

	@Override
	protected void init() {
		filter = new DatePropertyFilter(dateProperty);
		value1 = strToDate("2018-04-09 18:00:00.000");
		value2 = strToDate("2018-04-10 18:00:00.000");

		matchedValue1 = String.valueOf(value1.getTime());
		matchedValue2 = String.valueOf(value2.getTime());

		valueQName = "DateProperty-10";
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
