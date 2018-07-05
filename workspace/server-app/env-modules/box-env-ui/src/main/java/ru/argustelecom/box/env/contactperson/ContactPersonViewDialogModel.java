package ru.argustelecom.box.env.contactperson;

import java.io.Serializable;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import ru.argustelecom.box.env.party.model.role.ContactPerson;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "contactPersonViewDM")
@PresentationModel
public class ContactPersonViewDialogModel implements Serializable {

	private static final long serialVersionUID = -8466135817771474972L;

	@PersistenceContext
	private EntityManager em;

	@Getter
	private ContactPerson contactPerson;

	private ContactPersonDataDto cpdDto;

	public void open() {
		RequestContext.getCurrentInstance().update("contact_person_view_form-contact_person_view_dlg");
		RequestContext.getCurrentInstance().execute("PF('contactPersonViewDlg').show()");
	}

	public ContactPersonDataDto getCpdDto() {
		return cpdDto;
	}

	public void setCpdDto(ContactPersonDataDto cpdDto) {
		this.cpdDto = cpdDto;
		contactPerson = em.getReference(ContactPerson.class, cpdDto.getContactPersonId());
	}

}