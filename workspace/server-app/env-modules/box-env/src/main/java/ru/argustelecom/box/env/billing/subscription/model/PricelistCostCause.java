package ru.argustelecom.box.env.billing.subscription.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import ru.argustelecom.box.env.pricing.model.AbstractPricelist;

@Entity
@Access(AccessType.FIELD)
public class PricelistCostCause extends SubscriptionCostCause {

	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	@JoinColumn(name = "pricelist_id")
	private AbstractPricelist pricelist;

	protected PricelistCostCause() {
	}

	public PricelistCostCause(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return pricelist.getObjectName();
	}

	public AbstractPricelist getPricelist() {
		return pricelist;
	}

	public void setPricelist(AbstractPricelist pricelist) {
		this.pricelist = pricelist;
	}

	private static final long serialVersionUID = 5481379967163252156L;

}