package ru.argustelecom.box.nri.integration;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.integration.nri.ResourceBookingCheckService;
import ru.argustelecom.box.integration.nri.ResourceBookingResult;
import ru.argustelecom.box.integration.nri.ResourceBookingService;
import ru.argustelecom.box.integration.nri.ResourceLoadingResult;
import ru.argustelecom.box.integration.nri.ResourceLoadingService;
import ru.argustelecom.box.nri.booking.BookingAppService;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.integration.nls.NriIntegrationMessageBundle;
import ru.argustelecom.box.nri.loading.ResourceLoadingAppService;
import ru.argustelecom.box.nri.loading.model.ResourceLoading;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaAppService;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.annotation.Nonnull;
import javax.annotation.Priority;
import javax.enterprise.inject.Alternative;
import javax.inject.Inject;
import javax.interceptor.Interceptor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;
import static ru.argustelecom.system.inf.utils.CheckUtils.checkNotNull;

/**
 * Интеграционный сервис для бронивраония ресурсов
 * Created by s.kolyada on 18.12.2017.
 */
@DomainService
@Alternative
@Priority(Interceptor.Priority.APPLICATION)
public class ResourceBookingServiceImpl implements ResourceBookingService, ResourceLoadingService, ResourceBookingCheckService {

	private static final Logger log = Logger.getLogger(ResourceBookingServiceImpl.class);

	/**
	 * Сервис бронирования
	 */
	@Inject
	private BookingAppService bookingAppService;

	/**
	 * сервис для работы со схемами
	 */
	@Inject
	private ResourceSchemaAppService resourceSchemaAppService;

	/**
	 * Сервис нагрузки
	 */
	@Inject
	private ResourceLoadingAppService resourceLoadingAppService;

	@Override
	public ResourceBookingResult bookResources(@Nonnull Service serviceInstance) {
		NriIntegrationMessageBundle messages = LocaleUtils.getMessages(NriIntegrationMessageBundle.class);
		List<ResourceRequirement> bookings = new ArrayList<>();

		resourceSchemaAppService.findAllByServiceSpec(initializeAndUnproxy(serviceInstance.getPrototype()))
				.forEach(schema -> bookings.addAll(schema.getBookings()));
		if (CollectionUtils.isEmpty(bookings)) {
			return new ResourceBookingResult(false, messages.noNeedAnyResourceToBook());
		}

		Map<ResourceRequirement, BookingOrder> bookingResults = new HashMap<>();
		bookings.forEach(b -> bookingResults.put(b, null));

		boolean couldNotBookSomeItem = false;
		try {
			for (ResourceRequirement requirement : bookingResults.keySet()) {
				BookingOrder bookingOrder = bookingAppService.bookResource(requirement, serviceInstance);
				if (bookingOrder != null) {
					log.debug(messages.bookingWasCreated() + bookingOrder.getObjectName());
					bookingResults.put(requirement, bookingOrder);
				} else {
					couldNotBookSomeItem = true;
					break;
				}
			}
		} finally {
			if (couldNotBookSomeItem) {
				bookingResults.values().removeIf(Objects::isNull);
				for (BookingOrder order : bookingResults.values()) {
					Boolean res = bookingAppService.releaseBooking(order);
					if (res) {
						log.debug(messages.bookingWasCanceled() + order.getObjectName());
					} else {
						log.warn(messages.cancelingOfBookingWasFailed() + order.getObjectName());
					}
				}
			}
		}

		if (couldNotBookSomeItem) {
			return new ResourceBookingResult(messages.couldNotBookAllNeededResources());
		}

		return new ResourceBookingResult(messages.resourcesWereBookedWithSuccess());
	}

