package ru.argustelecom.box.env.commodity;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.OptionType.OptionTypeQuery;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.inf.service.Repository;

/**
 * Репозиторий для работы с {@linkplain ru.argustelecom.box.env.commodity.model.OptionType типами опций}.
 */
@Repository
public class OptionTypeRepository implements Serializable {

	private static final long serialVersionUID = 6626443300102630328L;

	@PersistenceContext
	private EntityManager em;

	public List<OptionType> findAll() {
		return new OptionTypeQuery<>(OptionType.class).getResultList(em);
	}

	public void addOptionTypes(ServiceType serviceType, Collection<OptionType> optionTypes) {
		checkRequiredArgument(serviceType, "ServiceType");
		checkNotNull(optionTypes);

		optionTypes.forEach(serviceType::addOptionType);
	}

	public void removeOptionTypes(ServiceType serviceType, Collection<OptionType> optionTypes) {
		checkRequiredArgument(serviceType, "ServiceType");
		checkNotNull(optionTypes);

		optionTypes.forEach(serviceType::removeOptionType);
	}

	public void addServiceTypes(OptionType optionType, Collection<ServiceType> serviceTypes) {
		checkRequiredArgument(optionType, "OptionType");
		checkNotNull(serviceTypes);

		serviceTypes.forEach(optionType::addServiceType);
	}

	public void removeServiceTypes(OptionType optionType, Collection<ServiceType> serviceTypes) {
		checkRequiredArgument(optionType, "OptionType");
		checkNotNull(serviceTypes);

		serviceTypes.forEach(optionType::removeServiceType);
	}

}
