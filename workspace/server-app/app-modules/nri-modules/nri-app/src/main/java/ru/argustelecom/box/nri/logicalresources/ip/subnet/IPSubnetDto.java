package ru.argustelecom.box.nri.logicalresources.ip.subnet;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.nri.logicalresources.ip.address.IPAddressDto;
import ru.argustelecom.box.nri.logicalresources.ip.subnet.model.IPSubnet;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.List;

/**
 * DTO подсети
 * @author a.wisniewski
 * @since 11.12.2017
 */
@Getter
@Setter
@Builder
public class IPSubnetDto extends ConvertibleDto implements Serializable, NamedObject {


	private static final long serialVersionUID = 1L;

	/**
	 * id
	 */
	private Long id;

	/**
	 * Непосредственно сама подсеть
	 */
	private String name;

	/**
	 * Тип подсети
	 */
	private String subnetType;

	/**
	 * Комментарий
	 */
	private String comment;

	/**
	 * Вложенные подсети
	 */
	private List<IPSubnetDto> childSubnets;

	/**
	 * id родительской подсети
	 */
	private Long parentId;

	/**
	 * IP-адреса, находящиеся в подсети
	 */
	private List<IPAddressDto> ipAddresses;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return IPSubnetDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return IPSubnet.class;
	}

	@Override
	public String getObjectName() {
		return name;
	}

}
