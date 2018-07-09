package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import javax.annotation.concurrent.NotThreadSafe;

import ru.argustelecom.box.env.type.model.predicate.PredicateFactory;
import ru.argustelecom.box.env.type.model.properties.DateIntervalProperty;
import ru.argustelecom.box.env.type.model.properties.DateIntervalPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.DateProperty;
import ru.argustelecom.box.env.type.model.properties.DatePropertyFilter;
import ru.argustelecom.box.env.type.model.properties.DoubleProperty;
import ru.argustelecom.box.env.type.model.properties.DoublePropertyFilter;
import ru.argustelecom.box.env.type.model.properties.LogicalProperty;
import ru.argustelecom.box.env.type.model.properties.LogicalPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.LongProperty;
import ru.argustelecom.box.env.type.model.properties.LongPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.LookupProperty;
import ru.argustelecom.box.env.type.model.properties.LookupPropertyFilter;
import ru.argustelecom.box.env.type.model.properties.TextProperty;
import ru.argustelecom.box.env.type.model.properties.TextPropertyFilter;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

@NotThreadSafe
public class TypePropertyQuery {

	private Map<TypeProperty<?>, TypePropertyFilter<?, ?>> cache = new HashMap<>();
	private TypePropertyPredicate restrictions;

	public DatePropertyFilter of(DateProperty property) {
		return findOrCreateFilter(property, DatePropertyFilter.class);
	}

	public DateIntervalPropertyFilter of(DateIntervalProperty property) {
		return findOrCreateFilter(property, DateIntervalPropertyFilter.class);
	}

	public DoublePropertyFilter of(DoubleProperty property) {
		return findOrCreateFilter(property, DoublePropertyFilter.class);
	}

	public LongPropertyFilter of(LongProperty property) {
		return findOrCreateFilter(property, LongPropertyFilter.class);
	}

	public LogicalPropertyFilter of(LogicalProperty property) {
		return findOrCreateFilter(property, LogicalPropertyFilter.class);
	}

	public LookupPropertyFilter of(LookupProperty property) {
		return findOrCreateFilter(property, LookupPropertyFilter.class);
	}

	public TextPropertyFilter of(TextProperty property) {
		return findOrCreateFilter(property, TextPropertyFilter.class);
	}

	public TypePropertyQuery and(TypePropertyPredicate... expressions) {
		return and(Arrays.asList(expressions));
	}

	public TypePropertyQuery and(Collection<TypePropertyPredicate> expressions) {
		LinkedList<TypePropertyPredicate> predicates = new LinkedList<>(expressions);
		if (this.restrictions != null) {
			predicates.addFirst(this.restrictions);
		}
		this.restrictions = PredicateFactory.and(predicates);
		return this;
	}

	public static TypePropertyPredicate and_(TypePropertyPredicate... expressions) {
		return PredicateFactory.and(expressions);
	}

	public static TypePropertyPredicate and_(Collection<TypePropertyPredicate> expressions) {
		return PredicateFactory.and(expressions);
	}

	public TypePropertyQuery or(TypePropertyPredicate... expressions) {
		return or(Arrays.asList(expressions));
	}

	public TypePropertyQuery or(Collection<TypePropertyPredicate> expressions) {
		LinkedList<TypePropertyPredicate> predicates = new LinkedList<>(expressions);
		if (this.restrictions != null) {
			predicates.addFirst(this.restrictions);
		}
		this.restrictions = PredicateFactory.or(predicates);
		return this;
	}

	public static TypePropertyPredicate or_(TypePropertyPredicate... expressions) {
		return PredicateFactory.or(expressions);
	}

	public static TypePropertyPredicate or_(Collection<TypePropertyPredicate> expressions) {
		return PredicateFactory.or(expressions);
	}

	public boolean isEmpty() {
		return restrictions == null;
	}

	public void clear() {
		restrictions = null;
	}

	@Override
	public String toString() {
		return restrictions != null ? "TypePropertyQuery [" + restrictions.render() + "]" : "TypePropertyQuery [empty]";
	}

	protected String render() {
		return restrictions != null ? restrictions.render() : null;
	}

	@SuppressWarnings("unchecked")
	protected <V> TypePropertyFilter<V, ?> findOrCreateFilter(TypeProperty<V> property) {
		checkRequiredArgument(property, "property");

		TypePropertyFilter<?, ?> filterInstance = cache.get(property);
		if (filterInstance == null) {
			filterInstance = createFilter(property);
			checkState(filterInstance != null, "Unable to create filter for property %s", property.getClass());
			cache.put(property, filterInstance);
		}

		return (TypePropertyFilter<V, ?>) filterInstance;
	}

	protected <V, P extends TypeProperty<V>, F extends TypePropertyFilter<V, P>> F findOrCreateFilter(P property,
			Class<F> filterClass) {
		checkRequiredArgument(filterClass, "filterClass");

		TypePropertyFilter<V, ?> filterInstance = findOrCreateFilter(property);
		checkState(filterClass.isInstance(filterInstance));

		return filterClass.cast(filterInstance);
	}

	private TypePropertyFilter<?, ?> createFilter(TypeProperty<?> property) {
		TypePropertyRef reference = TypePropertyRef.forClass(property.getClass());
		checkState(reference != null);

		if (reference.isFilterSupported()) {
			Class<? extends TypePropertyFilter<?, ?>> filterClass = reference.getFilterClass();
			return ReflectionUtils.newInstance(filterClass, property);
		}
		return null;
	}
}
