package ru.argustelecom.box.nri.logicalresources.ip.address;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
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
public class IPAddressDtoTmp extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;


	public static final LogicalResourceType logicalResourceType = LogicalResourceType.IP_ADDRESS;

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

	/**
	 * Конструктор со всеми параметрами
	 *
	 * @param id              ид
	 * @param name            имя
	 * @param isStatic        статический нет
	 * @param state           состояние
	 * @param stateChangeDate дата изменения состояния
	 * @param transferType    ти передачи данных
	 * @param isPrivate       внутренний ли адрес
	 * @param comment         комментарий
	 * @param subnet          подсеть
	 * @param purpose         назначение
	 * @param hasBooking      имеет бронирование
	 */
	@Builder
	public IPAddressDtoTmp(Long id, String name, Boolean isStatic, IPAddressState state, Date stateChangeDate,
						   IpTransferType transferType, Boolean isPrivate, String comment, IPSubnetDto subnet,
						   IPAddressPurpose purpose, Boolean hasBooking) {
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

	/**
	 * Из дто сделат дтотмп
	 *
	 * @param dto
	 */
	public IPAddressDtoTmp(IPAddressDto dto) {
		this.id = dto.getId();
		this.name = dto.getName();
		this.isStatic = dto.getIsStatic();
		this.state = dto.getState();
		this.stateChangeDate = dto.getStateChangeDate();
		this.transferType = dto.getTransferType();
		this.isPrivate = dto.getIsPrivate();
		this.comment = dto.getComment();
		this.subnet = dto.getSubnet();
		this.purpose = dto.getPurpose();
		this.hasBooking = Optional.ofNullable(dto.getHasBooking()).orElse(false);
	}

	/**
	 * Нужно для фрейма интеграции
	 *
	 * @return
	 */
	public LogicalResourceType getLogicalResourceType() {
		return logicalResourceType;
	}

	public IPAddressDto getReal() {
		return IPAddressDto.builder().
				id(this.getId())
				.name(this.getName())
				.isStatic(this.getIsStatic())
				.state(this.getState())
				.stateChangeDate(this.getStateChangeDate())
				.transferType(this.getTransferType())
				.isPrivate(this.getIsPrivate())
				.comment(this.getComment())
				.subnet(this.getSubnet())
				.purpose(this.getPurpose())
				.hasBooking(this.getHasBooking())
				.build();
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return IPAddressDtoTranslatorTmp.class;
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
