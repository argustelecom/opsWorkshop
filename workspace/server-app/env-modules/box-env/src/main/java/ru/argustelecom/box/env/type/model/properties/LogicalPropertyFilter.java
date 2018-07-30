package ru.argustelecom.box.env.type.model.properties;

import ru.argustelecom.box.env.type.model.TypePropertyFilter;
import ru.argustelecom.box.env.type.model.TypePropertyPredicate;
import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;

public class LogicalPropertyFilter extends TypePropertyFilter<Boolean, LogicalProperty> {

	protected LogicalPropertyFilter(LogicalProperty property) {
		super(property);
	}

	@Override
	public <X extends Boolean> TypePropertyPredicate equal(X value) {
		return isValid(value) ? PredicateFactory.equal(getProperty().getQualifiedName(), String.valueOf(value)) : null;
	}

	@Override
	public <X extends Boolean> TypePropertyPredicate notEqual(X value) {
		return isValid(value)
				? PredicateFactory.equal(getProperty().getQualifiedName(), String.valueOf(!value.booleanValue()))
				: null;
	}

	public TypePropertyPredicate isTrue() {
		return equal(true);
	}

	public TypePropertyPredicate isFalse() {
		return equal(false);
	}
}
