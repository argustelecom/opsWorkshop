package ru.argustelecom.box.nri.booking;

import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.booking.nls.BookingRequirementAppServiceMessagesBundle;
import ru.argustelecom.box.nri.schema.requirements.BookingRequirementRepository;
import ru.argustelecom.box.nri.schema.requirements.ip.IpAddressBookingRequirementRepository;
import ru.argustelecom.box.nri.schema.requirements.ip.model.IpAddressBookingRequirement;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.schema.requirements.phone.PhoneNumberBookingRequirementRepository;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;
import ru.argustelecom.system.inf.exception.BusinessExceptionWithoutRollback;
import ru.argustelecom.system.inf.utils.CheckUtils;

import javax.inject.Inject;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

/**
 * Сервис для работы с требованиями к бронированию
 * Created by s.kolyada on 22.12.2017.
 */
@ApplicationService
public class BookingRequirementAppService implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * репозиторий ip-адресов
	 */
	@Inject
	private IpAddressBookingRequirementRepository repository;

	/**
	 * Репозиторий телефонных номеров
	 */
	@Inject
	private PhoneNumberBookingRequirementRepository pnRepository;

	/**
	 * Репозиторий требований к бронированию
	 */
	@Inject
	private BookingRequirementRepository brRepository;

	/**
	 * Репозиторий бронирований
	 */
	@Inject
	BookingOrderRepository bookingOrderRepository;

	/**
	 * Транслятор ip-адресов
	 */
	@Inject
	private IpAddressBookingRequirementDtoTranslator translator;

	/**
	 * Транслятор телефонных номеров
	 */
	@Inject
	private PhoneNumberBookingRequirementDtoTranslator pnTranslator;

	/**
	 * Сохранить требование
	 *
	 * @param requirement требование
	 * @return дто требования
	 */
	public IpAddressBookingRequirementDto saveRequirement(IpAddressBookingRequirementDto requirement) {
		BookingRequirementAppServiceMessagesBundle messages = LocaleUtils.getMessages(BookingRequirementAppServiceMessagesBundle.class);
		CheckUtils.checkArgument(requirement != null, messages.requirementIsNeeded());
		CheckUtils.checkArgument(requirement.getSchema() != null, messages.schemaIsNeededForRequirement());

		IpAddressBookingRequirement res = repository.create(requirement.getName(),
				requirement.getShouldBePrivate(),
				requirement.getShouldHaveState(),
				requirement.getShouldHaveBooking(),
				requirement.getShouldBeStatic(),
				requirement.getShouldHaveTransferType(),
				requirement.getShouldHavePurpose(),
				requirement.getSchema().getId());

		if (Objects.isNull(res)) {
			return null;
		}

		return translator.translate(res);
	}

	/**
	 * Сохранить требование
	 *
	 * @param requirement требование
	 * @return дто требования
	 */
	public PhoneNumberBookingRequirementDto saveRequirement(PhoneNumberBookingRequirementDto requirement) {
		BookingRequirementAppServiceMessagesBundle messages = LocaleUtils.getMessages(BookingRequirementAppServiceMessagesBundle.class);
		CheckUtils.checkArgument(requirement != null, messages.requirementIsNeeded());
		CheckUtils.checkArgument(requirement.getSchema() != null, messages.schemaIsNeededForRequirement());

		PhoneNumberBookingRequirement req = pnRepository.create(requirement.name,
				requirement.getSchema().getId());

		if (Objects.isNull(req)) {
			return null;
		}

		return pnTranslator.translate(req);
	}

	/**
	 * Найти требование по идентификатору
	 *
	 * @param id идентификатор
	 * @return требование
	 */
	public IpAddressBookingRequirementDto findById(Long id) {
		IpAddressBookingRequirement res = repository.findById(id);
		if (Objects.isNull(res)) {
			return null;
		}

		return translator.translate(res);
	}

	/**
	 * Удалить требование
	 *
	 * @param selectedRequirement требование
	 * @return истина если успешно, иначе ложь
	 */
	public Boolean removeRequirement(BookingRequirementDto selectedRequirement) throws BusinessExceptionWithoutRollback {
		BookingRequirementAppServiceMessagesBundle messages = LocaleUtils.getMessages(BookingRequirementAppServiceMessagesBundle.class);

		if (selectedRequirement == null)
			throw new BusinessExceptionWithoutRollback(messages.requirementIsNeeded());
		ResourceRequirement br = brRepository.findOne(selectedRequirement.getId());

		if (br == null)
			throw new BusinessExceptionWithoutRollback(messages.requirementCouldNotFind());

		List<BookingOrder> orders =  bookingOrderRepository.loadAllBookingsByRequirement(br);
		//высвобождаем забронированные ресурсы
		if (CollectionUtils.isNotEmpty(orders)){
			orders.forEach(bookingOrderRepository::release);
		}

		switch (selectedRequirement.getBookingType()) {
			case IP_ADDRESS_BOOKING_REQUIREMENT:
				return repository.remove(selectedRequirement.getId());
			case PHONE_NUMBER_BOOKING_REQUIREMENT:
				return pnRepository.remove(selectedRequirement.getId());
			default:
				return false;
		}
	}
}
