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
 * Комбо-порт
 */
@Entity
@Table(schema = "nri", name = "port_combo")
@Access(AccessType.FIELD)
@Getter
@Setter
public class PortCombo extends Port {

	/**
	 * Тип Ethernet порта поумолчанию
	 */
	private static final EthernetPortType DEFAULT_ETH_PORT_TYPE = EthernetPortType.PORT_TYPE_1GE;

	/**
	 * Как используется данный порт
	 */
	@Column(name = "usage_type")
	@Enumerated(EnumType.STRING)
	private ComboPortUsageType usageType;

	/**
	 * МАС адрес
	 */
	@Column(name = "mac_address")
	@Convert(converter = MacAddressConverter.class)
	private MacAddress macAddress;

	/**
	 * Тип Ethernet порта
	 */
	@Column(name = "port_type")
	@Enumerated(EnumType.STRING)
	private EthernetPortType portType;

	/**
	 * Дефолтный конструктор
	 */
	protected PortCombo() {
		super(PortType.COMBO_PORT, PortPurpose.TECHNOLOGICAL);
	}

	/**
	 * Конструктор с ид
	 * @param id
	 */
	public PortCombo(Long id){
		this();
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

	public void setUsageType(ComboPortUsageType usageType) {
		this.usageType = usageType;
		// Если используемый порт не Ethernet, то нужно удалить используемый тип
		if (!ComboPortUsageType.ETHERNET_PORT.equals(usageType)) {
			portType = null;
			return;
		}
		portType = DEFAULT_ETH_PORT_TYPE;
	}

	public void setPortType(EthernetPortType portType) {
		// тип порта можно устанавливать только в случае если используемый тип порта ethernet
		if (!ComboPortUsageType.ETHERNET_PORT.equals(usageType)) {
			return;
		}
		this.portType = portType;
	}
}
