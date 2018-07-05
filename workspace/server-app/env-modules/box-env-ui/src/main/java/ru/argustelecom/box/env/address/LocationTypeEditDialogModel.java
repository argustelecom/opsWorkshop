package ru.argustelecom.box.env.address;

import static ru.argustelecom.box.env.address.LocationCategory.DISTRICT;
import static ru.argustelecom.box.env.address.LocationCategory.LODGING;
import static ru.argustelecom.box.env.address.LocationCategory.REGION;
import static ru.argustelecom.box.env.address.LocationCategory.STREET;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.EditDialogModel;
import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.nls.LocationMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "locationTypeEditDlgM")
@PresentationModel
public class LocationTypeEditDialogModel implements EditDialogModel<LocationType>, Serializable {

	private static final long serialVersionUID = 8709739384057350954L;

	@Inject
	private LocationTypeRepository ltr;

	@Inject
	private LocationLevelRepository llr;

	private LocationType editableLocationType;
	private Callback<LocationType> callback;

	private LocationCategory newCategory;
	private String newName;
	private String newShortName;
	private LocationLevel newLevel;

	private List<LocationLevel> regionLevels;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().update("location_type_edit_form");
		RequestContext.getCurrentInstance().execute("PF('locationTypeEditDlgVar').show();");
	}

	@Override
	public void submit() {
		if (!isEditMode()) {
			LocationType newType;
			switch (newCategory) {
			case REGION:
				newType = ltr.createRegionType(newLevel, newName, newShortName);
				break;
			case DISTRICT:
				newType = ltr.createDistrictType(newName, newShortName);
				break;
			case STREET:
				newType = ltr.createStreetType(newName, newShortName);
				break;
			case LODGING:
				newType = ltr.createLodgingType(newName, newShortName);
				break;
			default:
				throw new UnsupportedOperationException("Unsupported location type category " + newCategory);
			}
			callback.execute(newType);
		} else {
			if (newCategory.equals(REGION))
				editableLocationType.setLevel(newLevel);
			editableLocationType.setName(newName);
			editableLocationType.setShortName(newShortName);
		}
		cancel();
	}

	@Override
	public void cancel() {
		newCategory = null;
		newLevel = null;
		newName = null;
		newShortName = null;
		editableLocationType = null;
	}

	@Override
	public void setEditableObject(LocationType editableObject) {
		this.editableLocationType = editableObject;
		newCategory = determineCategory(editableObject.getLevel());
		newName = editableObject.getName();
		newShortName = editableObject.getShortName();
		newLevel = editableObject.getLevel();
	}

	public LocationCategory[] getCategories() {
		return LocationCategory.forCreationType();
	}

	public List<LocationLevel> getLevels() {
		if (regionLevels == null)
			regionLevels = llr.findRegionLevels();
		return regionLevels;
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private LocationCategory determineCategory(LocationLevel level) {
		if (level.getId().equals(LocationLevel.DISTRICT))
			return DISTRICT;
		if (level.getId().equals(LocationLevel.STREET))
			return STREET;
		if (level.getId().equals(LocationLevel.LODGING))
			return LODGING;
		else
			return REGION;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	@Override
	public LocationType getEditableObject() {
		return editableLocationType;
	}

	@Override
	public Callback<LocationType> getCallback() {
		return callback;
	}

	@Override
	public void setCallback(Callback<LocationType> callback) {
		this.callback = callback;
	}

	public LocationCategory getNewCategory() {
		return newCategory == null ? REGION : newCategory;
	}

	public void setNewCategory(LocationCategory newCategory) {
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

	public LocationLevel getNewLevel() {
		return newLevel;
	}

	public void setNewLevel(LocationLevel newLevel) {
		this.newLevel = newLevel;
	}

	public List<LocationLevel> getRegionLevels() {
		return regionLevels;
	}

	public void setRegionLevels(List<LocationLevel> regionLevels) {
		this.regionLevels = regionLevels;
	}

	@Override
	public String getHeader() {
		LocationMessagesBundle messages = LocaleUtils.getMessages(LocationMessagesBundle.class);
		if (isEditMode()) {
			return messages.locationTypeEditing();
		} else {
			return messages.locationTypeCreation();
		}
	}

}