package ru.argustelecom.ops.inf.modelbase;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import ru.argustelecom.system.inf.modelbase.Directory;

/**
 * Общий предок для всех бизнес-справочников
 *
 * @author a.frolov
 */
@MappedSuperclass
@Access(AccessType.FIELD)
@SequenceDefinition(name = "system.gen_directory_id")
public class BusinessDirectory extends Directory {

	private static final long serialVersionUID = -3952138933316242688L;

	protected BusinessDirectory() {
		super();
	}

	public BusinessDirectory(Long id) {
		super(id);
	}

	@Id
	@Override
	@Access(AccessType.PROPERTY)
	public Long getId() {
		return super.getId();
	}

}
