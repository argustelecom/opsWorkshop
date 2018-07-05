package ru.argustelecom.box.nri.ports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.OpticSplitterRole;
import ru.argustelecom.box.nri.ports.model.PonConnectorType;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;

/**
 * Дто PortOpticSplitter порта
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class PortOpticSplitterDto extends PortDto implements Cloneable {

	/**
	 * Разъём
	 */
	private PonConnectorType connectorType;

	/**
	 * Роль
	 */
	private OpticSplitterRole role;

	/**
	 * Конструктор со всеми параметрами
	 *
	 * @param id                 ид
	 * @param portNumber         номер порта
	 * @param portName           имя порта
	 * @param accessTechnology   технология доступа
	 * @param portPurpose        назначение
	 * @param technicalCondition техническое состояние
	 * @param medium             среда передачи
	 * @param resourceId         ид ресурса к которому принадлежит порт
	 * @param connectorType      тип соелинения
	 * @param role               роль
	 */
	@Builder
	public PortOpticSplitterDto(Long id, Integer portNumber, String portName, AccessTechnology accessTechnology,
								PortPurpose portPurpose, PortTechnicalCondition technicalCondition, TransmissionMedium medium, Long resourceId, PonConnectorType connectorType, OpticSplitterRole role) {
		super(id, PortType.OPTIC_SPLITTER, portNumber, portName, accessTechnology,
				portPurpose, technicalCondition, medium, resourceId);
		this.connectorType = connectorType;
		this.role = role;
	}

	@Override
	public PortOpticSplitterDto clone() {
		PortOpticSplitterDto newPort = PortOpticSplitterDto.builder().connectorType(connectorType).role(role).build();
		copyCommonFields(newPort);
		return newPort;
	}
}
