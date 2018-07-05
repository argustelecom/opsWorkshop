package ru.argustelecom.box.nri.ports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.EthernetPortType;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;


/**
 * Дто eth порта
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class PortEthernetDto extends PortDto implements Cloneable {

	/**
	 * Мак адресс
	 */
	private String macAddress;

	/**
	 * тип порта
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
	 * @param medium             среда передачи
	 * @param resourceId         ид ресурса к которому принадлежит порт
	 * @param macAddress         мас адресс
	 * @param portType           тип порта ethernet
	 */
	@Builder
	public PortEthernetDto(Long id, Integer portNumber, String portName, AccessTechnology accessTechnology,
						   PortPurpose portPurpose, PortTechnicalCondition technicalCondition, TransmissionMedium medium,
						   Long resourceId, String macAddress, EthernetPortType portType) {
		super(id, PortType.ETHERNET_PORT, portNumber, portName, accessTechnology,
				portPurpose, technicalCondition, medium, resourceId);
		this.macAddress = macAddress;
		this.portType = portType;
	}

	@Override
	public PortEthernetDto clone() {
		PortEthernetDto newPort = PortEthernetDto.builder().macAddress(macAddress).portType(portType).build();
		copyCommonFields(newPort);
		return newPort;
	}
}
