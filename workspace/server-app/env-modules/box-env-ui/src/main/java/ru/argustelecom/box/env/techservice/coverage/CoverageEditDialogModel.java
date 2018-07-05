package ru.argustelecom.box.env.techservice.coverage;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.techservice.coverage.model.Coverage;
import ru.argustelecom.box.env.techservice.coverage.model.CoverageState;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "coverageEditDlgM")
@PresentationModel
public class CoverageEditDialogModel implements Serializable {

	private static final long serialVersionUID = 5040188875210494389L;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Inject
	private CoverageRepository cr;

	@Inject
	private LocationRepository lr;

	@Getter
	@Setter
	private Callback<Coverage> coverageCallback;

	@Getter
	private Coverage coverage;

	@Getter
	@Setter
	private BusinessObjectDto<Building> newBuilding;

	@Getter
	@Setter
	private CoverageState newState;

	@Getter
	@Setter
	private String newNote;

	private List<CoverageState> states;

	public void submit() {
		if (!isEditMode()) {
			Coverage coverage = cr.create(newBuilding.getIdentifiable(), newState, newNote);
			coverageCallback.execute(coverage);
		} else {
			coverage.setState(newState);
			coverage.setNote(newNote);
		}
		cancel();
	}

	public void cancel() {
		newBuilding = null;
		newState = null;
		newNote = null;
		coverage = null;
	}

	public List<CoverageState> getStates() {
		if (states == null)
			states = CoverageState.values();
		return states;
	}

	public void setCoverage(Coverage coverage) {
		this.coverage = coverage;
		newBuilding = businessObjectDtoTr.translate(EntityManagerUtils.initializeAndUnproxy(coverage.getBuilding()));
		newState = coverage.getState();
		newNote = coverage.getNote();
	}

	public boolean isEditMode() {
		return coverage != null;
	}

}