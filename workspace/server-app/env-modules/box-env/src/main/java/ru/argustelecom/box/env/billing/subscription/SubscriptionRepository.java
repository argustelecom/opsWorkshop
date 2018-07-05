package ru.argustelecom.box.env.billing.subscription;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_FOR_DEBT;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.provision.model.AbstractProvisionTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.subscription.model.ContractSubjectCause;
import ru.argustelecom.box.env.billing.subscription.model.OrderSubjectCause;
import ru.argustelecom.box.env.billing.subscription.model.PricelistCostCause;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.Subscription.SubscriptionQuery;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionSubjectCause;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.contract.ContractEntryRepository;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.pricing.model.PeriodProductOffering;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.inf.service.Repository;

@Repository
public class SubscriptionRepository implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private SubscriptionCauseRepository subsCauseRp;

	@Inject
	private ContractEntryRepository contractEntryRp;

	public Subscription createSubscriptionByContract(PersonalAccount account, ProductOfferingContractEntry entry) {
		return createSubscriptionByContract(account, entry, null, null);
	}

	public Subscription createSubscriptionByContract(PersonalAccount personalAccount,
			ProductOfferingContractEntry entry, Date validFrom, Date validTo) {

		Subscription instance = createSubscription(personalAccount, entry.getProductOffering(), validFrom, validTo);
		ContractSubjectCause subjectCause = subsCauseRp.createContractSubjectCause(instance, entry, null);
		PricelistCostCause costCause = subsCauseRp.createPricelistCostCause(instance,
				entry.getProductOffering().getPricelist(), null);

		instance.setSubjectCause(subjectCause);
		instance.setCostCause(costCause);

		entry.setPersonalAccount(personalAccount);

		return instance;
	}

	/**
	 * <b>Не использовать</b>. От создания подписок на основании заявки пока что отказались: BOX-1290. Специально сделан
	 * Deprecated.
	 */
	@Deprecated
	public Subscription createSubscriptionByOrder(PersonalAccount personalAccount, ProductOffering productOffering,
			Date validFrom, Date validTo) {

		Subscription instance = createSubscription(personalAccount, productOffering, validFrom, validTo);

		OrderSubjectCause subjectCause = subsCauseRp.createOrderSubjectCause(instance, "Заявка");
		PricelistCostCause costCause = subsCauseRp.createPricelistCostCause(instance, productOffering.getPricelist(),
				null);

		instance.setSubjectCause(subjectCause);
		instance.setCostCause(costCause);

		return instance;
	}

	private Subscription createSubscription(PersonalAccount personalAccount, ProductOffering productOffering,
			Date validFrom, Date validTo) {
		checkState(productOffering.isRecurrentProduct(),
				"It's impossible to create subscription by non recurrent product");

		Long subscriptionId = idSequence.nextValue(Subscription.class);

		productOffering = initializeAndUnproxy(productOffering);
		AbstractProvisionTerms provisionTerms = initializeAndUnproxy(productOffering.getProvisionTerms());

		//@formatter:off
		Subscription instance = Subscription.builder()
				.id(subscriptionId)
				.personalAccount(personalAccount)
				.subject(productOffering.getProductType())
				.cost(productOffering.getPrice())
				.provisionTerms((RecurrentTerms) provisionTerms)
				.accountingDuration(((PeriodProductOffering) productOffering).getPeriod())
				.validFrom(validFrom)
				.validTo(validTo)
			.build();
		//@formatter:on

		em.persist(instance);

		personalAccount.addSubscription(instance);

		return instance;
	}

	public List<Subscription> findAllSubscriptions(PersonalAccount personalAccount) {
		SubscriptionQuery query = new SubscriptionQuery();
		query.and(query.personalAccount().equal(personalAccount));
		return query.createTypedQuery(em).getResultList();
	}

	public List<Subscription> findUnterminatedSubscriptions(PersonalAccount personalAccount) {
		//@formatter:off
		SubscriptionQuery query = new SubscriptionQuery();
		query.and(
			query.personalAccount().equal(personalAccount),
			query.state().notEqual(CLOSED)
		); //@formatter:on
		return query.createTypedQuery(em).getResultList();
	}

	public List<Subscription> findActiveSubscriptions(PersonalAccount personalAccount) {
		//@formatter:off
		SubscriptionQuery query = new SubscriptionQuery();
		query.and(
				query.personalAccount().equal(personalAccount),
				query.state().notEqual(SUSPENDED),
				query.state().notEqual(CLOSED)
		); //@formatter:on
		return query.createTypedQuery(em).getResultList();
	}

	public List<Subscription> findSuspendedForDebtSubscriptions(PersonalAccount personalAccount,
			Boolean manualControlled, int limit) {

		//@formatter:off
		SubscriptionQuery query = new SubscriptionQuery();
		query.and(
			query.personalAccount().equal(personalAccount),
			query.state().equal(SUSPENDED_FOR_DEBT)
		);
		
		if (manualControlled != null) {
			query.and(manualControlled ? query.manualControl().isTrue() : query.manualControl().isFalse());
		}
		//@formatter:on

		TypedQuery<Subscription> jpaQuery = query.createTypedQuery(em);
		if (limit > 0) {
			jpaQuery.setMaxResults(limit);
		}

		return jpaQuery.getResultList();
	}

	public Subscription findSubscription(ProductOfferingContractEntry entry) {
		checkRequiredArgument(entry, "ContractEntry");

		SubscriptionQuery query = new SubscriptionQuery();
		query.and(query.subject().equal(entry));
		return query.getSingleResult(em, false);
	}

	public List<Service> findServicesBySubscription(Subscription subscription) {
		checkRequiredArgument(subscription, "Subscription");

		SubscriptionSubjectCause subjectCause = subscription.getSubjectCause();
		if (subjectCause instanceof ContractSubjectCause) {
			ProductOfferingContractEntry contractEntry = ((ContractSubjectCause) subjectCause).getContractEntry();
			return contractEntryRp.findServicesBySubject(contractEntry);
		}

		return Collections.emptyList();
	}

	public ProductOffering findProductOfferingBySubscription(Subscription subscription) {
		checkRequiredArgument(subscription, "Subscription");

		SubscriptionSubjectCause subjectCause = subscription.getSubjectCause();
		if (subjectCause instanceof ContractSubjectCause) {
			ProductOfferingContractEntry contractEntry = ((ContractSubjectCause) subjectCause).getContractEntry();
			return initializeAndUnproxy(contractEntry.getProductOffering());
		}

		return null;
	}

	private static final long serialVersionUID = -7353726064105938967L;

}