package ru.argustelecom.box.env.person.avatar;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.sql.Blob;
import java.sql.SQLException;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.rowset.serial.SerialBlob;

import org.apache.commons.io.IOUtils;

import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.person.avatar.model.PersonAvatar;
import ru.argustelecom.system.inf.exception.SystemException;

@Stateless
public class PersonAvatarService implements Serializable {

	private static final long serialVersionUID = -3554549228772166492L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ImageResizer imageResizer;

	public PersonAvatar findAvatar(Person person) {
		checkArgument(person != null, "person is required");

		return em.find(PersonAvatar.class, person.getId());
	}

	public void addAvatar(Person person, InputStream imageInputStream, String formatName) {
		checkArgument(person != null, "person is required");

		PersonAvatar personAvatar = findAvatar(person);
		if (personAvatar != null) {
			changeImage(personAvatar, imageInputStream, formatName);
		} else {
			createAvatar(person, imageInputStream, formatName);
		}
	}

	public void removeAvatar(Person person) {
		checkArgument(person != null, "person is required");

		PersonAvatar personAvatar = findAvatar(person);
		if (personAvatar != null)
			em.remove(personAvatar);
	}

	private PersonAvatar createAvatar(Person person, InputStream imageInputStream, String formatName) {
		checkArgument(person != null, "person is required");

		PersonAvatar personAvatar = new PersonAvatar(person, createImage(imageInputStream, formatName), formatName);
		em.persist(personAvatar);
		return personAvatar;
	}

	private void changeImage(PersonAvatar personAvatar, InputStream imageInputStream, String formatName) {
		personAvatar.setImage(createImage(imageInputStream, formatName));
		personAvatar.setFormatName(formatName);
	}

	private Blob createImage(InputStream imageInputStream, String formatName) {
		try (InputStream scaledImageInputStream = imageResizer.resize(imageInputStream, formatName, 220, 220)) {
			return new SerialBlob(IOUtils.toByteArray(scaledImageInputStream));
		} catch (SQLException | IOException e) {
			throw new SystemException("Could not create image", e);
		}
	}

}
