package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.lifecycle.IPAddressState;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddress;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IPAddressPurpose;
import ru.argustelecom.box.nri.logicalresources.ip.address.model.IpTransferType;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.IPSubnetDto;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.Date;
import java.util.Optional;

/**
 * DTO логического ресурса IP-адрес
 *
 * @author d.khekk
 * @since 11.12.2017
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class IPAddressDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	private LogicalResourceType logicalResourceType = LogicalResourceType.IP_ADDRESS;

	/**
	 * Идентификатор
	 */
	private Long id;

	/**
	 * Имя IP-адреса
	 */
	@Setter
	private String name;

	/**
	 * Является ли статическим
	 */
	@Setter
	private Boolean isStatic;

	/**
	 * Стаус IP-адреса
	 */
	@Setter
	private IPAddressState state = IPAddressState.defaultStatus();

	/**
	 * Дата последнего изменения статуса
	 */
	@Setter
	private Date stateChangeDate;

	/**
	 * Тип передачи данных IP-адреса
	 */
	private IpTransferType transferType;

	/**
	 * Внутренний ли адрес
	 */
	private Boolean isPrivate;

	/**
	 * Комментарий
	 */
	@Setter
	private String comment;

	/**
	 * Подсеть
	 */
	@Setter
	private IPSubnetDto subnet;

	/**
	 * Назначение
	 */
	@Setter
	private IPAddressPurpose purpose;

	/**
	 * Признак наличия брони на адрес
	 */
	private Boolean hasBooking = false;

	@Builder
	public IPAddressDto(Long id, String name, Boolean isStatic,
						IPAddressState state, Date stateChangeDate, IpTransferType transferType, Boolean isPrivate,
						String comment, IPSubnetDto subnet, IPAddressPurpose purpose, Boolean hasBooking) {
		this.logicalResourceType = LogicalResourceType.IP_ADDRESS;
		this.id = id;
		this.name = name;
		this.isStatic = isStatic;
		this.state = state;
		this.stateChangeDate = stateChangeDate;
		this.transferType = transferType;
		this.isPrivate = isPrivate;
		this.comment = comment;
		this.subnet = subnet;
		this.purpose = purpose;
		this.hasBooking = Optional.ofNullable(hasBooking).orElse(false);
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return IPAddressDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return IPAddress.class;
	}

	@Override
	public String getObjectName() {
		return name;
	}
}
