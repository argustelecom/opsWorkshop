package ru.argustelecom.box.nri.service;

import lombok.Getter;
import org.jboss.logging.Logger;
import org.primefaces.event.ToggleEvent;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.booking.BookingAppService;
import ru.argustelecom.box.nri.booking.BookingRequirementAppService;
import ru.argustelecom.box.nri.booking.BookingRequirementDto;
import ru.argustelecom.box.nri.booking.IpAddressBookingRequirementDto;
import ru.argustelecom.box.nri.booking.PhoneNumberBookingRequirementDto;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.box.nri.service.nls.SchemaBookingInfoFrameModelMessagesBundle;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.List;
import java.util.function.Consumer;


/**
 * Модель формы работы с требованиями к парметрам
 */
@Named(value = "schemaBookingInfoFrameModel")
@PresentationModel
public class SchemaBookingInfoFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(SchemaBookingInfoFrameModel.class);

	@Inject
	private BookingAppService bookingAppService;

	@Getter
	private List<BookingRequirementDto> bookingRequirementDtos;

	@Getter
	private BookingRequirementDto selectedRequirement;

	@Inject
	private BookingRequirementAppService bookingRequirementAppService;

	/**
	 * Создать требование к IP адресу
	 */
	@Getter
	private Consumer<IpAddressBookingRequirementDto> createNewIpAddressRequirement = (newRequirement) -> {
		bookingRequirementAppService.saveRequirement(newRequirement);
		init();
	};

	/**
	 * текущая схема подключения
	 */
	@Getter
	private ResourceSchemaDto currentSchema;

	/**
	 * Инициализация
	 *
	 * @param schema схема
	 */
	public void preRender(ResourceSchemaDto schema) {
		currentSchema = schema;

		if (currentSchema == null) {
			return;
		}
		init();
	}

	private void init() {
		bookingRequirementDtos = bookingAppService.loadBookingRequirementsBySchema(currentSchema.getId());

	}

	public void onRowToggle(ToggleEvent event) {
		BookingRequirementDto selectedRawDto = (BookingRequirementDto) event.getData();

		if (selectedRawDto == null) {
			return;
		}

		selectedRequirement = selectedRawDto;
	}

	public void removeSelectedReq() {
		try {
			bookingRequirementAppService.removeRequirement(selectedRequirement);
			init();
		} catch (Exception e) {
			SchemaBookingInfoFrameModelMessagesBundle messages = LocaleUtils.getMessages(SchemaBookingInfoFrameModelMessagesBundle.class);
			log.error("Не удалось удалить требование", e);
			Notification.warn(messages.error(), messages.couldNotDeleteRequirement() + e.getMessage() );
		}

	}

	/**
	 * Добавить бронивароние телефонного номера
	 * TODO временное решение, пока само требование не имеет никаких полей для атвоподбора
	 */
	public void addPhoneBooking() {
		PhoneNumberBookingRequirementDto requirement = PhoneNumberBookingRequirementDto.builder()
				.name(RequirementType.PHONE_NUMBER_BOOKING_REQUIREMENT.getCaption())
				.schema(currentSchema)
				.build();


		requirement = bookingRequirementAppService.saveRequirement(requirement);

		bookingRequirementDtos.add(requirement);
	}
}
