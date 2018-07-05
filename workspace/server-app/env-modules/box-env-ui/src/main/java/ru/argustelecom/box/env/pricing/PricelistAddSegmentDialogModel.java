package ru.argustelecom.box.env.pricing;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.party.CustomerSegmentRepository;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class PricelistAddSegmentDialogModel implements Serializable {

	private static final long serialVersionUID = -6633496051505953832L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CustomerSegmentRepository customerSegmentRepository;

	private CommonPricelist pricelist;

	private List<CustomerSegment> newSegments;

	private List<CustomerSegment> segments;

	public void onCreationDialogOpen() {
		RequestContext.getCurrentInstance().update("add_segment_form");
		RequestContext.getCurrentInstance().execute("PF('addSegmentDlgVar').show()");
	}

	public void add() {
		newSegments.forEach(pricelist::addCustomerSegment);
		em.merge(pricelist);
		cleanParams();
	}

	public void cleanParams() {
		newSegments = null;
	}

	public List<CustomerSegment> getSegments() {
		if (segments == null)
			segments = customerSegmentRepository.findAllSegments();
		return segments.stream().filter(segment -> !pricelist.getCustomerSegments().contains(segment))
				.collect(Collectors.toList());
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public CommonPricelist getPricelist() {
		return pricelist;
	}

	public void setPricelist(CommonPricelist pricelist) {
		this.pricelist = pricelist;
	}

	public List<CustomerSegment> getNewSegments() {
		return newSegments;
	}

	public void setNewSegments(List<CustomerSegment> newSegments) {
		this.newSegments = newSegments;
	}

}