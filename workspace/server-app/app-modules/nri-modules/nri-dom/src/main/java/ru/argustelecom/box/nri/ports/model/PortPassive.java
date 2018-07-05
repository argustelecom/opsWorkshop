package ru.argustelecom.box.nri.ports.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;

/**
 * Порт пассивного оборудования
 */
@Entity
@Table(schema = "nri", name = "port_passive")
@Access(AccessType.FIELD)
@Getter
@Setter
public class PortPassive extends Port {

	/**
	 * Тип порта пассивного оборуодвания
	 */
	@Column(name = "port_type")
	@Enumerated(EnumType.STRING)
	private PassivePortType portType;

	/**
	 * Дефолтный конструктор
	 */
	protected PortPassive() {
		super(PortType.PASSIVE_PORT, PortPurpose.SUBSCRIBER);
	}

	/**
	 * Конструктор с ид
	 * @param id
	 */
	public PortPassive(Long id){
		this();
		this.id = id;
	}
}
