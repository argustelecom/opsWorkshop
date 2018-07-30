package ru.argustelecom.box.env.contactperson;

import static com.google.common.base.Preconditions.checkState;

import java.sql.SQLException;
import java.util.Optional;

import javax.inject.Inject;

import com.google.common.base.Strings;

import ru.argustelecom.box.env.contact.Contact;
import ru.argustelecom.box.env.contact.EmailContact;
import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.party.model.role.ContactPerson;
import ru.argustelecom.box.env.person.avatar.PersonAvatarService;
import ru.argustelecom.box.env.person.avatar.model.PersonAvatar;
import ru.argustelecom.box.inf.page.mailto.MailToLink;
import ru.argustelecom.box.inf.page.mailto.MailToLink.Recipient;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ContactPersonDataDtoTranslator {

	@Inject
	private PersonAvatarService personAvatarService;

	public ContactPersonDataDto translate(ContactPerson contactPerson) {
		Person person = (Person) contactPerson.getParty();
		Company company = contactPerson.getCompany();

		checkState(person != null, "%s doesn't have required reference to Person", contactPerson);
		checkState(company != null, "%s doesn't have required reference to Company", contactPerson);

		String email = findFirstEmail(person);
		String mailTo = null;
		if (!Strings.isNullOrEmpty(email)) {
			mailTo = new MailToLink().withRecipient(Recipient.of(email, person.getName().shortName(true))).href();
		}

		String callTo = null;

		//@formatter:off
		ContactPersonDataDto contactPersonDataDto =  ContactPersonDataDto.builder()

			// Собственные данные контактного лица (ContactPerson)
			.contactPersonId(contactPerson.getId())
			.appointment(contactPerson.getAppointment())
			.companyId(company.getId())
			.companyName(company.getObjectName())
			.email(email)
			.mailTo(mailTo)
			.callTo(callTo)

			// Данные персоны (Person)
			.personId(person.getId())
			.prefix(person.getName().prefix())
			.firstName(person.getName().firstName())
			.secondName(person.getName().secondName())
			.lastName(person.getName().lastName())
			.suffix(person.getName().suffix())
		.build();
		//@formatter:on

		PersonAvatar personAvatar = personAvatarService.findAvatar(person);

		try {
			if (personAvatar != null) {
				contactPersonDataDto.setImageInputStream(personAvatar.getImage().getBinaryStream());
				contactPersonDataDto.setImageFormatName(personAvatar.getFormatName());
			}
		} catch (SQLException ignore) {
		}

		return contactPersonDataDto;
	}

	private String findFirstEmail(Person person) {
		Optional<Contact<?>> firstEmail = person.getContactInfo().getContacts().stream()
				.filter(c -> c instanceof EmailContact).findFirst();
		return firstEmail.isPresent() ? firstEmail.get().getObjectName() : null;
	}

}
