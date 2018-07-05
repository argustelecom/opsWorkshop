package ru.argustelecom.box.env.commodity;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.findList;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.OptionType;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class OptionTypeAppService implements Serializable {

	private static final long serialVersionUID = -1096360156234033588L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private OptionTypeRepository optionTypeRp;

	public List<OptionType> findAll() {
		return optionTypeRp.findAll();
	}

	public void changeOptionTypes(Long serviceTypeId, List<Long> optionTypeIds) {
		checkNotNull(serviceTypeId);
		checkNotNull(optionTypeIds);

		ServiceType serviceType = em.find(ServiceType.class, serviceTypeId);
		checkNotNull(serviceType);

		List<OptionType> optionTypes = findList(em, OptionType.class, optionTypeIds);
		List<OptionType> currentOptionTypes = serviceType.getOptionTypes();

		List<OptionType> removableOptionTypes = currentOptionTypes.stream()
				.filter(optionType -> !optionTypes.contains(optionType)).collect(toList());
		removableOptionTypes.forEach(serviceType::removeOptionType);

		optionTypes.stream().filter(optionType -> !currentOptionTypes.contains(optionType))
				.forEach(serviceType::addOptionType);
	}

	public void changeServiceTypes(Long optionTypeId, List<Long> serviceTypeIds) {
		checkNotNull(optionTypeId);
		checkNotNull(serviceTypeIds);

		OptionType optionType = em.find(OptionType.class, optionTypeId);
		checkNotNull(optionType);

		List<ServiceType> serviceTypes = findList(em, ServiceType.class, serviceTypeIds);
		List<ServiceType> currentServiceTypes = optionType.getServiceTypes();

		List<ServiceType> removableServiceTypes = currentServiceTypes.stream()
				.filter(serviceType -> !serviceTypes.contains(serviceType)).collect(toList());
		removableServiceTypes.forEach(optionType::removeServiceType);

		serviceTypes.stream().filter(serviceType -> !currentServiceTypes.contains(serviceType))
				.forEach(optionType::addServiceType);
	}

}
