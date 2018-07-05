package ru.argustelecom.box.env.address;

import static ru.argustelecom.box.env.address.model.LocationType.GET_ALL_TYPES;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "locationTypeDirectoryVM")
@PresentationModel
public class LocationTypeDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -1045784976208958782L;

	@Inject
	private LocationLevelRepository locationLevelRepository;

	@Inject
	private LocationTypeRepository locationTypeRepository;

	private List<LocationType> types;
	private List<LocationType> selectedTypes;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public List<LocationType> getTypes() {
		if (types == null) {
			types = em.createNamedQuery(GET_ALL_TYPES, LocationType.class).getResultList();
			sortData();
		}
		return types;
	}

	public void remove(LocationType locationType) {
		em.remove(locationType);
		types.remove(locationType);
	}

	public void removeSelectedTypes() {
		selectedTypes.forEach(this::remove);
	}

	public void sortData() {
		Collections.sort(getTypes(), LOCATION_TYPE_COMPARATOR);
	}

	public Callback<LocationType> getCallback() {
		return (newLocationType -> {
			types.add(newLocationType);
			sortData();
		});
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private static final Comparator<LocationType> LOCATION_TYPE_COMPARATOR = (o1, o2) -> {
		if (!o1.getLevel().equals(o2.getLevel())) {
			return o1.getLevel().getObjectName().compareTo(o2.getLevel().getObjectName());
		} else {
			return o1.getObjectName().compareTo(o2.getObjectName());
		}
	};

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public List<LocationType> getSelectedTypes() {
		return selectedTypes;
	}

	public void setSelectedTypes(List<LocationType> selectedTypes) {
		this.selectedTypes = selectedTypes;
	}

}