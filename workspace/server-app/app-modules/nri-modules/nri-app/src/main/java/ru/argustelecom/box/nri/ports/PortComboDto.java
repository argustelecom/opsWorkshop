package ru.argustelecom.box.nri.ports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.ComboPortUsageType;
import ru.argustelecom.box.nri.ports.model.EthernetPortType;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;

/**
 * Дто Combo порта
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class PortComboDto extends PortDto implements Cloneable {
	/**
	 * Как используется данный порт
	 */
	private ComboPortUsageType usageType;

	/**
	 * МАС адрес
	 */
	private String macAddress;

	/**
	 * Тип Ethernet порта
	 */
	private EthernetPortType portType;

	/**
	 * Конструктор
	 *
	 * @param id                 ид
	 * @param portNumber         номер порта
	 * @param portName           имя порта
	 * @param accessTechnology   технология доступа
	 * @param portPurpose        назначение
	 * @param technicalCondition техническое состояние
	 * @param macAddress         мас адресс
	 * @param transmissionMedium среда передачи
	 * @param resourceId         ид ресурса к которому принадлежит порт
	 * @param portType           тип порта ethernet
	 * @param usageType          тип использования
	 */
	@Builder
	public PortComboDto(Long id, Integer portNumber, String portName, AccessTechnology accessTechnology,
						PortPurpose portPurpose, PortTechnicalCondition technicalCondition, String macAddress, TransmissionMedium transmissionMedium, Long resourceId, EthernetPortType portType, ComboPortUsageType usageType) {
		super(id, PortType.COMBO_PORT, portNumber, portName, accessTechnology,
				portPurpose, technicalCondition, transmissionMedium, resourceId);
		this.macAddress = macAddress;
		this.portType = portType;
		this.usageType = usageType;
	}

	@Override
	public PortComboDto clone() {
		PortComboDto newPort = PortComboDto.builder().macAddress(macAddress).portType(portType)
				.usageType(usageType).build();
		copyCommonFields(newPort);
		return newPort;
	}
}
