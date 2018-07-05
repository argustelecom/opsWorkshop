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
 * Оптический трансивер
 */
@Entity
@Table(schema = "nri", name = "port_optic_transceiver")
@Access(AccessType.FIELD)
@Getter
@Setter
public class PortOpticTransceiver extends Port {

	/**
	 * Серийный номер
	 */
	@Column(name = "serial_num")
	private String serialNum;

	/**
	 * МАС адрес
	 */
	@Column(name = "mac_address")
	@Convert(converter = MacAddressConverter.class)
	private MacAddress macAddress;

	/**
	 * Форм-фактор
	 */
	@Column(name = "form_factor")
	@Enumerated(EnumType.STRING)
	private OpticTransceiverFormFactor formFactor;

	/**
	 * Длина волны
	 */
	@Column(name = "wave_length")
	@Enumerated(EnumType.STRING)
	private OpticTransceiverWaveLength waveLength;

	/**
	 * Дефолтный конструктор
	 */
	protected PortOpticTransceiver() {
		super(PortType.OPTIC_TRANSCEIVER, PortPurpose.TECHNOLOGICAL);
		formFactor = OpticTransceiverFormFactor.SFP;
	}

	/**
	 * Конструктор с ид
	 * @param id ид
	 */
	public PortOpticTransceiver(Long id){
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
}
