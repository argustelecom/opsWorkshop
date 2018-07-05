package ru.argustelecom.box.env.contractperson;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.InputStream;
import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contact.ContactInfo;
import ru.argustelecom.box.env.party.PartyRepository;
import ru.argustelecom.box.env.party.model.Company;
import ru.argustelecom.box.env.party.model.role.ContactPerson;
import ru.argustelecom.box.env.person.PersonDataAppService;
import ru.argustelecom.box.env.person.avatar.PersonAvatarAppService;
import ru.argustelecom.box.inf.security.SecurityContext;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class ContactPersonDataAppService implements Serializable {

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private PersonDataAppService pdAppService;

	@Inject
	private PersonAvatarAppService personAvatarAs;

	@Inject
	private PartyRepository partyRepository;

	@Inject
	private SecurityContext security;

	public void addPersonAvatar(Long personId, InputStream imageInputStream, String imageFormatName) {
		security.checkGranted("CRM_ContactPersonsEdit");
		personAvatarAs.addAvatar(personId, imageInputStream, imageFormatName);
	}

	public void removePersonAvatar(Long personId) {
		security.checkGranted("CRM_ContactPersonsEdit");
		personAvatarAs.remove(personId);
	}

	public void renamePerson(Long personId, String prefix, String firstName, String secondName, String lastName,
			String suffix) {
		security.checkGranted("CRM_ContactPersonsEdit");
		pdAppService.renamePerson(personId, prefix, firstName, secondName, lastName, suffix);
	}

	public void editPersonData(Long personId, String note) {
		security.checkGranted("CRM_ContactPersonsEdit");
		pdAppService.editPersonData(personId, note);
	}

	public ContactPerson createContactPerson(Long companyId, String prefix, String lastName, String firstName,
			String secondName, String suffix, String appointment) {
		checkArgument(companyId != null, "Company is required for Contact Person creation");
		security.checkGranted("CRM_ContactPersonsEdit");

		Company company = em.find(Company.class, companyId);
		// FIXME [Фрейм контактов] Отрефачить после появления фрейма контактов
		return partyRepository.createContactPerson(company, prefix, lastName, firstName, secondName, suffix,
				appointment, new ContactInfo());
	}

	public void removeContactPerson(Long contactPersonId) {
		checkArgument(contactPersonId != null, "Contact Person is required");
		security.checkGranted("CRM_ContactPersonsEdit");

		ContactPerson contactPerson = em.find(ContactPerson.class, contactPersonId);
		partyRepository.removeContactPerson(contactPerson);
	}

	public void changeContactPersonAppointment(Long contactPersonId, String appointment) {
		checkArgument(contactPersonId != null, "Contact Person is required");
		security.checkGranted("CRM_ContactPersonsEdit");

		ContactPerson contactPerson = em.find(ContactPerson.class, contactPersonId);
		contactPerson.changeAppointment(appointment);
	}

	private static final long serialVersionUID = -2462589455285826364L;

}