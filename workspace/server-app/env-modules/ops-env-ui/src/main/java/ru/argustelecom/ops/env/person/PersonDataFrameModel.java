package ru.argustelecom.ops.env.person;

import java.io.Serializable;

import javax.inject.Inject;

public abstract class PersonDataFrameModel implements Serializable {

	private static final long serialVersionUID = 1655331190566278182L;

	@Inject
	protected PersonDataAppService personDataAs;

	public void save() {
		//@formatter:off
		personDataAs.renamePerson(
			getPerson().getId(),
			getPerson().getPersonName().prefix(),
			getPerson().getPersonName().firstName(),
			getPerson().getPersonName().secondName(),
			getPerson().getPersonName().lastName(),
			getPerson().getPersonName().suffix()
		);

		personDataAs.editPersonData(
			getPerson().getId(),
			getPerson().getNote()
		);
		//@formatter:on
	}

	public abstract Person getPerson();

}