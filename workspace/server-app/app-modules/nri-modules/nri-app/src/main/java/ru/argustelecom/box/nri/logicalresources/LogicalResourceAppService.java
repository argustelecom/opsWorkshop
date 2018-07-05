package ru.argustelecom.box.nri.logicalresources;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.booking.BookingOrderRepository;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.loading.ResourceLoadingRepository;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressDto;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.argustelecom.system.inf.utils.CheckUtils.checkNotNull;

/**
 * Сервис работы с логическими ресурсами
 * Created by s.kolyada on 22.12.2017.
 */
@ApplicationService
public class LogicalResourceAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Репозиторий дступа к нагрузкам
	 */
	@Inject
	private ResourceLoadingRepository resourceLoadingRepository;

	/**
	 * Репозиторий доступа к броням
	 */
	@Inject
	private BookingOrderRepository bookingOrderRepository;

	/**
	 * Транслятор ip-адресов
	 */
	@Inject
	private IPAddressDtoTranslator ipAddressDtoTranslator;

	/**
	 * Найти нагрузки по услуге@param service
	 * @param service услуга
	 * @return  список адресов
	 */
	public Set<IPAddressDto> loadLoadedResourcesByService(Service service) {
		Set<IPAddress> res = new HashSet<>();

		// получаем нагруженные услугой ресурсы
		List<ResourceLoading> loadings = resourceLoadingRepository.loadAllLoadingsByService(service);
		for (ResourceLoading loading : loadings) {
			loading.getLoadedLogicalResource().forEach(l -> res.add((IPAddress) l));
		}
		// транслируем в ДТО и возвращаем
		return res.stream().map(ipAddressDtoTranslator::translate).collect(Collectors.toSet());
	}

	/**
	 * Найти бронирования по услуге
	 * @param service услуга
	 * @return список усллллллулллл
	 */
	public Set<IPAddressDto> loadBookedResourcesByService(Service service) {
		Set<IPAddress> res = new HashSet<>();

		// получаем забронированные под услугу ресурсы
		List<BookingOrder> bookings = bookingOrderRepository.loadAllLoadingsByService(service);
		for (BookingOrder order : bookings) {
			order.getBookedLogicalResource().forEach(l -> res.add((IPAddress)l));
		}

		// транслируем в ДТО и возвращаем
		return res.stream().map(ipAddressDtoTranslator::translate).collect(Collectors.toSet());
	}

	/**
	 * Загрузить все ресурсы под данной услугой
	 * @param service услуга
	 * @return список ресурсов
	 */
	public Set<IPAddressDto> loadAllResourcesByService(Service service) {
		Set<IPAddressDto> res = new HashSet<>();

		res.addAll(loadLoadedResourcesByService(service));
		res.addAll(loadBookedResourcesByService(service));

		// транслируем в ДТО и возвращаем
		return res;
	}

	/**
	 * Найти услугу, за которой закреплён адрес
	 * @param id идентификатор адреса
	 * @return услуга
	 */
	public Service findService(Long id) {
		checkNotNull(id, "No ID specified");

		ResourceLoading loading = resourceLoadingRepository.findLoadingByResource(id);

		if (loading != null) {
			return loading.getServiceInstance();
		}

		BookingOrder bookingOrder = bookingOrderRepository.findBookingByResource(id);
		if (bookingOrder != null) {
			return bookingOrder.getServiceInstance();
		}

		return null;
	}
}
