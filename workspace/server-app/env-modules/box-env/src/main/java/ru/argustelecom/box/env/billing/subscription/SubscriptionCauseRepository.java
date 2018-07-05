package ru.argustelecom.box.env.billing.subscription;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.subscription.model.ContractSubjectCause;
import ru.argustelecom.box.env.billing.subscription.model.OrderSubjectCause;
import ru.argustelecom.box.env.billing.subscription.model.PricelistCostCause;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class SubscriptionCauseRepository implements Serializable {

	@Inject
	private IdSequenceService iss;

	public PricelistCostCause createPricelistCostCause(Subscription subscription, AbstractPricelist pricelist,
			String note) {
		checkNotNull(subscription, "subscription is required param");
		checkNotNull(pricelist, "pricelist is required param");

		PricelistCostCause instance = new PricelistCostCause(iss.nextValue(PricelistCostCause.class));

		instance.setSubscription(subscription);
		instance.setPricelist(pricelist);
		instance.setNote(note);
		subscription.setCostCause(instance);

		return instance;
	}

	public ContractSubjectCause createContractSubjectCause(Subscription subscription,
			ProductOfferingContractEntry contractEntry, String note) {
		checkNotNull(subscription, "subscription is required param");
		checkNotNull(contractEntry, "contract entry is required param");

		ContractSubjectCause instance = new ContractSubjectCause(iss.nextValue(ContractSubjectCause.class));

		instance.setSubscription(subscription);
		instance.setContractEntry(contractEntry);
		instance.setNote(note);
		subscription.setSubjectCause(instance);

		return instance;
	}

	public OrderSubjectCause createOrderSubjectCause(Subscription subscription, String note) {
		checkNotNull(subscription, "subscription is required param");
		checkNotNull(note, "note is required param for order subject and must contains info about order");

		OrderSubjectCause instance = new OrderSubjectCause(iss.nextValue(OrderSubjectCause.class));

		instance.setSubscription(subscription);
		instance.setNote(note);
		subscription.setSubjectCause(instance);

		return instance;
	}

	private static final long serialVersionUID = 3336473788202057484L;

}