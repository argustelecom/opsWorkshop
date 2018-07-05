package ru.argustelecom.box.env.billing.subscription;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.CLOSED;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.SubscriptionAccountingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.exception.SystemException;

//FIXME переписать!!!
@DomainService
public class SubscriptionChargesService implements Serializable {

	private static final long serialVersionUID = -8950833982872888247L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private SubscriptionAccountingService accountingSvc;

	public List<Subscription> getSubscriptions(PersonalAccount personalAccount, Range<LocalDateTime> period) {
		checkArgument(personalAccount != null);

		List<Subscription> subscriptions = new ArrayList<>();
		personalAccount.getSubscriptions().forEach(subscription -> {
			LocalDateTime subscriptionStart = toLocalDateTime(subscription.getValidFrom());
			LocalDateTime subscriptionEnd = subscription.getValidTo() != null
					? toLocalDateTime(subscription.getValidTo()) : null;
			boolean started = subscriptionStart.isBefore(period.upperEndpoint());
			boolean notEnded = subscriptionEnd == null || subscriptionEnd.isAfter(period.lowerEndpoint());
			boolean hasCharges = true;
			if (period.lowerEndpoint().isAfter(LocalDateTime.now())) {
				hasCharges = !subscription.getState().equals(CLOSED);
			}
			if (started && notEnded && hasCharges) {
				subscriptions.add(subscription);
			}
		});

		return subscriptions;
	}

	public Map<Subscription, Money> getSubscriptionsCharges(PersonalAccount personalAccount,
			Range<LocalDateTime> period) {
		Map<Subscription, Money> result = new HashMap<>();
		List<Subscription> subscriptions = getSubscriptions(personalAccount, period);
		subscriptions.forEach(subscription -> result.put(subscription, calcCharges(subscription, period)));
		return result;
	}

	public Money calcCharges(Subscription subscription, Range<LocalDateTime> period) {

		LocalDateTime now = LocalDateTime.now();

		if (!period.upperEndpoint().isAfter(now)) {
			return calcChargesForInvoicesStartedInPeriod(subscription, period);
		}

		if (!period.lowerEndpoint().isBefore(now)) {
			return calcChargesForChargingPeriodsStartedInPeriod(subscription, period);
		}

		if (period.contains(now)) {
			Money result = Money.ZERO;
			LocalDateTime dateFromForChargingPeriods = now;
			if (toLocalDateTime(subscription.getValidFrom()).isBefore(now)) {
				result = result.add(
						calcChargesForInvoicesStartedInPeriod(subscription, Range.closed(period.lowerEndpoint(), now)));
			} else {
				dateFromForChargingPeriods = period.lowerEndpoint();
			}

			if (subscription.getValidTo() == null || toLocalDateTime(subscription.getValidTo()).isAfter(now)) {
				result = result.add(calcChargesForChargingPeriodsStartedInPeriod(subscription,
						Range.closed(dateFromForChargingPeriods, period.upperEndpoint())));
			}
			return result;
		}

		throw new SystemException("Invalid period");
	}

	private static final String GET_INVOICES_STARTED_IN_PERIOD = "SubscriptionChargesService.getInvoicesStartedInPeriod";

	@NamedQuery(name = GET_INVOICES_STARTED_IN_PERIOD, query = "from LongTermInvoice i where exists (from InvoiceEntry e"
			+ " where e.invoice = i and subscription = :subscription) and startDate >= :periodStart and startDate < :periodEnd")
	private Money calcChargesForInvoicesStartedInPeriod(Subscription subscription, Range<LocalDateTime> period) {
		TypedQuery<LongTermInvoice> query = em.createNamedQuery(GET_INVOICES_STARTED_IN_PERIOD, LongTermInvoice.class);
		query.setParameter("subscription", subscription)
				.setParameter("periodStart", fromLocalDateTime(period.lowerEndpoint()))
				.setParameter("periodEnd", fromLocalDateTime(period.upperEndpoint()));
		List<LongTermInvoice> invoices = query.getResultList();
		Money result = Money.ZERO;
		for (LongTermInvoice invoice : invoices) {
			result = result.add(invoice.getTotalPrice());
		}

		return result;
	}

	private Money calcChargesForChargingPeriodsStartedInPeriod(Subscription subscription, Range<LocalDateTime> period) {
		Money result = Money.ZERO;
//		if (rulesService.hasCost(subscription)) {
//			RoundingPolicy roundingPolicy = subscription.getProvisionTerms().getRoundingPolicy();
//
//			Range<LocalDateTime> subsInPeriod = Range.closed(max(subscription.getValidFrom(), period.lowerEndpoint()),
//					subscription.getValidTo() == null ? period.upperEndpoint()
//							: min(subscription.getValidTo(), period.upperEndpoint()));
//			LocalDateTime poi = subsInPeriod.lowerEndpoint();
//			ChargingPeriod chargingPeriod = PeriodBuilderService.chargingOf(subscription, fromLocalDateTime(poi));
//			while (subsInPeriod.isConnected(chargingPeriod.boundaries())) {
//				if (period.contains(chargingPeriod.boundaries().lowerEndpoint())) {
//					// FIXME: убрать параметры opening & closing policy
//					CostInfo subsCost = accountingSvc.calculateCost(subscription,
//							chargingPeriod.toDateRange().lowerEndpoint(), null, null, roundingPolicy);
//					result = result.add(subsCost.getCost());
//				}
//				chargingPeriod = chargingPeriod.next();
//			}
//		}
		return result;
	}
}
