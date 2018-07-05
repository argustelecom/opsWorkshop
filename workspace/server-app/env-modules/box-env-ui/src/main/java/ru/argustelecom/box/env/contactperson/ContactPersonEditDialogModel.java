package ru.argustelecom.box.env.contactperson;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.context.RequestContext;

import lombok.Getter;
import ru.argustelecom.box.env.EditDialogModel;
import ru.argustelecom.box.env.contact.ContactEditFrameModel;
import ru.argustelecom.box.env.contactperson.nls.ContactPersonMessagesBundle;
import ru.argustelecom.box.env.contractperson.ContactPersonDataAppService;
import ru.argustelecom.box.env.party.model.role.ContactPerson;
import ru.argustelecom.box.env.person.avatar.PersonAvatarFrameModel;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "contactPersonEditDM")
@PresentationModel
public class ContactPersonEditDialogModel implements EditDialogModel<ContactPersonDataDto>, Serializable {

	private static final long serialVersionUID = 5535553765438514072L;

	private static final String STYLE_FOR_DLG_EDIT = "personnel-dlg-edit";

	@Inject
	private ContactPersonDataAppService cpdAppService;

	@Inject
	private ContactPersonDataDtoTranslator cpdTranslator;

	@Inject
	private PersonAvatarFrameModel personAvatarFm;

	@Inject
	private ContactEditFrameModel contactEditFM;

	@PersistenceContext
	private EntityManager em;

	@Getter
	private ContactPerson contactPerson;

	private Long companyId;
	private ContactPersonDataDto cpdDto = new ContactPersonDataDto();
	private Callback<ContactPersonDataDto> callback;

	public void open() {
		RequestContext.getCurrentInstance().update("contact_person_edit_form-contact_person_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('contactPersonEditDlg').show()");
	}

	@Override
	public boolean isEditMode() {
		return cpdDto.getContactPersonId() != null;
	}

	@Override
	public void submit() {

		if (isEditMode())
			change();
		else {
			create();
		}

		submitContacts();
		callback.execute(cpdDto);

		personAvatarFm.clean();
		cancel();
	}

	@Override
	public void cancel() {
		companyId = null;
		cpdDto = new ContactPersonDataDto();
		contactPerson = null;
		callback = null;
		personAvatarFm.clean();
	}

	@Override
	public String getHeader() {
		ContactPersonMessagesBundle messages = LocaleUtils.getMessages(ContactPersonMessagesBundle.class);
		if (isEditMode()) {
			return messages.contactPersonEditing();
		}
		return messages.contactPersonCreation();
	}

	private void change() {
		//@formatter:off
		cpdAppService.renamePerson(
			cpdDto.getPersonId(),
			cpdDto.getPrefix(),
			cpdDto.getFirstName(),
			cpdDto.getSecondName(),
			cpdDto.getLastName(),
			cpdDto.getSuffix()
		);

		cpdAppService.editPersonData(
			cpdDto.getPersonId(),
			cpdDto.getNote()
		);

		// change avatar
		if (personAvatarFm.isAvatarChanged())
			if (cpdDto.getImageInputStream() != null)
				cpdAppService.addPersonAvatar(
						cpdDto.getPersonId(),
						cpdDto.getImageInputStream(),
						cpdDto.getImageFormatName()
				);
			else
				cpdAppService.removePersonAvatar(cpdDto.getPersonId());

		cpdAppService.changeContactPersonAppointment(
			cpdDto.getContactPersonId(),
			cpdDto.getAppointment()
		);
		//@formatter:on
	}

	private void create() {
		//@formatter:off
		ContactPerson newContactPerson = cpdAppService.createContactPerson(
			cpdDto.getCompanyId(),
			cpdDto.getPrefix(),
			cpdDto.getLastName(),
			cpdDto.getFirstName(),
			cpdDto.getSecondName(),
			cpdDto.getSuffix(),
			cpdDto.getAppointment()
		);

		this.contactPerson = newContactPerson;
		cpdDto.setContactPersonId(newContactPerson.getId());
		cpdDto.setPersonId(newContactPerson.getParty().getId());

		// add avatar
		if (personAvatarFm.isAvatarChanged() && cpdDto.getImageInputStream() != null)
			cpdAppService.addPersonAvatar(
					cpdDto.getPersonId(),
					cpdDto.getImageInputStream(),
					cpdDto.getImageFormatName()
			);
		//@formatter:on
	}

	private void submitContacts() {
		contactEditFM.setParty(contactPerson.getParty());
		contactEditFM.submit();
		cpdDto = cpdTranslator.translate(contactPerson);
	}

	public Long getCompanyId() {
		return companyId;
	}

	public void setCompanyId(Long companyId) {
		this.companyId = companyId;

		if (cpdDto != null && !isEditMode())
			cpdDto.setCompanyId(companyId);
	}

	public String getStyleClass() {
		return isEditMode() ? StringUtils.EMPTY : STYLE_FOR_DLG_EDIT;
	}

	@Override
	public ContactPersonDataDto getEditableObject() {
		return cpdDto;
	}

	@Override
	public void setEditableObject(ContactPersonDataDto contactPersonDataDto) {
		this.cpdDto = contactPersonDataDto;
		this.contactPerson = em.getReference(ContactPerson.class, contactPersonDataDto.getContactPersonId());
	}

	@Override
	public Callback<ContactPersonDataDto> getCallback() {
		return callback;
	}

	@Override
	public void setCallback(Callback<ContactPersonDataDto> callback) {
		this.callback = callback;
	}

}