package ru.argustelecom.box.nri.integration.frames;

import lombok.Getter;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.nri.booking.BookingAppService;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.integration.frames.nls.BookedResourceFrameModelMessagesBundle;
import ru.argustelecom.box.nri.integration.viewmodel.BookingResourceDataHolder;
import ru.argustelecom.box.nri.integration.viewmodel.LogicalResourceDataHolder;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Контроллер фрейма вывдоа информации о забронированных ресурсах
 * Created by s.kolyada on 07.02.2018.
 */
@Named(value = "bookedResourceFrameModel")
@PresentationModel
public class BookedResourceFrameModel implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Текущая бронь
	 */
	@Getter
	private BookingResourceDataHolder currentBooking;

	/**
	 * Ресурсн забронированные услугой
	 */
	@Getter
	private Set<LogicalResourceDataHolder> resources;

	/**
	 * Сервис работы с броянми
	 */
	@Inject
	private BookingAppService bookingAppService;

	/**
	 * Подготовка к отображению
	 *
	 * @param booking бронирование
	 */
	public void preRender(BookingResourceDataHolder booking) {
		if (booking == null || booking.getBookingOrder() == null) {
			reset();
			return;
		}
		this.currentBooking = booking;
		resources = initBookedResourcesIfAny(booking);
	}

	/**
	 * Проинициализировать нагруженные ресурсы, если таковые имеются
	 * @param booking бронь
	 * @return списко нагруженных ресурсов
	 */
	private Set<LogicalResourceDataHolder> initBookedResourcesIfAny(BookingResourceDataHolder booking) {
		BookingOrder order = booking.getBookingOrder();
		Set<LogicalResourceDto> resourceDtos = bookingAppService.loadAllBookedResourceByOrder(order);
		return resourceDtos.stream()
				.map(LogicalResourceDataHolder::convert)
				.collect(Collectors.toSet());
	}

	/**
	 * Сбросить параметры
	 */
	private void reset() {
		currentBooking = null;
		resources = Collections.emptySet();
	}

	/**
	 * Отменить бронирование
	 */
	public void releaseBooking() {
		boolean isReleased = bookingAppService.releaseBooking(currentBooking.getBookingOrder());
		if (isReleased) {
			currentBooking.setBookingOrder(null);
		} else {
			BookedResourceFrameModelMessagesBundle messages = LocaleUtils.getMessages(BookedResourceFrameModelMessagesBundle.class);
			FacesContext.getCurrentInstance()
					.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.error(),
							messages.couldNotCancelBooking()));
		}
	}
}
