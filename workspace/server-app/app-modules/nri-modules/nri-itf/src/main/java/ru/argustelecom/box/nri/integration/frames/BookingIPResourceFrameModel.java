package ru.argustelecom.box.nri.integration.frames;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.booking.services.IpAddressBookingAppService;
import ru.argustelecom.box.nri.integration.frames.nls.BookingIPResourceFrameModelMessagesBundle;
import ru.argustelecom.box.nri.integration.viewmodel.BookingResourceDataHolder;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.function.Consumer;

/**
 * Контроллер фрейма вывода информации о бронировании IP адреса
 * Created by b.bazarov on 07.02.2018.
 */
@Named(value = "bookingIPResourceFrameModel")
@PresentationModel
public class BookingIPResourceFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Текущее требование
	 */
	@Getter
	private ResourceRequirement currentBookingRequirement;


	/**
	 * Сервис работы с бронированиями
	 */
	@Inject
	private IpAddressBookingAppService bookingService;

	/**
	 * Текущий сервис
	 */
	private Service currentService;

	/**
	 * Коллбек для передачи на фрейм интеграции
	 */
	@Getter
	@Setter
	private Consumer<ResourceRequirement> onCreateBookingButtonPressed;

	/**
	 * Подготовка к отоюражению
	 *
	 * @param booking информация о бронируемом ресурсе
	 * @param service услуга
	 */
	public void preRender(Service service, BookingResourceDataHolder booking) {
		if (booking == null || service == null || booking.getRequirement() == null) {
			currentService = null;
			currentBookingRequirement = null;
			return;
		}
		currentBookingRequirement = booking.getRequirement();
		currentService = service;
	}

	/**
	 * Забронировать
	 */
	public void book() {
		if (currentService != null && currentBookingRequirement != null) {
			BookingOrder order = bookingService.book(currentService, currentBookingRequirement);
			onCreateBookingButtonPressed.accept(currentBookingRequirement);
			if (order == null) {
				BookingIPResourceFrameModelMessagesBundle messages = LocaleUtils.getMessages(BookingIPResourceFrameModelMessagesBundle.class);
				FacesContext.getCurrentInstance()
						.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.error(),
								messages.couldNotBookResourceByRule() + currentBookingRequirement.getName()));
			}
		}
	}
}


