package ru.argustelecom.box.nri.ports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;
import ru.argustelecom.box.nri.ports.model.XDslPortType;

/**
 * Дто PortXDsl порта
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class PortXDslDto extends PortDto implements Cloneable {

	/**
	 * МАС адрес
	 */
	private String macAddress;

	/**
	 * Тип xDSL порта
	 */
	private XDslPortType portType;

	/**
	 * Конструктор
	 *
	 * @param id                 ид
	 * @param portNumber         номер порта
	 * @param portName           имя порта
	 * @param accessTechnology   технология доступа
	 * @param portPurpose        назначение
	 * @param technicalCondition техническое состояние
	 * @param transmissionMedium среда передачи
	 * @param macAddress         мас адресс
	 * @param portType           тип порта XDsl
	 */
	@Builder
	public PortXDslDto(Long id, Integer portNumber, String portName, AccessTechnology accessTechnology,
					   PortPurpose portPurpose, PortTechnicalCondition technicalCondition, TransmissionMedium transmissionMedium, Long resourceId, String macAddress, XDslPortType portType) {
		super(id, PortType.XDSL_PORT, portNumber, portName, accessTechnology,
				portPurpose, technicalCondition, transmissionMedium, resourceId);
		this.macAddress = macAddress;
		this.portType = portType;
	}

	@Override
	public PortXDslDto clone() {
		PortXDslDto newPort = PortXDslDto.builder().macAddress(macAddress).portType(portType).build();
		copyCommonFields(newPort);
		return newPort;
	}
}
