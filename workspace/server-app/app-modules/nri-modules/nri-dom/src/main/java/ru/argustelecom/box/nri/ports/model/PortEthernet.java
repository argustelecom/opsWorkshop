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
 * Порт Ethernet
 */
@Entity
@Table(schema = "nri", name = "port_ethernet")
@Access(AccessType.FIELD)
@Getter
@Setter
public class PortEthernet extends Port {

	/**
	 * Тип Ethernet порта
	 */
	@Column(name = "port_type", nullable = false)
	@Enumerated(EnumType.STRING)
	private EthernetPortType portType;

	/**
	 * МАС адрес
	 */
	@Column(name = "mac_address")
	@Convert(converter = MacAddressConverter.class)
	private MacAddress macAddress;

	/**
	 * Дефолтный конструктор
	 */
	public PortEthernet() {
		super(PortType.ETHERNET_PORT, PortPurpose.SUBSCRIBER);
		this.portType = EthernetPortType.PORT_TYPE_100FE;
	}

	/**
	 * Конструктор с ид
	 * @param id ид
	 */
	public PortEthernet(Long id){
		super(PortType.ETHERNET_PORT, PortPurpose.SUBSCRIBER);
		this.portType = EthernetPortType.PORT_TYPE_100FE;
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
