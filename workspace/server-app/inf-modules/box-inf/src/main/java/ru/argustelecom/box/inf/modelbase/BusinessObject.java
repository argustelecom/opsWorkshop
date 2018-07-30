package ru.argustelecom.box.inf.modelbase;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Предок всех сущностей.
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@SequenceDefinition
public class BusinessObject extends ru.argustelecom.system.inf.modelbase.SuperClass {

	protected BusinessObject() {
		super();
	}

	public BusinessObject(Long id) {
		super(id, ENT_SUPER_CLASS);
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

	private static final long serialVersionUID = -5902286098973295934L;

}