	@Override
	public ResourceBookingResult releaseBooking(Service serviceInstance) {
		NriIntegrationMessageBundle messages = LocaleUtils.getMessages(NriIntegrationMessageBundle.class);
		List<BookingOrder> bookingOrders = bookingAppService.loadAllBookingsByService(serviceInstance);
		boolean hasUnreleasedOrders = false;
		for (BookingOrder bookingOrder : bookingOrders) {
			if (!bookingAppService.releaseBooking(bookingOrder)) {
				hasUnreleasedOrders = true;
			}
		}

		if (hasUnreleasedOrders) {
			return new ResourceBookingResult(false, messages.couldNotReleaseSomeResources());
		}

		return new ResourceBookingResult(messages.resourcesWereReleased());
	}

	@Override
	public ResourceLoadingResult loadResources(Service serviceInstance) {
		NriIntegrationMessageBundle messages = LocaleUtils.getMessages(NriIntegrationMessageBundle.class);
		List<BookingOrder> booked = bookingAppService.loadAllBookingsByService(serviceInstance);
		for (BookingOrder bookingOrder : booked) {
			ResourceLoading rl = resourceLoadingAppService.loadResource(bookingOrder);
			log.debug(messages.loaded() + rl.getObjectName());
		}
		for (BookingOrder bookingOrder : booked) {
			if (bookingAppService.releaseBooking(bookingOrder)) {
				log.debug(messages.allResourcesWereReleased());
			} else {
				log.debug(messages.someBookingsWereNotReleased());
				//todo надо подумать что с этим делать
			}
		}
		return new ResourceLoadingResult(messages.resourcesWereLoaded());
	}

	@Override
	public ResourceLoadingResult releaseLoading(Service serviceInstance) {
		NriIntegrationMessageBundle messages = LocaleUtils.getMessages(NriIntegrationMessageBundle.class);
		List<ResourceLoading> loaded = resourceLoadingAppService.loadAllLoadingsByService(serviceInstance);
		for (ResourceLoading loading : loaded) {
			resourceLoadingAppService.releaseLoading(loading);
		}
		return new ResourceLoadingResult(messages.resourcesWereUnloaded());
	}

	@Override
	public boolean check(Service serviceInstance) {
		NriIntegrationMessageBundle messages = LocaleUtils.getMessages(NriIntegrationMessageBundle.class);
		checkNotNull(serviceInstance, messages.serviceDidNotSet());

		List<ResourceSchema> schemas = resourceSchemaAppService.findAllByServiceSpec(serviceInstance.getPrototype());

		if (CollectionUtils.isEmpty(schemas))
			return true;
		List<BookingOrder> bookingOrders = bookingAppService.loadAllBookingsByService(serviceInstance);


		//Если у нас нет ни одной брони, то надо проверить есть ли хотя бы одна схема с нулём требований
		if (CollectionUtils.isEmpty(bookingOrders)) {
			for (ResourceSchema sc : schemas) {
				if (CollectionUtils.isEmpty(sc.getBookings())) {
					return true;
				}
			}
			return false;
		}
		//Определим по какой схеме сделаны брони
		Set<ResourceSchema> schemasSet = new HashSet<>(bookingOrders.size());
		bookingOrders.forEach(order -> {
			if (order.getRequirement().getSchema() != null)
				schemasSet.add(order.getRequirement().getSchema());
		});
		ResourceSchema currentSchema;
		if (schemasSet.isEmpty()) {
			log.warn("Брони не имеют связи со схемами, id = " + StringUtils.join(bookingOrders.stream().map(booking -> booking.getId()).collect(Collectors.toList()), ","));
			return false;
		} else if (schemasSet.size() > 1)
			throw new IllegalStateException(messages.foundRequirementForDifferentSchemas());
		else
			currentSchema = schemasSet.iterator().next();
		//на всякий случай проверим что схема есть в списке, на самом деле её не может не быть там
		if (!schemas.contains(currentSchema))
			throw new IllegalStateException(messages.schemaDoesNotMatchAnyScheme() + serviceInstance.getObjectName());
		List requirementList = bookingOrders.stream().map(BookingOrder::getRequirement).collect(Collectors.toList());
		return currentSchema.getBookings().containsAll(requirementList) && currentSchema.getBookings().size() == requirementList.size();
	}
}
