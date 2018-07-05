package ru.argustelecom.box.env.test;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.contact.ContactTypeRepository;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ContactInfoCreationDlgModel implements Serializable {
	
	private static final long serialVersionUID = 3202425340913472912L;
	
	@Inject
	private ContactTypeRepository contactTypeRepository;
	
	private String firstName;
	private ContactType contactType;
	private String contactValue;
	private String contactDescription;
	
	public void createContactPerson() {
		// TODO создание контктного лица
		clean();
	}
	
	public void clean() {
		firstName = null;
		contactType = null;
		contactValue = null;
		contactDescription = null;
	}
	
	public List<ContactType> getContactTypes() {
		return contactTypeRepository.allContactTypes();
	}
	
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
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
