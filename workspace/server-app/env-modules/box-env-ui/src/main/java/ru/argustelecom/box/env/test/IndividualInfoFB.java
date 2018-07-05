package ru.argustelecom.box.env.test;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.Person;

public class IndividualInfoFB extends ClientInfoFB {

	private static final long serialVersionUID = 4986577522815550418L;

	@PersistenceContext
	private EntityManager em;

	private Person person;

	@PostConstruct
	public void postConstruct() {
		// FIXME
		person = em.createQuery("from Person p where p.id = 110594", Person.class).getSingleResult();
	}

	public Person getPerson() {
		return person;
	}

	public String getLastName() {
		return person.getName().lastName();
	}

	public String getName() {
		return person.getName().firstName();
	}

	public String getMiddleName() {
		return person.getName().secondName();
	}
}
