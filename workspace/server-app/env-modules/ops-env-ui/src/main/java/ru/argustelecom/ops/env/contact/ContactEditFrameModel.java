package ru.argustelecom.ops.env.contact;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.ops.env.party.model.Party;
import ru.argustelecom.system.inf.dataaccess.cache.DirectoryCacheService;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "contactEditFM")
@PresentationModel
public class ContactEditFrameModel implements Serializable {

	private static final long serialVersionUID = 7536314552668009446L;

	@Inject
	private ContactAppService contactAs;

	@Inject
	private DirectoryCacheService directoryCacheService;

	@Getter
	@Setter
	private Party party;

	@Getter
	private List<Contact> contacts = new ArrayList<>();

	@Getter
	private List<Contact> removedContacts = new ArrayList<>();

	public void preRender(Party party) {
		clearContacts();
		if (party != null) {
			this.party = party;
			contacts.addAll(party.getContactInfo().getContacts());
		}
		addEmptyContactsIfNeed();
		contacts.sort(new ByCategoryComparator());
	}

	public List<ContactType> getContactTypes(Contact contact) {
		return directoryCacheService.getDirectoryObjects(ContactType.class).stream()
				.filter(contactType -> contactType.getCategory() == contact.getType().getCategory())
				.collect(Collectors.toList());
	}

	public void createContact(Contact template) {
		contacts.add(createEmptyContactBy(template.getType().getCategory()));
		contacts.sort(new ByCategoryComparator());
	}

	public void removeContact(Contact contactToRemove) {
		if (contacts.stream().noneMatch(contact -> contact.getType().getCategory() == contact.getType().getCategory()
				&& !contact.equals(contactToRemove))) {
			contactToRemove.setType(null);
			contactToRemove.setValue(null);
			contactToRemove.setComment(null);
		} else {
			contacts.remove(contactToRemove);
			if (!isNewContact(contactToRemove)) {
				removedContacts.add(contactToRemove);
			}
		}
	}

	public void submit() {
		contacts.forEach(contact -> {
			if (isNewContact(contact)) {
				if (contact.getType() != null && contact.getValue() != null) {
					contactAs.addNewContact(party.getId(), contact.getType().getId(), contact.getValue(),
							contact.getComment());
				}
			} else {
				if (contact.getType() == null && contact.getValue() == null) {
					contactAs.removeContact(party.getId(), contact.getId());
				} else {
					contactAs.editContact(contact.getId(), contact.getType(), contact.getValue(), contact.getComment());
				}
			}
		});
		removedContacts.forEach(removedContact -> contactAs.removeContact(party.getId(), removedContact.getId()));
	}

	private void clearContacts() {
		contacts.clear();
		removedContacts.clear();
	}

	private void addEmptyContactsIfNeed() {
		Arrays.stream(ContactCategory.values()).forEach(category -> {
			if (contacts.stream()
					.noneMatch(contact -> contact.getType() != null && contact.getType().getCategory() == category)) {
				contacts.add(createEmptyContactBy(category));
			}
		});
	}

	private Contact createEmptyContactBy(ContactCategory category) {
		Contact contact;
		switch (category) {
		case EMAIL:
			contact = new EmailContact(null);
			break;
		case PHONE:
			contact = new PhoneContact(null);
			break;
		case SKYPE:
			contact = new SkypeContact(null);
			break;
		case CUSTOM:
			contact = new CustomContact(null);
			break;
		default:
			throw new SystemException(String.format("Unsupported contact category: '%s'", category));
		}
		return contact;
	}

	private boolean isNewContact(Contact contact) {
		return contact.getId() == null;
	}

	private static class ByCategoryComparator implements Comparator<Contact> {

		@Override
		public int compare(Contact o1, Contact o2) {
			if (o1.getType() == null || o2.getType() == null) {
				return 1;
			}

			return o1.getType().getCategory().name().compareTo(o2.getType().getCategory().name());
		}

	}

}
