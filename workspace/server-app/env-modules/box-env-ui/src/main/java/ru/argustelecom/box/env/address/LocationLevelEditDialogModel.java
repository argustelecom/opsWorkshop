package ru.argustelecom.box.env.address;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.EditDialogModel;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.nls.LocationMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "locationLevelEditDlgM")
@PresentationModel
public class LocationLevelEditDialogModel implements EditDialogModel<LocationLevel>, Serializable {

	private static final long serialVersionUID = 4169269902623888151L;

	@Inject
	private LocationLevelRepository llr;

	private LocationLevel editableLocationLevel;
	private Callback<LocationLevel> callback;

	@Getter
	@Setter
	private String newName;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().update("location_level_edit_form");
		RequestContext.getCurrentInstance().execute("PF('locationLevelEditDlgVar').show();");
	}

	@Override
	public void submit() {
		if (!isEditMode()) {
			LocationLevel level = llr.createLevel(newName);
			callback.execute(level);
		} else {
			editableLocationLevel.setName(newName);
		}
	}

	@Override
	public void cancel() {
		editableLocationLevel = null;
		callback = null;
		newName = null;
	}

	@Override
	public void setEditableObject(LocationLevel editableObject) {
		editableLocationLevel = editableObject;
		newName = editableObject.getName();
	}

	@Override
	public LocationLevel getEditableObject() {
		return editableLocationLevel;
	}

	@Override
	public Callback<LocationLevel> getCallback() {
		return callback;
	}

	@Override
	public void setCallback(Callback<LocationLevel> callback) {
		this.callback = callback;
	}

	@Override
	public String getHeader() {
		LocationMessagesBundle messages = LocaleUtils.getMessages(LocationMessagesBundle.class);
		if (isEditMode()) {
			return messages.locationLevelEditing();
		} else {
			return messages.locationLevelCreation();
		}
	}

}