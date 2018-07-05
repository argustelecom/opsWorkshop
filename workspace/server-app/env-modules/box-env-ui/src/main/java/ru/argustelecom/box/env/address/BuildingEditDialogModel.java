package ru.argustelecom.box.env.address;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.nls.LocationMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "buildingEditDlgM")
@PresentationModel
public class BuildingEditDialogModel implements Serializable {

	private static final long serialVersionUID = -237922220379514775L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private AddressAppService addressAppSrv;

	@Inject
	private LocationRepository lr;

	@Getter
	@Setter
	private Callback<Building> buildingCallback;
	@Getter
	@Setter
	private Building building;
	@Getter
	@Setter
	private Location parent;
	@Getter
	@Setter
	private String newNumber;
	@Getter
	@Setter
	private String newCorpus;
	@Getter
	@Setter
	private String newWing;
	@Getter
	@Setter
	private String newLandmark;
	@Getter
	@Setter
	private String newPostIndex;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().update("building_edit_form-building_edit_dlg");
		RequestContext.getCurrentInstance().execute("PF('buildingEditDlgVar').show();");
	}

	public void submit() {
		boolean success;
		if (!isEditMode())
			success = create();
		else
			success = change();

		if (success) {
			cancel();

			RequestContext.getCurrentInstance().execute("PF('buildingEditDlgVar').hide();");
		}
	}

	public void cancel() {
		newNumber = null;
		newCorpus = null;
		newWing = null;
		newLandmark = null;
		newPostIndex = null;
		building = null;
	}

	public void setBuilding(Building building) {
		this.building = building;
		this.parent = building.getParent();
		this.newNumber = building.getNumber();
		this.newCorpus = building.getCorpus();
		this.newWing = building.getWing();
		this.newLandmark = building.getLandmark();
		this.newPostIndex = building.getPostIndex();
	}

	public boolean isEditMode() {
		return building != null;
	}

	public String getHeader() {
		LocationMessagesBundle messages = LocaleUtils.getMessages(LocationMessagesBundle.class);
		if (isEditMode()) {
			return messages.buildingEditing();
		} else {
			return messages.buildingCreation();
		}
	}

	public String getSubmitButtonLabel() {
		OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		if (isEditMode()) {
			return messages.save();
		} else {
			return messages.create();
		}
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private boolean create() {
		Building oldBuilding = lr.findBuilding(parent, newNumber, newCorpus, newWing);
		if (showWarnNotification(oldBuilding != null, oldBuilding))
			return false;

		building = lr.createBuilding(parent, newNumber, newCorpus, newWing, newPostIndex, newLandmark);

		em.flush();
		addressAppSrv.reindex(building.getId());

		buildingCallback.execute(building);
		return true;
	}

	private boolean change() {
		Building existingBuilding = lr.findBuilding(parent, newNumber, newCorpus, newWing);
		if (showWarnNotification(existingBuilding != null && !existingBuilding.equals(building), existingBuilding))
			return false;

		building.setNumber(newNumber);
		building.setCorpus(newCorpus);
		building.setWing(newWing);
		building.setName(newNumber);
		building.setPostIndex(newPostIndex);
		building.setLandmark(newLandmark);

		em.flush();
		addressAppSrv.reindex(building.getId());

		return true;
	}

	private boolean showWarnNotification(boolean condition, Building building) {
		if (condition) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			LocationMessagesBundle locationMessages = LocaleUtils.getMessages(LocationMessagesBundle.class);

			Notification.error(overallMessages.cannotSaveChanges(),
					locationMessages.buildingAlreadyExist(building.getObjectName()));
		}
		return condition;
	}

}