package ru.argustelecom.box.env.order;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.LocationTypeRepository;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ChangeOrderConnectionAddressDialogModel implements Serializable {

	private static final long serialVersionUID = -3389614028063324257L;

	private static final Logger log = Logger.getLogger(ChangeOrderConnectionAddressDialogModel.class);

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationTypeRepository locationTypeRepository;

	@Inject
	private CurrentOrder currentOrder;

	private Order order;
	private Callback<Order> changeConnectionAddressCallback;

	@Getter
	@Setter
	private BusinessObjectDto<Building> newBuilding;

	private LocationType newLodgingType;
	private String newLodging;

	private List<LocationType> lodgingTypes;

	@PostConstruct
	protected void postConstruct() {
		refresh();
	}

	public void preRender() {
		Location location = initializeAndUnproxy(order.getConnectionAddress());
		if (location != null) {
			if (location instanceof Building) {
				newBuilding = businessObjectDtoTr.translate((Building) location);
			}
			if (location instanceof Lodging) {
				Lodging lodging = (Lodging) location;
				newBuilding = businessObjectDtoTr.translate((Building) initializeAndUnproxy(lodging.getParent()));
				newLodgingType = lodging.getType();
				newLodging = lodging.getNumber();
			}
		}
	}

	public void changeConnectionAddress() {
		Location location = null;
		if (newBuilding != null && newLodgingType != null && newLodging != null)
			location = locationRepository.findOrCreateLodging(newBuilding.getIdentifiable(), newLodgingType,
					newLodging);
		if (newBuilding != null && (newLodgingType == null || newLodging == null))
			location = newBuilding.getIdentifiable();
		order.setConnectionAddress(location);
		changeConnectionAddressCallback.execute(order);
	}

	public List<LocationType> getLodgingTypes() {
		if (lodgingTypes == null)
			lodgingTypes = locationTypeRepository.findLodgingTypes();
		return lodgingTypes;
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		checkNotNull(currentOrder.getValue(), "currentOrder required");
		if (currentOrder.changed(order)) {
			order = currentOrder.getValue();
			log.debugv("postConstruct. order_id={0}", order.getId());
		}
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Order getOrder() {
		return order;
	}

	public Callback<Order> getChangeConnectionAddressCallback() {
		return changeConnectionAddressCallback;
	}

	public void setChangeConnectionAddressCallback(Callback<Order> changeConnectionAddressCallback) {
		this.changeConnectionAddressCallback = changeConnectionAddressCallback;
	}

	public LocationType getNewLodgingType() {
		return newLodgingType;
	}

	public void setNewLodgingType(LocationType newLodgingType) {
		this.newLodgingType = newLodgingType;
	}

	public String getNewLodging() {
		return newLodging;
	}

	public void setNewLodging(String newLodging) {
		this.newLodging = newLodging;
	}

}