package ru.argustelecom.box.nri.ports.model;

import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import ru.argustelecom.box.nri.ports.MacAddressConverter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Порт xDSL
 */
@Entity
@Table(schema = "nri", name = "port_xdsl")
@Access(AccessType.FIELD)
@Getter
@Setter
public class PortXDsl extends Port {

	/**
	 * Тип xDSL порта
	 */
	@Column(name = "port_type")
	@Enumerated(EnumType.STRING)
	private XDslPortType portType = XDslPortType.ADSL;

	/**
	 * МАС адрес
	 */
	@Column(name = "mac_address")
	@Convert(converter = MacAddressConverter.class)
	private MacAddress macAddress;

	/**
	 * Дефолтный конструктор
	 */
	protected PortXDsl() {
		super(PortType.XDSL_PORT, PortPurpose.SUBSCRIBER);
	}

	/**
	 * Конструктор с ид
	 * @param id ид
	 */
	public PortXDsl(Long id){
		super(PortType.XDSL_PORT, PortPurpose.SUBSCRIBER);
		this.id = id;
	}

	/**
	 * Установить мак-адрес
	 */
	public void setMacAddress(String macAddress) {
		if(StringUtils.isNotBlank(macAddress))
			this.macAddress = new MacAddress(macAddress);
		else
			this.macAddress = null;
	}
}
