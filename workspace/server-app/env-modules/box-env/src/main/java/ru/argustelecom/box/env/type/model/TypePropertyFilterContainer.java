package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.type.model.TypeCreationalContext.creationalContext;
import static ru.argustelecom.box.env.type.model.TypePropertyQuery.or_;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

import javax.persistence.criteria.Predicate;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.type.model.TypeInstance.EntityQueryPropertiesFilter;

public class TypePropertyFilterContainer {

	private Map<FilterItemKey, FilterItem<?>> items = new HashMap<>();

	public <V, P extends TypeProperty<V>, T extends Type> void addProperty(T owner, P property) {
		FilterItemKey key = new FilterItemKey(property);
		FilterItem<V> item = findItem(key);
		if (item == null) {
			item = new FilterItem<>(key);
			items.put(key, item);
		}
		item.addProperty(owner, property);
	}

	public <V, P extends TypeProperty<V>> boolean removeProperty(P property) {
		FilterItemKey key = new FilterItemKey(property);
		FilterItem<V> item = findItem(key);
		if (item != null) {
			boolean result = item.removeProperty(property);
			if (result && item.getProperties().isEmpty()) {
				items.remove(key);
			}
			return result;
		}
		return false;
	}

	public List<TypePropertyAccessor<?>> getAccessors() {
		return items.values().stream().map(FilterItem::getAccessor).collect(toList());
	}

	public void clearProperties() {
		items.clear();
	}

	public void clearValues() {
		getAccessors().forEach(a -> a.setValue(null));
	}

	public TypePropertyQuery createQuery() {
		TypePropertyQuery query = new TypePropertyQuery();
		items.values().forEach(item -> item.applyTo(query));
		return query;
	}

	public Predicate toJpaPredicate(EntityQueryPropertiesFilter<?> propertiesFilter) {
		TypePropertyQuery propertyQuery = createQuery();
		if (!propertyQuery.isEmpty()) {
			return propertiesFilter.satisfied(propertyQuery);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	private <V> FilterItem<V> findItem(FilterItemKey key) {
		return (FilterItem<V>) items.get(key);
	}

	@Getter
	@EqualsAndHashCode(of = "key")
	private static class FilterItem<V> {

		private final FilterItemKey key;
		private TypePropertyAccessor<V> accessor;
		private Set<TypeProperty<V>> properties = new HashSet<>();
		private AtomicLong instanceCounter = new AtomicLong(1L);

		FilterItem(FilterItemKey key) {
			this.key = checkRequiredArgument(key, "key");
		}

		<P extends TypeProperty<V>, T extends Type> void addProperty(T owner, P property) {
			checkArgument(Objects.equals(property.getValueClass(), key.getValueClass()));
			checkArgument(Objects.equals(property.getKeyword(), key.getPropertyKeyword()));

			if (accessor == null) {
				Long instanceId = instanceCounter.getAndIncrement();
				TypeCreationalContext<T> ctx = creationalContext(owner);
				TypeInstanceProxy<T> proxy = new TypeInstanceProxy<>(owner, instanceId);
				accessor = ctx.createAccessor(proxy, property, true);
			}

			properties.add(property);
		}

		boolean removeProperty(TypeProperty<V> property) {
			return properties.remove(property);
		}

		void applyTo(TypePropertyQuery query) {
			if (properties.isEmpty() || accessor == null) {
				return;
			}

			List<TypePropertyPredicate> predicates = new ArrayList<>();

			V value = accessor.getValue();
			if (value != null) {
				for (TypeProperty<V> property : properties) {
					TypePropertyFilter<V, ?> filter = query.findOrCreateFilter(property);
					TypePropertyPredicate predicate = filter.equal(value);
					if (predicate != null) {
						predicates.add(predicate);
					}
				}
			}

			if (!predicates.isEmpty()) {
				query.and(or_(predicates));
			}
		}
	}

	@Getter
	@EqualsAndHashCode
	private static class FilterItemKey {
		private final Class<?> valueClass;
		private final String propertyKeyword;

		FilterItemKey(TypeProperty<?> property) {
			this.propertyKeyword = property.getKeyword();
			this.valueClass = property.getValueClass();
		}
	}

	private static class TypeInstanceProxy<T extends Type> extends TypeInstance<T> {

		private static final long serialVersionUID = 1669833412942179123L;

		TypeInstanceProxy(T owner, Long id) {
			super(id);
			setType(owner);
		}

	}
}
