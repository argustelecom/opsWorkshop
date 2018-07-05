package ru.argustelecom.box.nri.ports;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.OpticTransceiverFormFactor;
import ru.argustelecom.box.nri.ports.model.OpticTransceiverWaveLength;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;

/**
 * Дто OpticTransceiver порта
 */
@Setter
@Getter
@EqualsAndHashCode(callSuper = true)
public class PortOpticTransceiverDto extends PortDto implements Cloneable {

	/**
	 * Серийный номер
	 */
	private String serialNum;

	/**
	 * МАС адрес
	 */
	private String macAddress;

	/**
	 * Форм-фактор
	 */
	private OpticTransceiverFormFactor formFactor;

	/**
	 * Длина волны
	 */
	private OpticTransceiverWaveLength waveLength;

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
	 * @param macAddress         мас адресс
	 * @param serialNum          Сирийный номер
	 * @param formFactor         Вариант исполнения
	 * @param resourceId         ид ресурса к которому принадлежит порт
	 * @param waveLength         длинна волны
	 */
	@Builder
	public PortOpticTransceiverDto(Long id, Integer portNumber, String portName, AccessTechnology accessTechnology,
								   PortPurpose portPurpose, PortTechnicalCondition technicalCondition, TransmissionMedium medium,
								   String macAddress, String serialNum, OpticTransceiverFormFactor formFactor, Long resourceId, OpticTransceiverWaveLength waveLength) {
		super(id, PortType.OPTIC_TRANSCEIVER, portNumber, portName, accessTechnology,
				portPurpose, technicalCondition, medium, resourceId);
		this.macAddress = macAddress;
		this.formFactor = formFactor;
		this.serialNum = serialNum;
		this.waveLength = waveLength;
	}

	@Override
	public PortOpticTransceiverDto clone() {
		PortOpticTransceiverDto newPort = PortOpticTransceiverDto.builder().macAddress(macAddress).formFactor(formFactor)
				.serialNum(serialNum).waveLength(waveLength).build();
		copyCommonFields(newPort);
		return newPort;
	}
}
