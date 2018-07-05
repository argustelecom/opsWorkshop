package ru.argustelecom.box.nri.booking;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType;
import ru.argustelecom.box.nri.resources.requirements.ResourceSchemaDto;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;

/**
 * Created by s.kolyada on 21.12.2017.
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class IpAddressBookingRequirementDto extends BookingRequirementDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Должен ли быть частным
	 */
	private Boolean shouldBePrivate = false;

	/**
	 * Должен быть статическим
	 */
	private Boolean shouldBeStatic = true;

	/**
	 * Метод передачи данных
	 */
	private IpTransferType shouldHaveTransferType = IpTransferType.UNICAST;

	/**
	 * Назначение
	 */
	private IPAddressPurpose shouldHavePurpose = IPAddressPurpose.NOT_SPECIFIED;

	/**
	 * Требование по состоянию адреса в ЖЦ
	 */
	private IPAddressState shouldHaveState = IPAddressState.defaultStatus();

	/**
	 * Должен ли иметь брони
	 */
	private Boolean shouldHaveBooking = false;

	/**
	 * Конструктор
	 *
	 * @param id                идентификатор
	 * @param name              имя
	 * @param shouldBePrivate   дб привтным
	 * @param shouldHaveState   должен иметь состояние
	 * @param shouldHaveBooking должен иметь брони
	 * @param shouldBeStatic    должен бфть статическим
	 * @param shouldHaveTransferType должен иметь Метод передачи данных
	 * @param shouldHaveBooking должен иметь брони
	 * @param shouldHavePurpose должен иметь назначение
	 * @param schema 			схема
	 */
	@Builder
	public IpAddressBookingRequirementDto(Long id, String name, Boolean shouldBePrivate, IPAddressState shouldHaveState,
										  Boolean shouldHaveBooking, Boolean shouldBeStatic, IpTransferType shouldHaveTransferType,
										  IPAddressPurpose shouldHavePurpose, ResourceSchemaDto schema) {
		super(id, name, RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT, schema);
		this.shouldBePrivate = shouldBePrivate;
		this.shouldHaveState = shouldHaveState;
		this.shouldHaveBooking = shouldHaveBooking;
		this.shouldBeStatic = shouldBeStatic;
		this.shouldHaveTransferType = shouldHaveTransferType;
		this.shouldHavePurpose = shouldHavePurpose;
	}

	@Override
	public String getObjectName() {
		return name;
	}
}
