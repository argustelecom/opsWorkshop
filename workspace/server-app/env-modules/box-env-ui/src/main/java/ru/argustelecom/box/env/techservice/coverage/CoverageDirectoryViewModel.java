package ru.argustelecom.box.env.techservice.coverage;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.techservice.coverage.model.Coverage;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "coverageDirectoryVM")
@PresentationModel
public class CoverageDirectoryViewModel extends ViewModel {

	private static final long serialVersionUID = 550431908746125525L;

	@Inject
	private CoverageRepository cr;

	@PersistenceContext
	private EntityManager em;

	private List<Coverage> coverages;
	private List<Coverage> selectedCoverages;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public List<Coverage> getCoverages() {
		if (coverages == null)
			coverages = cr.findAll();
		return coverages;
	}

	public void remove() {
		if (selectedCoverages != null && !selectedCoverages.isEmpty())
			selectedCoverages.forEach(coverage -> {
				coverages.remove(coverage);
				em.remove(coverage);
			});
	}

	public Callback<Coverage> getCoverageCallback() {
		return coverage -> coverages.add(coverage);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public List<Coverage> getSelectedCoverages() {
		return selectedCoverages;
	}

	public void setSelectedCoverages(List<Coverage> selectedCoverages) {
		this.selectedCoverages = selectedCoverages;
	}

}