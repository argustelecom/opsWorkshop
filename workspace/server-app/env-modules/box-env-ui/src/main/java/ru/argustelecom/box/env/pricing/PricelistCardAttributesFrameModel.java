package ru.argustelecom.box.env.pricing;

import static ru.argustelecom.box.env.pricing.PricelistJournalViewState.PricelistJournalMode;

import lombok.Getter;
import java.io.Serializable;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.party.OwnerAppService;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "pricelistCardAttributesFm")
@PresentationModel
public class PricelistCardAttributesFrameModel implements Serializable {

	@Inject
	private OwnerAppService ownerAs;

	@Inject
	private PricelistAppService pricelistAs;

	@Getter
	private PricelistAttributesDto pricelist;

	@Getter
	private List<Owner> owners;

	@Getter
	private boolean renderOwner;

	public void preRender(PricelistAttributesDto pricelist) {
		if (!Objects.equals(this.pricelist, pricelist)) {
			this.pricelist = pricelist;
		}

		owners = ownerAs.findAll();
		renderOwner = owners.size() > 1;
	}

	public void save() {
		pricelistAs.changeName(pricelist.getId(), pricelist.getName());
		pricelistAs.changeOwner(pricelist.getId(), pricelist.getOwner().getId());
		pricelistAs.changeDate(pricelist.getId(), pricelist.getValidFrom(), pricelist.getValidTo());
	}

	public PricelistJournalMode getPricelistType() {
		return PricelistJournalMode.determineMode(pricelist.getClazz());
	}


	private static final long serialVersionUID = -2687588070043242428L;

}