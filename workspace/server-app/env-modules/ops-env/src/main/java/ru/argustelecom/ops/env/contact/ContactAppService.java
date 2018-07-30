package ru.argustelecom.ops.env.contact;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.base.Objects;

import ru.argustelecom.ops.env.idsequence.IdSequenceService;
import ru.argustelecom.ops.env.party.model.Party;
import ru.argustelecom.ops.env.stl.EmailAddress;
import ru.argustelecom.ops.inf.service.ApplicationService;

@ApplicationService
public class ContactAppService implements Serializable {

	private static final long serialVersionUID = 9214265328440788994L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	public Contact<?> addNewContact(Long partyId, Long contactTypeId, Object value, String comment) {
		checkNotNull(partyId, "partyId is required");
		checkNotNull(contactTypeId, "contactTypeId is required");
		checkNotNull(value, "value is required");
		checkState(value instanceof String);

		Party party = em.find(Party.class, partyId);
		ContactType contactType = em.find(ContactType.class, contactTypeId);

		Class<?> contactValueClass = contactType.getCategory().contactValueClass();
		Contact<?> contact = party.getContactInfo().createContact(contactType,
				convertContactValue(contactValueClass, (String) value), idSequence.nextValue(Contact.class));
		if (comment != null) {
			contact.setComment(comment);
		}

		return contact;
	}

	public void editContact(Long contactId, ContactType type, Object value, String comment) {
		checkNotNull(contactId, "contactId is required");
		checkNotNull(type, "type is required");
		checkNotNull(value, "value is required");
		checkState(value instanceof String);

		Contact<?> contact = em.find(Contact.class, contactId);
		checkArgument(type.getCategory().equals(contact.getType().getCategory()), "");
		if (!Objects.equal(value, contact.getValue())) {
			Class<?> contactValueClass = contact.getType().getCategory().contactValueClass();
			contact.setValue(convertContactValue(contactValueClass, (String) value));
		}
		if (!Objects.equal(comment, contact.getComment())) {
			contact.setComment(comment);
		}
	}

	public void removeContact(Long partyId, Long contactId) {
		checkNotNull(partyId, "partyId is required");
		checkNotNull(contactId, "contactId is required");

		Party party = em.find(Party.class, partyId);
		Contact<?> contact = em.find(Contact.class, contactId);
		party.getContactInfo().remove(contact);
	}

	@SuppressWarnings("unchecked")
	private <T, V> V convertContactValue(Class<T> contactValueClass, String value) {
		if (contactValueClass.equals(EmailAddress.class)) {
			return (V) EmailAddress.create(value);
		}

		return (V) value;
	}

}