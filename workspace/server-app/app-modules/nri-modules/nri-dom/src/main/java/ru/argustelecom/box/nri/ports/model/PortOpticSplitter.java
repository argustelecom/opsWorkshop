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
 * Оптический сплиттер
 */
@Entity
@Table(schema = "nri", name = "port_optic_splitter")
@Access(AccessType.FIELD)
@Getter
@Setter
public class PortOpticSplitter extends Port {

	/**
	 * Разъём
	 */
	@Column(name = "connector_type")
	@Enumerated(EnumType.STRING)
	private PonConnectorType connectorType;

	/**
	 * Роль
	 */
	@Column(name = "role")
	@Enumerated(EnumType.STRING)
	private OpticSplitterRole role;

	/**
	 * Дефолтный конструктор
	 */
	protected PortOpticSplitter() {
		super(PortType.OPTIC_SPLITTER, PortPurpose.SUBSCRIBER);
		role = OpticSplitterRole.OUTCOMING;
	}

	/**
	 * конструктор
	 * @param id
	 */
	public PortOpticSplitter(Long id) {
		super(PortType.OPTIC_SPLITTER, PortPurpose.SUBSCRIBER);
		this.id = id;
		role = OpticSplitterRole.OUTCOMING;
	}
}
