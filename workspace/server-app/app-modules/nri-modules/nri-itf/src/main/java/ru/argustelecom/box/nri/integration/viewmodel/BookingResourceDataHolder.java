package ru.argustelecom.box.nri.integration.viewmodel;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.collections.CollectionUtils;
import ru.argustelecom.box.nri.booking.model.BookingOrder;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import java.io.Serializable;
import java.util.Objects;

/**
 * Холдер информации о бронируемом ресурсе
 * Created by s.kolyada on 05.02.2018.
 */
@Getter
public class BookingResourceDataHolder implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * Имя требования
	 */
	private String name;

	/**
	 * Бронирование
	 */
	@Setter
	private BookingOrder bookingOrder;

	/**
	 * Требование
	 */
	private ResourceRequirement requirement;

	/**
	 * Конструктор
	 *
	 * @param bookingOrder
	 * @param requirement
	 */
	@Builder
	public BookingResourceDataHolder(BookingOrder bookingOrder, ResourceRequirement requirement) {
		this.name = requirement == null ? "" : requirement.getObjectName();
		if (Objects.nonNull(bookingOrder)) {
			this.bookingOrder = bookingOrder;
			if (CollectionUtils.isNotEmpty(bookingOrder.getBookedLogicalResource())
					&& bookingOrder.getBookedLogicalResource().size() < 2) {
				name += " - " + bookingOrder.getBookedLogicalResource().iterator().next().getObjectName();
			}
		}
		this.requirement = requirement;
	}

	/**
	 * Признак того, что ресурсы по данному требованию забронированы
	 *
	 * @return истина если забронировано, иначе ложь
	 */
	public Boolean isBooked() {
		return Objects.nonNull(bookingOrder);
	}

	/**
	 * Возвращает составное имя требование плюс если есть бронь
	 *
	 * @return имя
	 */
	public String getName() {
		String result = requirement.getObjectName();
		if (Objects.nonNull(bookingOrder) && CollectionUtils.isNotEmpty(bookingOrder.getBookedLogicalResource())
				&& bookingOrder.getBookedLogicalResource().size() < 2) {
			result += " - " + bookingOrder.getBookedLogicalResource().iterator().next().getObjectName();

		}
		return result;
	}
}