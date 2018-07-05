package ru.argustelecom.box.env.party.model.role;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;

import com.google.common.base.Preconditions;

@Embeddable
@Access(AccessType.FIELD)
public class ContactPersons implements Serializable {

	private static final long serialVersionUID = 8286447398840695929L;

	@OneToMany(targetEntity = ContactPerson.class)
	@JoinColumn(name = "contact_person_id")
	private List<ContactPerson> persons = new ArrayList<>();

	public ContactPersons() {
	}

	public List<ContactPerson> getPersons() {
		return Collections.unmodifiableList(persons);
	}

	public void add(ContactPerson contactPerson) {
		Preconditions.checkNotNull(contactPerson);

		if (!persons.contains(contactPerson))
			persons.add(contactPerson);
	}

	public void remove(ContactPerson contactPerson) {
		Preconditions.checkNotNull(contactPerson);

		if (persons.contains(contactPerson))
			persons.remove(contactPerson);
	}

}