package ru.argustelecom.box.env.address;

import static ru.argustelecom.box.env.address.LocationCategory.COUNTRY;
import static ru.argustelecom.box.env.address.LocationCategory.REGION;
import static ru.argustelecom.box.env.address.LocationCategory.STREET;

import java.io.Serializable;
import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.model.Country;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.model.Region;
import ru.argustelecom.box.env.address.model.Street;
import ru.argustelecom.box.env.address.nls.LocationMessagesBundle;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "addressAttributesFM")
@PresentationModel
public class AddressAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 4930138757584491766L;

	private static final String EMPTY_ICON = "fa fa-question";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private AddressAppService addressAppSrv;

	@Inject
	private LocationRepository lr;

	@Inject
	private LocationTypeRepository ltr;

	@Inject
	private CurrentLocation currentLocation;

	private Location location;

	public void preRender() {
		refresh();
	}

	public String getSelectedLocationIcon() {
		if (location != null) {
			if (location instanceof Country) {
				return COUNTRY.getIcon();
			}
			if (location instanceof Region) {
				return REGION.getIcon();
			}
			if (location instanceof Street) {
				return STREET.getIcon();
			}
		}
		return EMPTY_ICON;
	}

	public boolean isSelectedCountry() {
		return location instanceof Country;
	}

	public void handleChange() {
		Location oldLocation = null;
		if (location instanceof Country)
			oldLocation = lr.findCountry(location.getName());
		if (location instanceof Region)
			oldLocation = lr.findRegion(location.getParent(), ((Region) location).getType(), location.getName());
		if (location instanceof Street)
			oldLocation = lr.findStreet(location.getParent(), ((Street) location).getType(), location.getName());

		if (oldLocation != null) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			LocationMessagesBundle locationMessages = LocaleUtils.getMessages(LocationMessagesBundle.class);

			Notification.error(
					locationMessages.cannotEditLocation(),
					overallMessages.uniqueConstraintViolation());
			em.refresh(location);
		} else {
			em.flush();
			addressAppSrv.reindex(location.getId());
		}
	}

	public List<LocationType> getTypes() {
		if (location instanceof Region)
			return ltr.findLocationTypes(((Region) location).getType().getLevel());
		if (location instanceof Street)
			return ltr.findLocationTypes(((Street) location).getType().getLevel());
		return Collections.emptyList();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		if (currentLocation.changed(location)) {
			location = currentLocation.getValue();
		}
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Location getLocation() {
		return location;
	}

}