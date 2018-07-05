package ru.argustelecom.box.homemeasurement;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.login.PersonalAreaLoginRepository;
import ru.argustelecom.box.env.login.model.PersonalAreaLogin;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.homemeasurement.model.HomeMeasurement;
import ru.argustelecom.box.homemeasurement.model.HomeMeasurementHistory;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "homeMeasurementVM")
@PresentationModel
public class HomeMeasurementViewModel extends ViewModel {

	private static final long serialVersionUID = -6348978304215003379L;

	private static final Logger log = Logger.getLogger(HomeMeasurementViewModel.class);

	@Inject
	private PersonalAreaLoginRepository palr;

	@Inject
	private HomeMeasurementRepository hmr;

	private Customer customer;

	private Map<Location, List<HomeMeasurement>> homeMeasurementMap;

	private HomeMeasurement selectedHomeMeasurement;
	private List<HomeMeasurementHistory> historyList;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		if (customer != null)
			initHomeMeasurementMap();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		if (customer == null) {
			PersonalAreaLogin currentLogin = palr.currentLogin();
			checkState(currentLogin != null);
			checkState(currentLogin.getCustomer() != null);

			customer = currentLogin.getCustomer();
			log.debugv("postConstruct. customer_id={0}", customer.getId());
		}
	}

	private void initHomeMeasurementMap() {
		homeMeasurementMap = hmr.getHomeMeasurementMap(customer);
	}

	private void initHistory() {
		if (selectedHomeMeasurement != null)
			historyList = hmr.getHistoryList(selectedHomeMeasurement);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Map<Location, List<HomeMeasurement>> getHomeMeasurementMap() {
		return homeMeasurementMap;
	}

	public HomeMeasurement getSelectedHomeMeasurement() {
		return selectedHomeMeasurement;
	}

	public void setSelectedHomeMeasurement(HomeMeasurement selectedHomeMeasurement) {
		if (!Objects.equals(this.selectedHomeMeasurement, selectedHomeMeasurement)) {
			this.selectedHomeMeasurement = selectedHomeMeasurement;
			initHistory();
		}
		this.selectedHomeMeasurement = selectedHomeMeasurement;
	}

	public List<HomeMeasurementHistory> getHistoryList() {
		return historyList;
	}
}