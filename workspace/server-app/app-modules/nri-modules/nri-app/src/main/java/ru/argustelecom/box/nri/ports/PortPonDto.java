package ru.argustelecom.box.nri.ports;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.PonConnectorType;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;

/**
 * Дто Pon порта
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class PortPonDto extends PortDto implements Cloneable {


	/**
	 * Разъём
	 */
	private PonConnectorType connectorType;

	/**
	 * Макс. кол-во абонентов
	 */
	private Integer maxSubscriberNum = 64;

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
	 * @param maxSubscriberNum   максимальное количество подписчиков
	 */
	@Builder
	public PortPonDto(Long id, Integer portNumber, String portName, AccessTechnology accessTechnology,
					  PortPurpose portPurpose, PortTechnicalCondition technicalCondition, TransmissionMedium medium, Long resourceId, PonConnectorType connectorType, Integer maxSubscriberNum) {
		super(id, PortType.PON_PORT, portNumber, portName, accessTechnology,
				portPurpose, technicalCondition, medium, resourceId);
		this.maxSubscriberNum = maxSubscriberNum;
		this.connectorType = connectorType;
	}

	@Override
	public PortPonDto clone() {
		PortPonDto newPort = PortPonDto.builder().maxSubscriberNum(maxSubscriberNum).connectorType(connectorType).build();
		copyCommonFields(newPort);
		return newPort;
	}
}
