package ru.argustelecom.box.nri.integration.frames;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import org.primefaces.model.DualListModel;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.nri.booking.BookingAppService;
import ru.argustelecom.box.nri.booking.PhoneNumberBookingRequirementDto;
import ru.argustelecom.box.nri.booking.PhoneNumberBookingRequirementDtoTranslator;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.integration.viewmodel.BookingResourceDataHolder;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberAppService;
import ru.argustelecom.box.nri.logicalresources.phone.PhoneNumberDto;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.box.nri.schema.requirements.phone.model.PhoneNumberBookingRequirement;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Контроллер фрейма подбора телефонного номера
 * Created by s.kolyada on 07.02.2018.
 */
@Named(value = "bookPhoneNumberFrameModel")
@PresentationModel
public class BookPhoneNumberFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Текущая бронь
	 */
	@Getter
	private BookingResourceDataHolder currentBooking;

	/**
	 * Текущая услуга
	 */
	private Service currentService;

	/**
	 * Текущее требование к ресурсу
	 */
	@Getter
	private PhoneNumberBookingRequirementDto currentRequirement;

	/**
	 * Доступные для бронирования ресурсы
	 */
	@Getter
	@Setter
	private DualListModel<PhoneNumberDto> availableResources;

	/**
	 * Транслятор требования к брони телефонного номера
	 */
	@Inject
	private PhoneNumberBookingRequirementDtoTranslator translator;

	/**
	 * Сервис работы с телефонными номерами
	 */
	@Inject
	private PhoneNumberAppService phoneNumberAppService;

	/**
	 * Сервис работы с бронями
	 */
	@Inject
	private BookingAppService bookingAppService;

	/**
	 * Подготовка к отображению
	 *
	 * @param booking бронирование
	 */
	public void preRender(Service service, BookingResourceDataHolder booking) {
		if (booking == null || booking.getRequirement() == null
				|| !RequirementType.PHONE_NUMBER_BOOKING_REQUIREMENT.equals(booking.getRequirement().getType())) {
			reset();
			return;
		}
		this.currentBooking = booking;
		this.currentService = service;
		currentRequirement = translator.translate((PhoneNumberBookingRequirement) booking.getRequirement());

		availableResources = new DualListModel<>(new ArrayList<>(), new ArrayList<>());
	}

	/**
	 * Сбросить параметры
	 */
	private void reset() {
		currentBooking = null;
		currentService = null;
		currentRequirement = null;
		availableResources.getSource().clear();
		availableResources.getTarget().clear();
	}

	/**
	 * Искать ресурсы подходящие под требования
	 */
	public void find() {
		availableResources.getSource().clear();

		currentRequirement.setState(PhoneNumberState.AVAILABLE);
		currentRequirement.setCanHaveBookings(false);
		List<PhoneNumberDto> logicalResourceDtos = phoneNumberAppService.findPhoneNumbersLike(currentRequirement);

		if (CollectionUtils.isEmpty(logicalResourceDtos)) {
			return;
		}
		logicalResourceDtos.removeAll(availableResources.getTarget());
		availableResources.setSource(logicalResourceDtos);
	}

	/**
	 * Забронировать выбранные ресурсы
	 */
	public void book() {
		List<PhoneNumberDto> phones = availableResources.getTarget();

		if (CollectionUtils.isEmpty(phones)) {
			return;
		}

		// создаём наряд на бронирование
		BookingOrder order = bookingAppService.bookPhoneNumbers(currentService,
				currentRequirement,
				phones);

		// обновляем текущи букинг
		currentBooking.setBookingOrder(order);
	}
}
