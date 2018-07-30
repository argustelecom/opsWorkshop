package ru.argustelecom.box.env.contact;

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
import ru.argustelecom.box.env.party.model.Party;
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

	@Inject
	private ContactDtoTranslator translator;

	@Getter
	@Setter
	private Party party;

	@Getter
	private List<ContactDto> contacts = new ArrayList<>();

	@Getter
	private List<ContactDto> removedContacts = new ArrayList<>();

	public void preRender(Party party) {
		clearContacts();
		if (party != null) {
			this.party = party;
			party.getContactInfo().getContacts().forEach(contact -> contacts.add(translator.translate(contact)));
		}
		addEmptyContactsIfNeed();
		contacts.sort(new ByCategoryComparator());
	}

	public List<ContactType> getContactTypes(ContactDto contactDto) {
		return directoryCacheService.getDirectoryObjects(ContactType.class).stream()
				.filter(contactType -> contactType.getCategory().equals(contactDto.getCategory()))
				.collect(Collectors.toList());
	}

	public void createContact(ContactDto template) {
		contacts.add(createEmptyContactDtoBy(template.getCategory()));
		contacts.sort(new ByCategoryComparator());
	}

	public void removeContact(ContactDto contactDto) {
		if (contacts.stream().noneMatch(
				contact -> contact.getCategory().equals(contactDto.getCategory()) && !contact.equals(contactDto))) {
			contactDto.setType(null);
			contactDto.setValue(null);
			contactDto.setComment(null);
		} else {
			contacts.remove(contactDto);
			if (!isNewContact(contactDto)) {
				removedContacts.add(contactDto);
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
			if (contacts.stream().noneMatch(contact -> contact.getCategory().equals(category))) {
				contacts.add(createEmptyContactDtoBy(category));
			}
		});
	}

	private ContactDto createEmptyContactDtoBy(ContactCategory category) {
		ContactDto contactDto;
		switch (category) {
		case EMAIL:
			contactDto = new EmailContactDto();
			break;
		case CUSTOM:
			contactDto = new CustomContactDto();
			break;
		default:
			throw new SystemException(String.format("Unsupported contact category: '%s'", category));
		}
		contactDto.setCategory(category);
		return contactDto;
	}

	private boolean isNewContact(ContactDto contact) {
		return contact.getId() == null;
	}

	private static class ByCategoryComparator implements Comparator<ContactDto> {

		@Override
		public int compare(ContactDto o1, ContactDto o2) {
			return o1.getCategory().name().compareTo(o2.getCategory().name());
		}

	}

}
