package ru.argustelecom.box.env.commodity.model;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;

/**
 * Сущность описывающая типы опций.
 */
@Entity
@Access(AccessType.FIELD)
public abstract class OptionType extends CommodityType {

	private static final long serialVersionUID = -5841314723168154221L;

	@ManyToMany(mappedBy = "optionTypes")
	private List<ServiceType> serviceTypes = new ArrayList<>();

	public List<ServiceType> getServiceTypes() {
		return Collections.unmodifiableList(serviceTypes);
	}

	List<ServiceType> getMutableServiceTypes() {
		return serviceTypes;
	}

	public void addServiceType(ServiceType serviceType) {
		checkNotNull(serviceType);
		checkState(!serviceTypes.contains(serviceType));

		serviceTypes.add(serviceType);
		serviceType.getMutableOptionTypes().add(this);
	}

	public void removeServiceType(ServiceType serviceType) {
		checkNotNull(serviceType);
		checkState(serviceTypes.contains(serviceType));

		serviceTypes.remove(serviceType);
		serviceType.getMutableOptionTypes().remove(this);
	}

	protected OptionType() {
		super();
	}

	protected OptionType(Long id) {
		super(id);
	}

	public static class OptionTypeQuery<T extends OptionType> extends CommodityTypeQuery<T> {

		public OptionTypeQuery(Class<T> entityClass) {
			super(entityClass);
		}

	}

}