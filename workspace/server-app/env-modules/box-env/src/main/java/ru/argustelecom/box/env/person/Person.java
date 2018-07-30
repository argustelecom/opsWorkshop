package ru.argustelecom.box.env.person;

import org.hibernate.validator.constraints.NotBlank;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contact.ContactInfo;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.PersonName;
import ru.argustelecom.system.inf.modelbase.SuperClass;

@Getter
@Setter
public class Person extends SuperClass {
	private static final long serialVersionUID = 7924275862795533940L;

	private Long personId;
	private String note;
	private PersonName personName;
	private PartyRole partyRole;
	private ContactInfo contactInfo;

	public Person() {
	}

	public Person(Long personId, PersonName personName, String note) {
		this.personId = personId;
		this.personName = personName;
		this.note = note;
	}

	public void addRole(PartyRole partyRole) {
		this.partyRole = partyRole;
	}

	public void setContactInfo(ContactInfo contactInfo) {
		this.contactInfo = contactInfo;
	}

	public void changeNote(String note) {
		this.note = note;
	}

	public void rename(PersonName personName) {
		this.personName = personName;
	}
}