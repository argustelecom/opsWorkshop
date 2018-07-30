package ru.argustelecom.box.env.type.model.filter;

import ru.argustelecom.box.env.type.model.TypeProperty;
import ru.argustelecom.box.env.type.model.TypePropertyFilter;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;

public abstract class AbstractIntervalValuePropertyFilter<V, P extends TypeProperty<V>>
		extends TypePropertyFilter<V, P> {

	protected AbstractIntervalValuePropertyFilter(P property) {
		super(property);
	}

	protected abstract String getLoValueQualifiedName();

	protected abstract String getHiValueQualifiedName();

	protected abstract <X extends V> String convertLoValue(X value);

	protected abstract <X extends V> String convertHiValue(X value);

	@Override
	public <X extends V> TypePropertyPredicate equal(X value) {
		if (!isValid(value)) {
			return null;
		}

		//@formatter:off
		return PredicateFactory.and(
			PredicateFactory.equal(getLoValueQualifiedName(), convertLoValue(value)),
			PredicateFactory.equal(getHiValueQualifiedName(), convertHiValue(value))
		);
		//@formatter:on
	}

	@Override
	public <X extends V> TypePropertyPredicate notEqual(X value) {
		if (!isValid(value)) {
			return null;
		}
		String loValueQName = getLoValueQualifiedName();
		String hiValueQName = getHiValueQualifiedName();

		//@formatter:off
		return PredicateFactory.or(
			PredicateFactory.and(
				PredicateFactory.equal(loValueQName, "*"),
				PredicateFactory.not(PredicateFactory.equal(loValueQName, convertLoValue(value)))
			),
			PredicateFactory.and(
				PredicateFactory.equal(hiValueQName, "*"),
				PredicateFactory.not(PredicateFactory.equal(hiValueQName, convertHiValue(value)))
			)
		);
		//@formatter:on
	}
}
