package ru.argustelecom.box.env.commodity.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;

import ru.argustelecom.box.env.type.model.SupportFiltering;
import ru.argustelecom.box.env.type.model.SupportUniqueProperty;

/**
 * Сущность описывающая типы услуг.
 */
@Entity
@Access(AccessType.FIELD)
@SupportFiltering
@SupportUniqueProperty
public class ServiceType extends CommodityType {

	//@formatter:off
	@ManyToMany(cascade = CascadeType.ALL)
	@JoinTable(schema = "system", name = "option_type_service_type",
			joinColumns = @JoinColumn(name = "service_type_id", referencedColumnName = "id"),
			inverseJoinColumns = @JoinColumn(name = "option_type_id", referencedColumnName = "id"))
	//@formatter:on
	private List<OptionType> optionTypes = new ArrayList<>();

	public List<OptionType> getOptionTypes() {
		return Collections.unmodifiableList(optionTypes);
	}

	List<OptionType> getMutableOptionTypes() {
		return optionTypes;
	}

	public void addOptionType(OptionType optionType) {
		checkNotNull(optionType);
		checkState(!optionTypes.contains(optionType));

		optionTypes.add(optionType);
		optionType.getMutableServiceTypes().add(this);
	}

	public void removeOptionType(OptionType optionType) {
		checkNotNull(optionType);
		checkState(optionTypes.contains(optionType));

		optionTypes.remove(optionType);
		optionType.getMutableServiceTypes().remove(this);
	}

	protected ServiceType() {
		super();
	}

	protected ServiceType(Long id) {
		super(id);
	}

	public static class ServiceTypeQuery<T extends ServiceType> extends CommodityTypeQuery<T> {

		public ServiceTypeQuery(Class<T> entityClass) {
			super(entityClass);
		}

	}

	private static final long serialVersionUID = 7841360317424078139L;

}