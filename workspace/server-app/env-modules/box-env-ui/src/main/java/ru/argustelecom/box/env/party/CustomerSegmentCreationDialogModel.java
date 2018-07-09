package ru.argustelecom.box.env.party;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("customerSegmentCreationDlg")
public class CustomerSegmentCreationDialogModel implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CustomerTypeRepository customerTypeRp;

	private List<CustomerType> customerTypes;

	private CustomerSegment segment;
	private CustomerType segmentCustomerType;
	private String segmentName;
	private String segmentDesc;

	private Callback<CustomerSegment> callback;

	public List<CustomerType> getCustomerTypes() {
		if (customerTypes == null) {
			customerTypes = customerTypeRp.getAllCustomerTypes();
		}
		return customerTypes;
	}

	public void onCancelSegment() {
		cleanDialog();
	}

	public void cleanDialog() {
		segment = null;
		segmentName = null;
		segmentDesc = null;
		segmentCustomerType = null;
		callback = null;
	}

	public boolean isNewSegment() {
		return segment == null;
	}

	public CustomerSegment getSegment() {
		return segment;
	}

	public void setSegment(CustomerSegment segment) {
		this.segment = segment;
		this.segmentName = segment != null ? segment.getObjectName() : null;
		this.segmentDesc = segment != null ? segment.getDescription() : null;
		this.segmentCustomerType = segment != null ? segment.getCustomerType() : null;
	}

	public CustomerType getSegmentCustomerType() {
		return segmentCustomerType;
	}

	public void setSegmentCustomerType(CustomerType segmentCustomerType) {
		this.segmentCustomerType = segmentCustomerType;
	}

	public String getSegmentName() {
		return segmentName;
	}

	public void setSegmentName(String segmentName) {
		this.segmentName = segmentName;
	}

	public String getSegmentDesc() {
		return segmentDesc;
	}

	public void setSegmentDesc(String segmentDesc) {
		this.segmentDesc = segmentDesc;
	}

	public Callback<CustomerSegment> getCallback() {
		return callback;
	}

	public void setCallback(Callback<CustomerSegment> callback) {
		this.callback = callback;
	}

	private static final long serialVersionUID = -4542401563331519336L;

}
