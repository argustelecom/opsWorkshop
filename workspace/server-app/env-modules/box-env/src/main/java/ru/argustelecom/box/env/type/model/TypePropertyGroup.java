package ru.argustelecom.box.env.type.model;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import com.google.common.collect.Lists;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.MetadataUnit;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Представляет группу {@linkplain TypeProperty характеристик} типа
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "type_property_group", uniqueConstraints = {
		@UniqueConstraint(name = "uc_type_property_group_name", columnNames = { "holder_id", "name" }) })
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TypePropertyGroup extends MetadataUnit<Long> implements Identifiable, Ordinal {

	private static final long serialVersionUID = -3583264966263209309L;
	private static final Integer INITIAL_ORDINAL_NUMBER = 1;

	/**
	 * Порядковый номер группы
	 */
	@Getter
	@Setter
	@Column(nullable = false)
	private Integer ordinalNumber;

	@Getter(AccessLevel.PROTECTED)
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "holder_id", nullable = false)
	private TypePropertyHolder holder;

	@OneToMany(targetEntity = TypeProperty.class, fetch = FetchType.LAZY, mappedBy = "group")
	private List<TypeProperty<?>> properties = Lists.newArrayList();

	protected TypePropertyGroup(Long id, TypePropertyHolder holder) {
		super(id);
		this.holder = checkRequiredArgument(holder, "holder");
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

	public List<TypeProperty<?>> getProperties() {
		return Collections.unmodifiableList(properties);
	}

	public boolean addProperty(TypeProperty<?> property) {
		checkRequiredArgument(property, "property");
		checkArgument(Objects.equals(property.getHolder(), holder));

		boolean contains = properties.contains(property);
		if (!contains) {
			properties.add(property);
			property.setGroup(this);
			property.setOrdinalNumber(properties.size());
		}
		return !contains;
	}

	public boolean removeProperty(TypeProperty<?> property) {
		checkRequiredArgument(property, "property");
		checkArgument(Objects.equals(property.getHolder(), holder));

		boolean removed = properties.remove(property);
		if (removed) {
			property.setGroup(null);
			property.setOrdinalNumber(holder.getPropertiesWithoutGroup().size());
		}
		return removed;
	}

	@Override
	public List<TypePropertyGroup> group() {
		return holder.getPropertyGroups();
	}

	@Override
	public Integer initialOrdinalNumber() {
		return INITIAL_ORDINAL_NUMBER;
	}

}
