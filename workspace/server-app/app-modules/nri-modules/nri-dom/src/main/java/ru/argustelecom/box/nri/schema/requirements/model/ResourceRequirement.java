package ru.argustelecom.box.nri.schema.requirements.model;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.nri.schema.model.ResourceSchema;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * Требование к бронированию
 * Created by s.kolyada on 18.12.2017.
 */
@Entity
@Access(AccessType.FIELD)
@Inheritance(strategy = InheritanceType.JOINED)
@Table(schema = "nri", name = "resource_req")
@Getter
@Setter
public class ResourceRequirement extends BusinessObject {

	/**
	 * Тип требования
	 */
	@Transient
	protected RequirementType type;

	private static final long serialVersionUID = 1L;

	@ManyToOne
	@JoinColumn(name = "schema_id")
	protected ResourceSchema bookSchema;

	@Column(name = "name", nullable = false)
	protected String name;

	public ResourceRequirement(RequirementType type) {
		this.type = type;
	}

	protected ResourceRequirement(Long id, RequirementType type) {
		super(id);
		this.type = type;
	}

	@Override
	public String getObjectName() {
		return name;
	}

	public ResourceSchema getSchema() {
		return bookSchema;
	}
}
