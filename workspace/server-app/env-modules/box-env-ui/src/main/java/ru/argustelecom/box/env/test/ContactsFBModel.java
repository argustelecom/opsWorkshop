package ru.argustelecom.box.env.test;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contact.Contact;
import ru.argustelecom.box.env.contact.ContactCategory;
import ru.argustelecom.box.env.contact.ContactInfo;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContactsFBModel implements Serializable {

	private static final long serialVersionUID = 2526574008494659874L;

	@PersistenceContext
	private EntityManager em;

	private List<ContactCard> contactCards;

	@PostConstruct
	public void postConstruct() {
		List<Person> persons = em.createQuery("from Person", Person.class).getResultList();
		contactCards = persons.stream().map(p -> new ContactCard(p)).filter(cc -> !(cc.getContacts().isEmpty())).collect(Collectors.toList());
	}

	public List<ContactCard> getContactCards() {
		return contactCards;
	}

	public void removeContactCard() {

	}

	public String getContactIconClass(Contact<?> contact) {
		String contactIconClass = "";
		ContactCategory cc = contact.getType().getCategory();
		switch (cc) {
		case EMAIL:
			contactIconClass = "fa-envelope";
			break;
		case PHONE:
			contactIconClass = "fa-phone";
			break;
		case CUSTOM:
			contactIconClass = "";
			break;
		}

		return contactIconClass;
	}

	public List<ContactType> getPosibleContactTypes(ContactCategory category) {
		return em.createQuery("from ContactType", ContactType.class).getResultList().stream()
				.filter(ct -> ct.getCategory().equals(category)).collect(Collectors.toList());
	}

	public static class ContactCard {
		private Party party;
		private ContactInfo contactInfo;

		public ContactCard(Party party) {
			this.party = party;
			this.contactInfo = party.getContactInfo();
		}

		public long getId() {
			return party.getId();
		}

		public String getContactName() {
			return party.getObjectName();
		}
		
		public void setContactName(String name) {}

		public List<Contact<?>> getContacts() {
			return contactInfo.getContacts();
		}

		public void removeContact(Contact<?> contact) {
			contactInfo.remove(contact);
		}

		public Party getParty() {
			return party;
		}
	}
}
