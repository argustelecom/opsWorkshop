package ru.argustelecom.box.env.address;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.address.model.LocationLevel;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "locationLevelDirectoryVM")
@PresentationModel
public class LocationLevelDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = -5708575549134446099L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private LocationLevelRepository llr;

	private List<LocationLevel> levels;
	private List<LocationLevel> selectedLevels;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public List<LocationLevel> getLevels() {
		if (levels == null) {
			levels = llr.findAllLevels();
		}
		return levels;
	}

	public void remove() {
		selectedLevels.forEach(level -> {
			em.remove(level);
			levels.remove(level);
		});
	}

	public Callback<LocationLevel> getCallback() {
		return (level -> levels.add(level));
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public List<LocationLevel> getSelectedLevels() {
		return selectedLevels;
	}

	public void setSelectedLevels(List<LocationLevel> selectedLevels) {
		this.selectedLevels = selectedLevels;
	}

}