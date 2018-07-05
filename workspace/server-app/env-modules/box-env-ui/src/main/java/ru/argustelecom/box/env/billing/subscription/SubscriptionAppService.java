package ru.argustelecom.box.env.billing.subscription;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.contract.ContractRepository;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class SubscriptionAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private ContractRepository contractRp;

	public boolean haveContractWithEntriesWithoutSubs(Long customerId) {
		return contractRp.countContractsWithEntriesWithoutSubs(customerId) != 0;
	}

	public List<Subscription> findAllSubscriptions(Long personalAccountId) {
		return subscriptionRp.findAllSubscriptions(em.find(PersonalAccount.class, personalAccountId));
	}

	public List<Subscription> findAllActiveSubscriptions(Long personalAccountId) {
		return subscriptionRp.findActiveSubscriptions(em.find(PersonalAccount.class, personalAccountId));
	}

	public Subscription createSubscriptionByContract(Long personalAccountId, Long contractEntryId, Date validFrom,
			Date validTo) {
		return subscriptionRp.createSubscriptionByContract(em.find(PersonalAccount.class, personalAccountId),
				em.find(ProductOfferingContractEntry.class, contractEntryId), validFrom, validTo);
	}

	/**
	 * <b>Не использовать</b>. От создания подписок на основании заявки пока что отказались: BOX-1290. Специально сделан
	 * Deprecated.
	 */
	public Subscription createSubscriptionByOrder(Long personalAccountId, Long pricelistEntryId, Date validFrom,
			Date validTo) {
		return subscriptionRp.createSubscriptionByOrder(em.find(PersonalAccount.class, personalAccountId),
				em.find(ProductOffering.class, pricelistEntryId), validFrom, validTo);
	}

	public void removeSubscription(Long id) {
		em.remove(em.find(Subscription.class, id));
	}

	private static final long serialVersionUID = -3843816992913088988L;

}