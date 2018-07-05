package ru.argustelecom.box.nri.schema.requirements.ip.model;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType;
import ru.argustelecom.box.nri.schema.requirements.model.RequirementType;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;
import ru.argustelecom.box.nri.schema.requirements.model.ResourceRequirement;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Требование к бронированию Ip-адреса
 * Created by s.kolyada on 18.12.2017.
 */
@Entity
@Table(schema = "nri", name = "booking_req_ip_addr")
@Getter
@Setter
public class IpAddressBookingRequirement extends ResourceRequirement {

	/**
	 * Требование по типу адреса
	 */
	@Column(name = "should_be_private")
	private Boolean shouldBePrivate = false;

	/**
	 * Требование по виду адреса
	 */
	@Column(name = "should_be_static")
	private Boolean shouldBeStatic = true;

	/**
	 * Требование по состоянию адреса в ЖЦ
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "should_have_state")
	private IPAddressState shouldHaveState = IPAddressState.defaultStatus();

	@Column(name = "should_have_booking")
	private Boolean shouldHaveBooking = false;

	/**
	 * Требование по методу передачи данных
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "should_have_transfer_type")
	private IpTransferType shouldHaveTransferType;


	/**
	 * Требование к назначению
	 */
	@Enumerated(EnumType.ORDINAL)
	@Column(name = "should_have_purpose")
	private IPAddressPurpose shouldHavePurpose;


	protected IpAddressBookingRequirement() {
		super(RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT);
	}

	protected IpAddressBookingRequirement(Long id) {
		super(id, RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT);
	}

	/**
	 * Конструктор со всеми параметрами
	 * @param id
	 * @param name
	 * @param shouldBePrivate
	 * @param shouldHaveState
	 * @param shouldHaveBooking
	 * @param shouldBeStatic
	 * @param shouldHaveTransferType
	 * @param shouldHavePurpose
	 * @param schema
	 */
	@Builder
	public IpAddressBookingRequirement(Long id, String name, Boolean shouldBePrivate, IPAddressState shouldHaveState,
									   Boolean shouldHaveBooking, Boolean shouldBeStatic, IpTransferType shouldHaveTransferType,
									   IPAddressPurpose shouldHavePurpose, ResourceSchema schema) {
		super(id, RequirementType.IP_ADDRESS_BOOKING_REQUIREMENT);
		this.name = name;
		this.bookSchema = schema;
		this.shouldBePrivate = shouldBePrivate;
		this.shouldHaveState = shouldHaveState;
		this.shouldHaveBooking = shouldHaveBooking;
		this.shouldBeStatic = shouldBeStatic;
		this.shouldHaveTransferType = shouldHaveTransferType;
		this.shouldHavePurpose = shouldHavePurpose;
	}

	/**
	 * Создать предикаты
	 * @param query
	 * @return
	 */
	public List<Predicate> createPredicates(IPAddress.IPAddressQuery query) {
		List<Predicate> result = new ArrayList<>();

		result.add(query.isPrivate().equal(shouldBePrivate));
		result.add(query.isStatic().equal(shouldBeStatic));
		result.add(query.state().equal(shouldHaveState));
		result.add(query.transferType().equal(shouldHaveTransferType));
		result.add(query.purpose().equal(shouldHavePurpose));
		if (shouldHaveBooking) {
			result.add(query.bookingOrder().isNotNull());
		} else {
			result.add(query.bookingOrder().isNull());
		}

		return result;
	}
}
