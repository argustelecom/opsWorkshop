package ru.argustelecom.box.env.commodity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.service.ApplicationService;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

@ApplicationService
public class ServiceAppService implements Serializable {

	private static final long serialVersionUID = 7753323862841218180L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CommodityRepository commodityRp;

	@Inject
	private RatedOutgoingCallsRepository ratedOutgoingCallsRp;

	public List<Service> findBy(Long customerId) {
		checkNotNull(customerId);

		Customer customer = em.find(Customer.class, customerId);
		checkNotNull(customer);

		return commodityRp.findBy(customer);
	}

	public List<Service> findByType(Long serviceTypeId) {
		checkNotNull(serviceTypeId);

		ServiceType serviceType = em.find(ServiceType.class, serviceTypeId);
		checkNotNull(serviceType);

		return commodityRp.findBy(serviceType);
	}

	public List<Service> findBy(Long customerId, Long serviceTypeId) {
		checkNotNull(customerId);
		checkNotNull(serviceTypeId);

		Customer customer = em.find(Customer.class, customerId);
		checkNotNull(customer);

		ServiceType serviceType = em.find(ServiceType.class, serviceTypeId);
		checkNotNull(serviceTypeId);

		return commodityRp.findBy(customer, serviceType);
	}

	public List<Service> findCrossingServices(List<Long> serviceIds, Date dateFrom, Date dateTo) {
		checkRequiredArgument(serviceIds, "serviceIds");
		checkNotNull(dateFrom);
		checkNotNull(dateTo);

		return ratedOutgoingCallsRp.findCrossingServices(serviceIds, dateFrom, dateTo);
	}

	public List<Service> findCrossingServicesByTariff(Long tariffId, Date dateFrom, Date dateTo) {
		checkRequiredArgument(tariffId, "serviceIds");
		checkNotNull(dateFrom);
		checkNotNull(dateTo);

		return ratedOutgoingCallsRp.findCrossingServicesByTariff(tariffId, dateFrom, dateTo);
	}

	public List<Service> findCrossingServicesWithTariff(Long tariffId, Date dateFrom, Date dateTo, List<Long> serviceIds) {
		checkRequiredArgument(tariffId, "tariffId");
		checkRequiredArgument(serviceIds, "serviceIds");
		checkNotNull(dateFrom);
		checkNotNull(dateTo);

		return ratedOutgoingCallsRp.findCrossingServicesWithTariff(tariffId, dateFrom, dateTo, serviceIds);
	}

	public List<Service> findAllServices() {
		return commodityRp.findAllServices();
	}
}
