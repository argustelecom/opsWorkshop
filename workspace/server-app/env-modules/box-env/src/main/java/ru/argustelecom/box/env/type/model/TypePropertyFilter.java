package ru.argustelecom.box.env.type.model;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import lombok.Getter;
import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

public abstract class TypePropertyFilter<V, P extends TypeProperty<V>> {

	@Getter
	private P property;

	protected TypePropertyFilter(P property) {
		this.property = checkRequiredArgument(property, "property");
	}

	protected EntityConverter getEntityConverter() {
		return property.getEntityConverter();
	}

	protected <X extends V> boolean isValid(X value) {
		return value != null;
	}

	public abstract <X extends V> TypePropertyPredicate equal(X value);

	public abstract <X extends V> TypePropertyPredicate notEqual(X value);

	public TypePropertyPredicate isNull() {
		return PredicateFactory.equal(property.getQualifiedName(), "null");
	}

	public TypePropertyPredicate isNotNull() {
		//@formatter:off
		String valueQName = property.getQualifiedName();
		return PredicateFactory.and(
			PredicateFactory.equal(valueQName, "*"),
			PredicateFactory.not(PredicateFactory.equal(valueQName, "null"))
		);
		//@formatter:on
	}
}
