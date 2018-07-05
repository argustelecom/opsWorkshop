package ru.argustelecom.box.nri.schema.requirements.phone.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;

import javax.persistence.Entity;
import javax.persistence.Table;

/**
 * Требование к бронированию телефонного номера
 * Created by b.bazarov on 01.02.2018.
 */
@Entity
@Table(schema = "nri", name = "booking_req_phone_number")
@Getter
@Setter
public class PhoneNumberBookingRequirement extends ResourceRequirement {


	private static final long serialVersionUID = 6128852309403740361L;

	protected PhoneNumberBookingRequirement(){
		super(RequirementType.PHONE_NUMBER_BOOKING_REQUIREMENT);
	}

	protected PhoneNumberBookingRequirement(Long id) {
		super(id, RequirementType.PHONE_NUMBER_BOOKING_REQUIREMENT);
	}

	/**
	 * Конструктор
	 *
	 * @param id     идентификатор
	 * @param name   имя
	 * @param schema схема подключения
	 */
	@Builder
	public PhoneNumberBookingRequirement(Long id, String name, ResourceSchema schema) {
		super(id, RequirementType.PHONE_NUMBER_BOOKING_REQUIREMENT);
		this.name = name;
		this.bookSchema = schema;
	}
}
