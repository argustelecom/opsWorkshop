package ru.argustelecom.box.nri.ports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.PassivePortType;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;

/**
 * Дто для пассивного порта
 */
@EqualsAndHashCode(callSuper = true)
public class PortPassiveDto extends PortDto implements Cloneable {
	/**
	 * Тип порта пассивного оборуодвания
	 */
	@Setter
	@Getter
	private PassivePortType portType;

	/**
	 * конструктор
	 *
	 * @param id                 ид
	 * @param portNumber         номер порта
	 * @param portName           имя порта
	 * @param accessTechnology   технология доступа
	 * @param portPurpose        назначение
	 * @param technicalCondition техническое состояние
	 * @param medium             среда передачи
	 * @param resourceId         ид ресурса к которому принадлежит порт
	 * @param portType           тип порта ethernet
	 */
	@Builder
	public PortPassiveDto(Long id, Integer portNumber, String portName, AccessTechnology accessTechnology,
						  PortPurpose portPurpose, PortTechnicalCondition technicalCondition, TransmissionMedium medium, Long resourceId, PassivePortType portType) {
		super(id, PortType.PASSIVE_PORT, portNumber, portName, accessTechnology,
				portPurpose, technicalCondition, medium, resourceId);
		this.portType = portType;
	}

	@Override
	public PortPassiveDto clone() {
		PortPassiveDto newPort = PortPassiveDto.builder().portType(portType).build();
		copyCommonFields(newPort);
		return newPort;
	}
}
