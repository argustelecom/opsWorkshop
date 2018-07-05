package ru.argustelecom.box.nri.booking;

import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.booking.nls.BookingAppServiceMessagesBundle;
import ru.argustelecom.box.nri.booking.services.IpAddressBookingAppService;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberRepository;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.schema.ResourceSchemaRepository;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.BookingRequirementRepository;
import ru.argustelecom.box.nri.schema.requirements.ip.model.IpAddressBookingRequirement;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.system.inf.utils.CheckUtils;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Сервис для работы с бронями
 * Created by s.kolyada on 18.12.2017.
 */
@ApplicationService
public class BookingAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Репозиторий дступа к нарядам на бронирование
	 */
	@Inject
	private BookingOrderRepository bookingOrderRepository;

	/**
	 * Сервис бронирования ip-адресов
	 */
	@Inject
	private IpAddressBookingAppService ipAddressBookingAppService;

	/**
	 * Транслятор требований к бронированиям
	 */
	@Inject
	private BookingRequirementDtoTranslator bookingRequirementDtoTranslator;

	/**
	 * Репозиторий схем подключения
	 */
	@Inject
	private ResourceSchemaRepository resourceSchemaRepository;

	/**
	 * Транслятор ДТО логических ресурсов
	 */
	@Inject
	private LogicalResourceDtoTranslator logicalResourceDtoTranslator;

	@Inject
	private PhoneNumberRepository pnRepository;

	/**
	 * Репозиторий доступа к общим данным требований
	 */
	@Inject
	private BookingRequirementRepository bookingRequirementRepository;

	/**
	 * Забронировать ресурс под требование
	 * @param requirement требование к бронируемому ресурсу
	 * @param serviceInstance услуга
	 * @return созданный наряд на бронирование или null, если не удалось ничего забронировать
	 */
	public BookingOrder bookResource(ResourceRequirement requirement, Service serviceInstance) {
		if (requirement instanceof IpAddressBookingRequirement) {
			return ipAddressBookingAppService.book(serviceInstance,requirement);
		}
		BookingAppServiceMessagesBundle messages = LocaleUtils.getMessages(BookingAppServiceMessagesBundle.class);
		throw new IllegalStateException(messages.unsupportedRequirementType());
	}

	/**
	 * Отмена бронирования
	 * @param bookingOrder наряд на бронирование
	 * @return истина, если успешно снято бронирование, иначе ложь
	 */
	public boolean releaseBooking(BookingOrder bookingOrder) {
		return bookingOrderRepository.release(bookingOrder);
	}

	/**
	 * Загрузить требования к бронированию ресурсов по схеме
	 * @param id идентификатор схемы
	 * @return список требований
	 */
	public List<BookingRequirementDto> loadBookingRequirementsBySchema(Long id) {
		ResourceSchema schema = resourceSchemaRepository.findById(id);
		if (schema == null || CollectionUtils.isEmpty(schema.getBookings())) {
			return new ArrayList<>();
		} else {
			return schema.getBookings()
					.stream()
					.map(bookingRequirementDtoTranslator::translate)
					.collect(Collectors.toList());
		}
	}

	/**
	 * Получить все брони по услуге
	 * @param serviceInstance услуга
	 * @return список бронирований
	 */
	public List<BookingOrder> loadAllBookingsByService(Service serviceInstance){
		return bookingOrderRepository.loadAllLoadingsByService(serviceInstance);
	}

	/**
	 * Загрузить все ресурсы текущего наряда на бронирование
	 * @param order бронь
	 * @return список всех ресурсов
	 */
	public Set<LogicalResourceDto> loadAllBookedResourceByOrder(BookingOrder order) {
		BookingOrder bookingOrder = bookingOrderRepository.findOne(order.getId());

		return bookingOrder.getBookedLogicalResource().stream()
				.map(logicalResourceDtoTranslator::translate)
				.collect(Collectors.toSet());
	}

	/**
	 * Забронировать список телефонных номеров
	 * @param service услуга под которую осуществляется бронирование
	 * @param currentRequirement требование по которому осуществляется бронирование
	 * @param phonesToBook бронируемые телефонные номера
	 * @return наряд на бронирование
	 */
	public BookingOrder bookPhoneNumbers(Service service,
										 PhoneNumberBookingRequirementDto currentRequirement,
										 List<PhoneNumberDto> phonesToBook) {
		BookingAppServiceMessagesBundle messages = LocaleUtils.getMessages(BookingAppServiceMessagesBundle.class);
		CheckUtils.checkArgument(Objects.nonNull(service), messages.didNotPassServiceInstance());
		CheckUtils.checkArgument(Objects.nonNull(currentRequirement), messages.didNotPassRequirementForBooking());
		CheckUtils.checkArgument(CollectionUtils.isNotEmpty(phonesToBook), messages.didNotPassPhoneNumberListForBooking());

		// проинициализируем телефонные номера
		List<Long> ids = phonesToBook.stream()
				.map(PhoneNumberDto::getId)
				.collect(Collectors.toList());
		List<PhoneNumber> phones = pnRepository.findMany(ids);

		// проинициализируем требование
		ResourceRequirement requirement = bookingRequirementRepository.findOne(currentRequirement.getId());

		// создадим наряд на бронирование
		return bookingOrderRepository.createBookingOrder(new HashSet<>(phones), service, requirement);
	}
}
