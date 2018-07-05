package ru.argustelecom.box.env.party.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import ru.argustelecom.box.inf.modelbase.BusinessDirectory;

/**
 * Класс описывающий должность, которую может занимать {@linkplain ru.argustelecom.box.env.party.model.role.Employee
 * работник} . Должности не имеют иерархии, так как было принято решение, что в ней нет необходимости.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", uniqueConstraints = { @UniqueConstraint(name = "unq_position", columnNames = { "name" }) })
public class Appointment extends BusinessDirectory {

	private static final long serialVersionUID = -8137002441316325996L;

	@Column(length = 128, nullable = false)
	private String name;

	protected Appointment() {
	}

	public Appointment(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return name;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	/**
	 * @return Название должности.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}