package ru.argustelecom.box.nri.booking;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.phone.lifecycle.PhoneNumberState;
import ru.argustelecom.box.nri.logicalresources.phone.model.PhoneNumber;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import javax.persistence.criteria.Predicate;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Дто для требований к телефонному номеру
 * Created by b.bazarov on 01.02.2017.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class PhoneNumberBookingRequirementDto extends BookingRequirementDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Маска телефонного номера
	 */
	@Setter
	private String pnMask;

	/**
	 * Требуемый статус
	 */
	@Setter
	private PhoneNumberState state;

	/**
	 * Может ли иметь бронирования
	 */
	@Setter
	private Boolean canHaveBookings;

	/**
	 * Конструктор
	 *
	 * @param id     			идентификатор
	 * @param name   			имя
	 * @param schema 			схема
	 * @param pnMask 			Маска телефонного номера
	 * @param canHaveBookings  	Может ли иметь бронирования
	 * @param state 			Требуемый статус
	 */
	@Builder
	public PhoneNumberBookingRequirementDto(Long id, String name,
											ResourceSchemaDto schema, String pnMask, PhoneNumberState state,
											Boolean canHaveBookings) {
		super(id, name, RequirementType.PHONE_NUMBER_BOOKING_REQUIREMENT, schema);
		this.pnMask = pnMask;
		this.state = state;
		this.canHaveBookings = canHaveBookings;
	}

	@Override
	public String getObjectName() {
		return name;
	}

	/**
	 * Сгенерировать предикаты для поиска
	 * TODO перенести в сущность, когда она будет расширяться
	 * @param query запрос
	 * @return список предикатов, составленный на основании состояния требования
	 */
	public List<Predicate> createPredicates(PhoneNumber.PhoneNumberQuery query) {
		List<Predicate> result = new ArrayList<>();

		result.add(query.state().equal(state));
		result.add(query.digits().contains(pnMask));
		if (canHaveBookings) {
			result.add(query.bookingOrder().isNotNull());
		} else {
			result.add(query.bookingOrder().isNull());
		}

		return result;
	}
}
