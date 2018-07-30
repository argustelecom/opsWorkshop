package ru.argustelecom.box.env.type.model.filter;

import java.util.Collection;
import java.util.stream.Collectors;

import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyFilter;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;

public abstract class AbstractSingleValuePropertyFilter<V, P extends TypeProperty<V>> extends TypePropertyFilter<V, P> {

	protected AbstractSingleValuePropertyFilter(P property) {
		super(property);
	}

	protected String getValueQualifiedName() {
		return getProperty().getQualifiedName();
	}

	protected <X extends V> String convertValue(X value) {
		return value.toString();
	}

	@Override
	public <X extends V> TypePropertyPredicate equal(X value) {
		return isValid(value) ? PredicateFactory.equal(getValueQualifiedName(), convertValue(value)) : null;
	}

	@Override
	public <X extends V> TypePropertyPredicate notEqual(X value) {
		if (!isValid(value)) {
			return null;
		}

		//@formatter:off
		String valueQName = getValueQualifiedName();
		return PredicateFactory.and(
			PredicateFactory.equal(valueQName, "*"),
			PredicateFactory.not(PredicateFactory.equal(valueQName, convertValue(value)))
		);
		//@formatter:on
	}

	public <X extends V> TypePropertyPredicate in(Collection<X> values) {
		if (values == null) {
			return null;
		}

		//@formatter:off
		Collection<String> convertedValues = values.stream()
			.filter(this::isValid)
			.map(this::convertValue)
			.collect(Collectors.toList());
		//@formatter:on

		if (convertedValues.isEmpty()) {
			return null;
		}
		return PredicateFactory.in(getValueQualifiedName(), convertedValues);
	}
}
