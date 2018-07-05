package ru.argustelecom.box.env.pricing;

import java.io.Serializable;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "pricelistProductOfferingsFm")
@PresentationModel
public class PricelistProductOfferingsFrameModel implements Serializable {

	@Inject
	private ProductOfferingAppService productOfferingAs;

	@Inject
	private ProductOfferingDtoTranslator productOfferingDtoTr;

	private AbstractPricelist pricelist;
	private List<ProductOfferingDto> offerings;
	private List<ProductOfferingDto> selectedOfferings;

	public void preRender(AbstractPricelist pricelist) {
		if (!Objects.equals(this.pricelist, pricelist)) {
			this.pricelist = pricelist;
			initOfferings();
		}

	}

	public void remove() {
		selectedOfferings.forEach(offering -> {
			productOfferingAs.remove(offering.getId());
			offerings.remove(offering);
		});
	}

	public Callback<ProductOfferingDto> getCallback() {
		return productOfferingDto -> {
			offerings.removeIf(po -> po.equals(productOfferingDto));
			offerings.add(productOfferingDto);
		};
	}

	public AbstractPricelist getPricelist() {
		return pricelist;
	}

	public List<ProductOfferingDto> getOfferings() {
		return offerings;
	}

	public List<ProductOfferingDto> getSelectedOfferings() {
		return selectedOfferings;
	}

	public void setSelectedOfferings(List<ProductOfferingDto> selectedOfferings) {
		this.selectedOfferings = selectedOfferings;
	}

	private void initOfferings() {
		offerings = productOfferingAs.findAllBy(pricelist.getId()).stream().map(productOfferingDtoTr::translate)
				.collect(Collectors.toList());
	}

	private static final long serialVersionUID = 1446942156435721936L;

}