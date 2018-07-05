package ru.argustelecom.box.env.person;

import java.io.Serializable;

import javax.inject.Inject;

public abstract class PersonDataFrameModel implements Serializable {

	private static final long serialVersionUID = 1655331190566278182L;

	@Inject
	protected PersonDataAppService personDataAs;

	public void save() {
		//@formatter:off
		personDataAs.renamePerson(
			getPersonDataDto().getPersonId(), 
			getPersonDataDto().getPrefix(),
			getPersonDataDto().getFirstName(),
			getPersonDataDto().getSecondName(),
			getPersonDataDto().getLastName(),
			getPersonDataDto().getSuffix()
		);

		personDataAs.editPersonData(
			getPersonDataDto().getPersonId(),
			getPersonDataDto().getNote()
		);
		//@formatter:on
	}

	public abstract PersonDataDto getPersonDataDto();

}