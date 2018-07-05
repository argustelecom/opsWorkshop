package ru.argustelecom.box.nri.ports.model;

import com.google.common.base.Verify;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.nri.resources.model.ResourceInstance;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Общее представление Порт
 */
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
@Table(schema = "nri", name = "port")
@Access(AccessType.FIELD)
@Getter
@Setter
public class Port extends BusinessObject implements Serializable, NamedObject {

	/**
	 * Тип порта
	 */
	@Setter(AccessLevel.NONE)
	@Transient
	private PortType type;

	/**
	 * Номер порта
	 */
	@Column(name = "port_num", nullable = false)
	private Integer portNumber;

	/**
	 * Наименование порта
	 */
	@Column(name = "port_name")
	private String portName;

	/**
	 * Технологий доступа
	 */
	@Column(name = "technology", nullable = false)
	@Enumerated(EnumType.STRING)
	private AccessTechnology accessTechnology;

	/**
	 * Назначение порта
	 */
	@Column(name = "purpose", nullable = false)
	@Enumerated(EnumType.STRING)
	private PortPurpose portPurpose;

	/**
	 * Среда передачи
	 */
	@Column(name = "medium", nullable = false)
	@Enumerated(EnumType.STRING)
	private TransmissionMedium transmissionMedium;


	/**
	 * Техническое состояние
	 */
	@Column(name = "condition", nullable = false)
	@Enumerated(EnumType.STRING)
	private PortTechnicalCondition technicalCondition;
	/**
	 * Родительский ресурс, в которо находится данный порт
	 */
	@ManyToOne
	@JoinColumn(name = "resource_id", referencedColumnName="id", nullable = false)
	private ResourceInstance resource;



	/**
	 * Дефолтный конструктор
	 */
	protected Port() {
		technicalCondition = PortTechnicalCondition.IN_ORDER;
	}

	/**
	 * Конструктор с состоянием поумолчанию
	 * @param portType тип порта
	 * @param portPurpose назначение порта
	 */
	protected Port(PortType portType, PortPurpose portPurpose) {
		this();
		Verify.verifyNotNull(portType);
		Verify.verifyNotNull(portPurpose);
		this.type = portType;
		this.portPurpose = portPurpose;
		// если данный тип порта поддерживает всего 1 технологию доступа, то сразу её выставляем
		if (portType.getSupportedAccessTechnologies().size() == 1) {
			this.accessTechnology = portType.getSupportedAccessTechnologies().get(0);
		}
	}

	/**
	 * Устанавливает тип порта, при этом валидирует состояние порта относительного ограничений нового типа
	 * @param type тип порта
	 */
	public void setType(PortType type) {
		this.type = type;
		// если данный тип порта поддерживает всего 1 технологию доступа, то сразу её выставляем
		if (type.getSupportedAccessTechnologies().size() == 1) {
			this.accessTechnology = type.getSupportedAccessTechnologies().get(0);
			return;
		}

		// при изменении типа порта, если уже выставлена технология доступа и эта технология доступа не поддерживается
		// данным портом, то сбрасываем её
		if (accessTechnology != null && !type.getSupportedAccessTechnologies().contains(accessTechnology)) {
			accessTechnology = null;
		}
	}
}
