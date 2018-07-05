package ru.argustelecom.box.env.pricing;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import ru.argustelecom.box.env.party.CustomerSegmentRepository;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.CommonPricelist;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class SegmentsFrameModel implements Serializable {

	private static final long serialVersionUID = 8077751053799510532L;

	@Inject
	private CustomerSegmentRepository customerSegmentRepository;

	private CommonPricelist pricelist;

	private List<CustomerSegment> segments;

	public void preRender(AbstractPricelist pricelist) {
		this.pricelist = (CommonPricelist) pricelist;
	}

	public void remove(CustomerSegment segment) {
		pricelist.removeCustomerSegment(segment);
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

}