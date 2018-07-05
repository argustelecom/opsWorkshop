package ru.argustelecom.box.nri.ports;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.ports.model.AccessTechnology;
import ru.argustelecom.box.nri.ports.model.Port;
import ru.argustelecom.box.nri.ports.model.PortPurpose;
import ru.argustelecom.box.nri.ports.model.PortTechnicalCondition;
import ru.argustelecom.box.nri.ports.model.PortType;
import ru.argustelecom.box.nri.ports.model.TransmissionMedium;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;
import java.util.Comparator;

/**
 * ДТО порта
 */
@Getter
@Setter
@EqualsAndHashCode(of = "id", callSuper = false)
public class PortDto extends ConvertibleDto implements Serializable, NamedObject, Cloneable {
	/**
	 * Создает компаратор для упорядочивания портов по типу потом по номеру
	 */
	public static final Comparator<PortDto> COMPARATOR_BY_TYPE_AND_NAME = (o1, o2) -> {
		if (o1.type == null) return -1;
		int value1 = o1.type.getName().compareTo(o2.type.getName());
		if (value1 == 0) {
			return Integer.compare(o1.portNumber, o2.portNumber);
		}
		return value1;
	};

	/**
	 * id
	 */
	private Long id;

	/**
	 * Тип порта
	 */
	private PortType type;

	/**
	 * Номер порта
	 */
	private Integer portNumber;

	/**
	 * Наименование порта
	 */
	private String portName;

	/**
	 * Технологий доступа
	 */
	private AccessTechnology accessTechnology;


	/**
	 * Назначение порта
	 */
	private PortPurpose portPurpose;

	/**
	 * Техническое состояние
	 */
	private PortTechnicalCondition technicalCondition;

	/**
	 * Среда передачи
	 */
	private TransmissionMedium transmissionMedium;
	/**
	 * Ресурс к которому привязан порт
	 */
	private Long resourceId;

	/**
	 * Конструктор
	 *
	 * @param id                 id
	 * @param type               тип порта
	 * @param portNumber         номер порта
	 * @param portName           имя
	 * @param accessTechnology   технология доступа
	 * @param portPurpose        назначение
	 * @param technicalCondition тех.состояние
	 */
	public PortDto(Long id, PortType type, Integer portNumber, String portName, AccessTechnology accessTechnology,
				   PortPurpose portPurpose, PortTechnicalCondition technicalCondition, TransmissionMedium transmissionMedium, Long resourceId) {
		this.id = id;
		this.type = type;
		this.portNumber = portNumber;
		this.portName = portName;
		this.accessTechnology = accessTechnology;
		this.portPurpose = portPurpose;
		this.technicalCondition = technicalCondition;
		this.transmissionMedium = transmissionMedium;
		this.resourceId = resourceId;
	}

	@Override
	public Class<PortDtoTranslator> getTranslatorClass() {
		return PortDtoTranslator.class;
	}

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public Class<Port> getEntityClass() {
		return Port.class;
	}

	@Override
	public String getObjectName() {
		return portName;
	}

	/**
	 * Скопировать общие поля
	 *
	 * @param dest куда копировать
	 */
	public void copyCommonFields(PortDto dest) {
		dest.setType(this.getType());
		dest.setPortNumber(this.getPortNumber());
		dest.setPortName(this.getPortName());
		dest.setAccessTechnology(this.getAccessTechnology());
		dest.setPortPurpose(this.getPortPurpose());
		dest.setTechnicalCondition(this.getTechnicalCondition());
		dest.setTransmissionMedium(this.getTransmissionMedium());
		dest.setResourceId(this.getResourceId());
	}


	@Override
	public PortDto clone() {
		//Делаем клон пабликом что бы при массовом создании не использовать фабрики
		return new PortDto(id, type, portNumber, portName, accessTechnology,
				portPurpose, technicalCondition, transmissionMedium, resourceId);
	}

}


