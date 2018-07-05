package ru.argustelecom.box.env.contact;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.EditDialogModel;
import ru.argustelecom.box.env.contact.nls.ContactMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "contactTypeEditDM")
@PresentationModel
public class ContactTypeEditDialogModel implements EditDialogModel<ContactType>, Serializable {

	private static final long serialVersionUID = 3661600831969601608L;

	@Inject
	private ContactTypeRepository ctr;

	private ContactType editableObject;
	private Callback<ContactType> callback;

	private ContactCategory newCategory;
	private String newName;
	private String newShortName;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().update("contact_type_edit_form");
		RequestContext.getCurrentInstance().execute("PF('contactTypeEditDlg').show();");
	}

	@Override
	public void submit() {
		boolean success;
		if (!isEditMode()) {
			success = create();
		} else {
			success = change();
		}

		if (success) {
			cancel();
			RequestContext.getCurrentInstance().execute("PF('contactTypeEditDlg').hide();");
		}
	}

	@Override
	@SuppressWarnings("Duplicates")
	public void cancel() {
		editableObject = null;
		callback = null;
		newCategory = null;
		newName = null;
		newShortName = null;
	}

	@Override
	public void setEditableObject(ContactType editableObject) {
		this.editableObject = editableObject;

		newCategory = editableObject.getCategory();
		newName = editableObject.getName();
		newShortName = editableObject.getShortName();
	}

	public ContactCategory[] getCategories() {
		return ContactCategory.values();
	}

	private boolean create() {
		ContactType existingContactType = ctr.findContactType(newCategory, newName);
		if (existingContactType != null) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			ContactMessagesBundle contractMessages = LocaleUtils.getMessages(ContactMessagesBundle.class);

			Notification.error(
					overallMessages.cannotSaveChanges(),
					contractMessages.contactTypeAlreadyExist(newName)
			);
			return false;
		}

		ContactType newContactType = ctr.createContactType(newCategory, newName, newShortName);
		callback.execute(newContactType);
		return true;
	}

	private boolean change() {
		ContactType existingContactType = ctr.findContactType(newCategory, newName);
		if (existingContactType != null && !existingContactType.equals(editableObject)) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			ContactMessagesBundle contractMessages = LocaleUtils.getMessages(ContactMessagesBundle.class);

			Notification.error(
					overallMessages.cannotSaveChanges(),
					contractMessages.contactTypeAlreadyExist(newName)
			);
			return false;
		}

		editableObject.setCategory(newCategory);
		editableObject.setName(newName);
		editableObject.setShortName(newShortName);
		return true;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public ContactCategory getNewCategory() {
		return newCategory;
	}

	public void setNewCategory(ContactCategory newCategory) {
		this.newCategory = newCategory;
	}

	public String getNewName() {
		return newName;
	}

	public void setNewName(String newName) {
		this.newName = newName;
	}

	public String getNewShortName() {
		return newShortName;
	}

	public void setNewShortName(String newShortName) {
		this.newShortName = newShortName;
	}

	@Override
	public ContactType getEditableObject() {
		return editableObject;
	}

	@Override
	public Callback<ContactType> getCallback() {
		return callback;
	}

	@Override
	public void setCallback(Callback<ContactType> callback) {
		this.callback = callback;
	}

}