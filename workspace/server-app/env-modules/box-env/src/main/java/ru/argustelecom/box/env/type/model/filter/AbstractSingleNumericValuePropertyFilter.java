package ru.argustelecom.box.env.type.model.filter;

import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;

public abstract class AbstractSingleNumericValuePropertyFilter<V, P extends TypeProperty<V>>
		extends AbstractSingleValuePropertyFilter<V, P> {

	protected AbstractSingleNumericValuePropertyFilter(P property) {
		super(property);
	}

	public <X extends V> TypePropertyPredicate greater(X value) {
		return isValid(value) ? PredicateFactory.greater(getValueQualifiedName(), convertValue(value)) : null;
	}

	public <X extends V> TypePropertyPredicate greaterOrEqual(X value) {
		return isValid(value) ? PredicateFactory.greaterOrEqual(getValueQualifiedName(), convertValue(value)) : null;
	}

	public <X extends V> TypePropertyPredicate less(X value) {
		return isValid(value) ? PredicateFactory.less(getValueQualifiedName(), convertValue(value)) : null;
	}

	public <X extends V> TypePropertyPredicate lessOrEqual(X value) {
		return isValid(value) ? PredicateFactory.lessOrEqual(getValueQualifiedName(), convertValue(value)) : null;
	}

	public <X extends V> TypePropertyPredicate between(X valueLo, X valueHi) {
		if (!isValid(valueLo) || !isValid(valueHi)) {
			return null;
		}
		String valueQName = getValueQualifiedName();

		//@formatter:off
		return PredicateFactory.and(
			PredicateFactory.greaterOrEqual(valueQName, convertValue(valueLo)),
			PredicateFactory.lessOrEqual(valueQName, convertValue(valueHi))
		);
		//@formatter:on
	}
}
