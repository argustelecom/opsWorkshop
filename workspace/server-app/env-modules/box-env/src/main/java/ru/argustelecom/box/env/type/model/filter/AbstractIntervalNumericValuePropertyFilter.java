package ru.argustelecom.box.env.type.model.filter;

import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;

public abstract class AbstractIntervalNumericValuePropertyFilter<V, P extends TypeProperty<V>>
		extends AbstractIntervalValuePropertyFilter<V, P> {

	protected AbstractIntervalNumericValuePropertyFilter(P property) {
		super(property);
	}

	public <X extends V> TypePropertyPredicate contains(X value) {
		if (!isValid(value)) {
			return null;
		}

		//@formatter:off
		return PredicateFactory.and(
			PredicateFactory.lessOrEqual(getLoValueQualifiedName(), convertLoValue(value)),
			PredicateFactory.greaterOrEqual(getHiValueQualifiedName(), convertHiValue(value))
		);
		//@formatter:on
	}

	public <X extends V> TypePropertyPredicate intersects(X value) {
		if (!isValid(value)) {
			return null;
		}

		//@formatter:off
		return PredicateFactory.and(
			PredicateFactory.less(getLoValueQualifiedName(), convertHiValue(value)),
			PredicateFactory.greater(getHiValueQualifiedName(), convertLoValue(value))
		);
		//@formatter:on
	}
}
