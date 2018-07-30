package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.text.MessageFormat.format;
import static java.util.stream.Collectors.toSet;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.ReflectionUtils;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "type_property_holder")
public class TypePropertyHolder extends MetadataUnit<Long> implements Identifiable {

	private static final long serialVersionUID = -5564133221045691004L;
	protected static final int DEPRECATED_PROPERTY_DEFAULT_ORDINAL_NUMBER = 0;

	@OneToMany(targetEntity = TypeProperty.class, fetch = FetchType.LAZY, mappedBy = "holder", cascade = CascadeType.ALL)
	private Set<TypeProperty<?>> properties = new HashSet<>();

	@OneToMany(fetch = FetchType.LAZY, mappedBy = "holder", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<TypePropertyGroup> propertyGroups = Lists.newArrayList();

	@Transient
	private Set<TypeProperty<?>> activeProperties;

	@Transient
	private Set<TypeProperty<?>> deprecatedProperties;

	/**
	 * Конструктор предназначен для инстанцирования JPA провайдером. Не делай его публичным ни здесь, ни в потомках.
	 */
	protected TypePropertyHolder() {
		super();
	}

	/**
	 * Конструктор предназначен для инстанцирования спецификацией при ее создании. Идентификатор холдера равен
	 * идентификтору спецификации. Для избежания накладок идентификаторов холдеров необходимо использовать один
	 * генератор для всех потомков спецификации!
	 * 
	 * @param id
	 */
	protected TypePropertyHolder(Long id) {
		super(id);
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	@Override
	protected Long checkId(Long id) {
		return checkNotNull(id);
	}

	public Set<TypeProperty<?>> getProperties() {
		if (activeProperties == null) {
			activeProperties = filterPropertiesByStatus(MetadataUnitStatus.ACTIVE);
		}
		return activeProperties;
	}

	public <T extends TypeProperty<?>> Set<T> getProperties(Class<T> propertyClass) {
		//@formatter:off
		return Collections.unmodifiableSet(
			properties.stream()
				.filter(p -> propertyClass.isAssignableFrom(p.getClass()))
				.map(propertyClass::cast)
				.collect(toSet())
		);
		//@formatter:on
	}

	public Set<TypeProperty<?>> getDeprecatedProperties() {
		if (deprecatedProperties == null) {
			deprecatedProperties = filterPropertiesByStatus(MetadataUnitStatus.DEPRECATED);
		}
		return deprecatedProperties;
	}

	public Set<TypeProperty<?>> getAllProperties() {
		return Collections.unmodifiableSet(properties);
	}

	public TypeProperty<?> getProperty(String keyword) {
		return keyword == null ? null
				: properties.stream().filter(p -> Objects.equals(keyword, p.getKeyword())).findFirst().orElse(null);
	}

	public <T extends TypeProperty<?>> T getProperty(Class<T> propertyClass, String keyword) {
		TypeProperty<?> result = getProperty(keyword);
		if (result != null && propertyClass.isAssignableFrom(result.getClass())) {
			return propertyClass.cast(result);
		}
		return null;
	}

	public boolean hasProperties() {
		return !properties.isEmpty();
	}

	public boolean hasProperty(String keyword) {
		return getProperty(keyword) != null;
	}

	public <T extends TypeProperty<?>> boolean hasProperty(T property) {
		return properties.contains(property);
	}

	public <T extends TypeProperty<?>> T createProperty(Class<T> propertyClass, String keyword, Long id) {
		checkRequiredArgument(propertyClass, "propertyClass");
		checkRequiredArgument(id, "propertyId");

		PropertyCondition condition = getPropertyCondition(propertyClass, keyword);
		if (condition == PropertyCondition.DEPRECATED) {
			return resurrectProperty(propertyClass, keyword);
		}

		checkCreationCondition(condition, keyword);

		T property = ReflectionUtils.newInstance(propertyClass, this, id);
		property.setKeyword(keyword);
		properties.add(property);
		resetPropertyCache();
		return property;
	}

	public void removeProperty(String keyword) {
		removeProperty(getProperty(keyword));
	}

	public <T extends TypeProperty<?>> void removeProperty(T property) {
		if (property != null && property.isDeletable() && hasProperty(property)) {
			property.setStatus(MetadataUnitStatus.DEPRECATED);
			resetPropertyCache();

			TypePropertyGroup group = property.getGroup();
			if (group != null) {
				group.removeProperty(property);
			}
			property.setOrdinalNumber(DEPRECATED_PROPERTY_DEFAULT_ORDINAL_NUMBER);
		}
	}

	protected <T extends TypeProperty<?>> T resurrectProperty(Class<T> propertyClass, String keyword) {
		T property = getProperty(propertyClass, keyword);
		property.setStatus(MetadataUnitStatus.ACTIVE);
		resetPropertyCache();
		return property;
	}

	protected <T extends TypeProperty<?>> PropertyCondition getPropertyCondition(Class<T> propertyClass, String keyword) {
		TypeProperty<?> property = getProperty(keyword);
		if (property == null) {
			return PropertyCondition.NOT_EXISTS;
		}
		if (Objects.equals(propertyClass, property.getClass())) {
			if (property.getStatus() == MetadataUnitStatus.DEPRECATED) {
				return PropertyCondition.DEPRECATED;
			}
			return PropertyCondition.EXISTS;
		}
		return PropertyCondition.INCONSISTENT_CLASS;
	}

	public List<TypePropertyGroup> getPropertyGroups() {
		return Collections.unmodifiableList(propertyGroups);
	}

	public TypePropertyGroup getPropertyGroup(String groupName) {
		if (Strings.isNullOrEmpty(groupName)) {
			return null;
		}

		return propertyGroups.stream().filter(g -> groupName.equalsIgnoreCase(g.getObjectName())).findFirst()
				.orElse(null);
	}

	public TypePropertyGroup createPropertyGroup(String groupName, Long groupId) {
		checkRequiredArgument(groupId, "groupId");
		checkRequiredArgument(groupName, "groupName");

		TypePropertyGroup result = getPropertyGroup(groupName);
		if (result == null) {
			propertyGroups.add(result = new TypePropertyGroup(groupId, this));
			result.setKeyword(MetadataUnit.generateKeyword(TypePropertyGroup.class, groupId));
			result.setOrdinalNumber(propertyGroups.size());
			result.setName(groupName);
		}

		return result;
	}

	public TypePropertyGroup createPropertyGroup(String groupName, Long groupId, Integer ordinalNumber) {
		checkRequiredArgument(ordinalNumber, "ordinalNumber");

		TypePropertyGroup result = createPropertyGroup(groupName, groupId);
		result.changeOrdinalNumber(ordinalNumber);
		
		return result;
	}

	public boolean removePropertyGroup(TypePropertyGroup groupToRemove) {
		checkRequiredArgument(groupToRemove, "groupToRemove");

		boolean removed = groupToRemove.getProperties().isEmpty() && propertyGroups.remove(groupToRemove);
		if (removed) {
			Ordinal.normalize(groupToRemove.group());
		}
		return removed;
	}

	public List<TypeProperty<?>> getPropertiesWithoutGroup() {
		return Collections.unmodifiableList(
				getProperties().stream().filter(property -> !property.hasGroup()).collect(Collectors.toList()));
	}

	protected enum PropertyCondition {
		EXISTS, INCONSISTENT_CLASS, DEPRECATED, NOT_EXISTS
	}

	protected void resetPropertyCache() {
		activeProperties = null;
		deprecatedProperties = null;
	}

	private void checkCreationCondition(PropertyCondition condition, String keyword) {
		if (condition == PropertyCondition.EXISTS)
			throw new BusinessException(format("Свойство с идентификатором {0} уже существует", keyword));

		if (condition == PropertyCondition.INCONSISTENT_CLASS)
			throw new BusinessException(format(
					"Свойство с идентификатором {0} уже существует и тип значения отличается от указанного", keyword));
	}

	private Set<TypeProperty<?>> filterPropertiesByStatus(MetadataUnitStatus status) {
		final Set<TypeProperty<?>> result = new HashSet<>();
		properties.forEach(property -> {
			if (property.getStatus() == status) {
				result.add(property);
			}
		});
		return Collections.unmodifiableSet(result);
	}

}
