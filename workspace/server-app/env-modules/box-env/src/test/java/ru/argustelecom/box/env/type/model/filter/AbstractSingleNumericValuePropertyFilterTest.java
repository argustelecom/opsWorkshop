package ru.argustelecom.box.env.type.model.filter;

import static java.text.MessageFormat.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import org.junit.Test;

import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;

public abstract class AbstractSingleNumericValuePropertyFilterTest<V, P extends TypeProperty<V>, F extends AbstractSingleNumericValuePropertyFilter<V, P>>
		extends AbstractSingleValuePropertyFilterTest<V, P, F> {

	@Test
	public void shouldCreateLessPredicate() {
		TypePropertyPredicate predicate = print("Less", filter.less(value1));

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("{0} < {1}", valueQName, matchedValue1)));
	}

	@Test
	public void shouldCreateLessOrEqualPredicate() {
		TypePropertyPredicate predicate = print("LessOrEqual", filter.lessOrEqual(value1));

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("{0} <= {1}", valueQName, matchedValue1)));
	}

	@Test
	public void shouldCreateGreaterPredicate() {
		TypePropertyPredicate predicate = print("Greater", filter.greater(value1));

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("{0} > {1}", valueQName, matchedValue1)));
	}

	@Test
	public void shouldCreateGreaterOrEqualPredicate() {
		TypePropertyPredicate predicate = print("GreaterOrEqual", filter.greaterOrEqual(value1));

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("{0} >= {1}", valueQName, matchedValue1)));
	}

	@Test
	public void shouldCreateBetweenPredicate() {
		TypePropertyPredicate predicate = print("Between", filter.between(value1, value2));
		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(),
				equalTo(format("({0} >= {1} AND {0} <= {2})", valueQName, matchedValue1, matchedValue2)));
	}

	@Override
	public void shouldReturnNullWhenNullInput() {
		super.shouldReturnNullWhenNullInput();
		assertThat(filter.less(null), is(nullValue()));
		assertThat(filter.lessOrEqual(null), is(nullValue()));
		assertThat(filter.greater(null), is(nullValue()));
		assertThat(filter.greaterOrEqual(null), is(nullValue()));
	}
}
