package ru.argustelecom.box.nri.booking.services;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.booking.BookingOrderRepository;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressRepository;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.schema.requirements.ip.model.IpAddressBookingRequirement;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.inject.Inject;
import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;

/**
 * Сервис брониварояния ip-адресов
 * Created by s.kolyada on 19.12.2017.
 */
@ApplicationService
public class IpAddressBookingAppService implements IBookingService, Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Репозиторий доступа к ip-адресам
	 */
	@Inject
	private IPAddressRepository ipAddressRepository;

	/**
	 * Репозиторий доступа к нарядам на бронирование
	 */
	@Inject
	private BookingOrderRepository bookingOrderRepository;

	@Override
	public BookingOrder book(Service serviceInstance, ResourceRequirement requirement) {
		IpAddressBookingRequirement bookingRequirement = (IpAddressBookingRequirement) requirement;
		IPAddress.IPAddressQuery query = new IPAddress.IPAddressQuery();
		List<Predicate> predicates = bookingRequirement.createPredicates(query);

		IPAddress ipAddress = ipAddressRepository.findFirstWithPredicates(predicates,query);

		return  ipAddress == null ? null :
				bookingOrderRepository.createBookingOrder(Collections.singleton(ipAddress),
						serviceInstance,
						requirement);
	}
}
