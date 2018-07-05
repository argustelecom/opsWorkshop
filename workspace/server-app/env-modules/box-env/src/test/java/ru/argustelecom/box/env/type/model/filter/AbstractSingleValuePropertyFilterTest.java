package ru.argustelecom.box.env.type.model.filter;

import static java.text.MessageFormat.format;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;

import org.jboss.logging.Logger;
import org.junit.Test;

import ru.argustelecom.box.env.type.model.AbstractEavTest;
import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;

public abstract class AbstractSingleValuePropertyFilterTest<V, P extends TypeProperty<V>, F extends AbstractSingleValuePropertyFilter<V, P>>
		extends AbstractEavTest {

	private static final Logger log = Logger.getLogger("TypePropertyFilterTest");

	protected V value1;
	protected V value2;

	protected String matchedValue1;
	protected String matchedValue2;

	protected F filter;
	protected String valueQName;
	protected String propertyQName;

	@Override
	public void setup() {
		super.setup();
		init();
	}

	protected abstract void init();

	protected TypePropertyPredicate print(String type, TypePropertyPredicate predicate) {
		log.infov("Predicate {1}:  {0}", predicate != null ? predicate.render() : "-", type);
		return predicate;
	}

	@Test
	public void shouldCreateIsNullPredicate() {
		TypePropertyPredicate predicate = print("IsNull", filter.isNull());

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("{0} = null", propertyQName)));
	}

	@Test
	public void shouldCreateIsNotNullPredicate() {
		TypePropertyPredicate predicate = print("IsNotNull", filter.isNotNull());

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("({0} = * AND NOT({0} = null))", propertyQName)));
	}

	@Test
	public void shouldCreateEqualPredicate() {
		TypePropertyPredicate predicate = print("Equal", filter.equal(value1));

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("{0} = {1}", valueQName, matchedValue1)));
	}

	@Test
	public void shouldCreateNotEqualPredicate() {
		TypePropertyPredicate predicate = print("NotEqual", filter.notEqual(value1));

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("({0} = * AND NOT({0} = {1}))", valueQName, matchedValue1)));
	}

	@Test
	public void shouldCreateIn1Predicate() {
		TypePropertyPredicate predicate = print("In1", filter.in(Arrays.asList(value2, value1)));

		assertThat(predicate, is(notNullValue()));
		assertThat(predicate.render(), equalTo(format("{0} IN ({1}, {2})", valueQName, matchedValue2, matchedValue1)));
	}

	@Test
	public void shouldReturnNullWhenNullInput() {
		assertThat(filter.equal(null), is(nullValue()));
		assertThat(filter.notEqual(null), is(nullValue()));
		assertThat(filter.in(null), is(nullValue()));
		assertThat(filter.in(Arrays.asList(null, null, null)), is(nullValue()));
		assertThat(filter.in(Collections.emptyList()), is(nullValue()));
	}
}
