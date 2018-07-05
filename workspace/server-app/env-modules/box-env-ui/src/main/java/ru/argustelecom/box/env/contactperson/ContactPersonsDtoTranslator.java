package ru.argustelecom.box.env.contactperson;

import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.party.model.role.ContactPersons;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ContactPersonsDtoTranslator {

	@Inject
	private ContactPersonDataDtoTranslator cpdDtoTranslator;

	public ContactPersonsDto translate(ContactPersons contactPersons) {
		return ContactPersonsDto.builder().values(aggregateContactPersonDataDtoList(contactPersons)).build();
	}

	private List<ContactPersonDataDto> aggregateContactPersonDataDtoList(ContactPersons contactPersons) {
		return contactPersons.getPersons().stream().map(contactPerson -> cpdDtoTranslator.translate(contactPerson))
				.collect(Collectors.toList());
	}

}