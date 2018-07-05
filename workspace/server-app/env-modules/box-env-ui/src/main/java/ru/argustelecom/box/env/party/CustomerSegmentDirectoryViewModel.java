package ru.argustelecom.box.env.party;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class CustomerSegmentDirectoryViewModel extends ViewModel {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CustomerSegmentRepository customerSegmentRepo;

	private List<CustomerSegment> customerSegments;
	private List<CustomerSegment> selectedSegments;

	private Callback<CustomerSegment> callback;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
	}

	public List<CustomerSegment> getCustomerSegments() {
		if (customerSegments == null) {
			customerSegments = customerSegmentRepo.findAllSegments();
		}
		return customerSegments;
	}

	public List<CustomerSegment> getSelectedSegments() {
		return selectedSegments;
	}

	public void setSelectedSegments(List<CustomerSegment> selectedSegments) {
		this.selectedSegments = selectedSegments;
	}

	public void removeSelectedSegments() {
		if (selectedSegments == null || selectedSegments.isEmpty())
			return;

		selectedSegments.forEach(em::remove);
		customerSegments.removeAll(selectedSegments);
		selectedSegments.clear();
	}

	public Callback<CustomerSegment> getCallback() {
		if (callback == null) {
			callback = segment -> {
				if (!customerSegments.contains(segment)) {
					customerSegments.add(segment);
				}
			};
		}
		return callback;
	}

	private static final long serialVersionUID = 8217200128904673858L;

}
