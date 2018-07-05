package ru.argustelecom.box.nri.service;

import lombok.Getter;
import ru.argustelecom.box.nri.booking.BookingRequirementAppService;
import ru.argustelecom.box.nri.booking.BookingRequirementDto;
import ru.argustelecom.box.nri.booking.IpAddressBookingRequirementDto;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

/**
 * Модель формы работы с требованиями к парметрам IP-Адреса
 */
@Named(value = "ipAddressBookingRequirementInfoFrameModel")
@PresentationModel
public class IPAddressBookingRequirementInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * выбранное требование
	 */
	@Getter
	private IpAddressBookingRequirementDto selectedIpRequirement;

	/**
	 * Сервис для работы с требованиями к бронированию
	 */
	@Inject
	private BookingRequirementAppService bookingRequirementAppService;

	/**
	 * Инициализация
	 *
	 * @param requirement требование
	 */
	public void preRender(BookingRequirementDto requirement) {
		selectedIpRequirement = bookingRequirementAppService.findById(requirement.getId());
	}

	/**
	 * Инициализация
	 *
	 * @param requirement требование
	 */
	public void preRender(ResourceRequirement requirement) {
		selectedIpRequirement = bookingRequirementAppService.findById(requirement.getId());
	}
}
