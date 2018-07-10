package ru.argustelecom.box.env.person;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.party.model.PersonName;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class PersonDataAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	public void renamePerson(Long personId, String prefix, String firstName, String secondName, String lastName,
			String suffix) {
		checkArgument(personId != null, "personId is required");

		Person person = em.find(Person.class, personId);
		PersonName personName = PersonName.of(prefix, firstName, secondName, lastName, suffix);
		person.rename(personName);
	}

	public void editPersonData(Long personId, String note) {
		checkArgument(personId != null, "personId is required");

		Person person = em.find(Person.class, personId);
	}

	private static final long serialVersionUID = -6184398717699947398L;
}