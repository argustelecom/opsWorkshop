package ru.argustelecom.ops.env.person;

import ru.argustelecom.ops.env.party.model.Person;

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
			getPerson().getName().prefix(),
			getPerson().getName().firstName(),
			getPerson().getName().secondName(),
			getPerson().getName().lastName(),
			getPerson().getName().suffix()
		);

		personDataAs.editPersonData(
			getPerson().getId(),
			getPerson().getNote()
		);
		//@formatter:on
	}

	public abstract Person getPerson();

}