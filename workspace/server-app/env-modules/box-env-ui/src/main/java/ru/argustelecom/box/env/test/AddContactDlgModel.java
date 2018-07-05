package ru.argustelecom.box.env.test;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.contact.Contact;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.contact.ContactTypeRepository;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class AddContactDlgModel implements Serializable {

	private static final long serialVersionUID = -3300320558996583988L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private ContactTypeRepository contactTypeRepository;

	private ContactType contactType;
	private String contactValue;
	private String contactDescription;

	private Party party;

	public void open(Party party) {
		this.party = party;
		RequestContext.getCurrentInstance().execute("PF('addContactDlg').show()");
	}

	/*
	 * public void open() { RequestContext.getCurrentInstance().execute("PF('addContactDlg').show()"); }
	 */

	public void addContact() {
		Contact<?> newContact = party.getContactInfo().createContact(contactType, contactValue,
				idSequence.nextValue(Contact.class));
		em.persist(newContact);
		clean();
	}

	public void clean() {
		contactType = null;
		contactValue = null;
		contactDescription = null;
	}

	public List<ContactType> getContactTypes() {
		return contactTypeRepository.allContactTypes();
	}

	public ContactType getContactType() {
		return contactType;
	}

	public void setContactType(ContactType contactType) {
		this.contactType = contactType;
	}

	public String getContactValue() {
		return contactValue;
	}

	public void setContactValue(String contactValue) {
		this.contactValue = contactValue;
	}

	public String getContactDescription() {
		return contactDescription;
	}

	public void setContactDescription(String contactDescription) {
		this.contactDescription = contactDescription;
	}
}
