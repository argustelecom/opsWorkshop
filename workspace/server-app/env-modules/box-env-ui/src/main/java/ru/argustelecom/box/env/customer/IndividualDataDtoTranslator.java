package ru.argustelecom.box.env.customer;

import javax.inject.Inject;

import ru.argustelecom.box.env.contact.ContactDtoTranslator;
import ru.argustelecom.box.env.contact.EmailContactDto;
import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.party.model.role.Individual;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class IndividualDataDtoTranslator {

	@Inject
	private ContactDtoTranslator contactDtoTr;

	public IndividualDataDto translate(Individual individual) {
		Person person = (Person) individual.getParty();
		EmailContactDto mainEmail = individual.getMainEmail() != null
				? (EmailContactDto) contactDtoTr.translate(individual.getMainEmail()) : null;

		//@formatter:off
		return IndividualDataDto.builder()

			// Собственные данные физ. клиента (Individual)
			.individualId(individual.getId())
			.name(individual.getObjectName())
			.typeName(individual.getTypeInstance() != null ? individual.getTypeInstance().getType().getObjectName() : null)
			.vip(individual.isVip())
			.mainEmail(mainEmail)

			// Данные персоны (Person)
			.personId(person.getId())
			.prefix(person.getName().prefix())
			.firstName(person.getName().firstName())
			.secondName(person.getName().secondName())
			.lastName(person.getName().lastName())
			.suffix(person.getName().suffix())
			.note(person.getNote())

		.build();
		//@formatter:on
	}

}