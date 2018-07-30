package ru.argustelecom.box.env.person.avatar;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.InputStream;
import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.person.avatar.model.PersonAvatar;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class PersonAvatarAppService implements Serializable {

	private static final long serialVersionUID = 4021079206835744496L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private PersonAvatarService personAvatarService;

	public PersonAvatar findAvatar(Long personId) {
		checkNotNull(personId, "personId is required");

		Person person = em.getReference(Person.class, personId);
		return personAvatarService.findAvatar(person);
	}

	public void addAvatar(Long personId, InputStream imageInputStream, String formatName) {
		checkNotNull(personId, "personId is required");
		checkNotNull(imageInputStream, "imageInputStream is required");
		checkNotNull(personId, "formatName is required");

		Person person = em.getReference(Person.class, personId);
		personAvatarService.addAvatar(person, imageInputStream, formatName);
	}

	public void remove(Long personId) {
		checkNotNull(personId, "personId is required");

		Person person = em.getReference(Person.class, personId);
		personAvatarService.removeAvatar(person);
	}

}
