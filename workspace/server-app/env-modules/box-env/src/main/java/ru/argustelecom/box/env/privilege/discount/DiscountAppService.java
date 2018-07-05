package ru.argustelecom.box.env.privilege.discount;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.singletonList;
import static ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution.DISALLOWED;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResult;
import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceRepository;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.SubscriptionAccountingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.inf.service.ApplicationService;
import ru.argustelecom.system.inf.exception.BusinessException;

@ApplicationService
public class DiscountAppService implements Serializable {

	private static final long serialVersionUID = -7652394420234801340L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private DiscountRepository discountRp;

	@Inject
	private LongTermInvoiceRepository ir;

	@Inject
	private PersonalAccountBalanceService pabs;

	@Inject
	private SubscriptionAccountingService sas;

	public Discount create(Long subscriptionId, Date validFrom, Date validTo, BigDecimal rate) {
		checkNotNull(subscriptionId);
		checkNotNull(validFrom);
		checkNotNull(validTo);
		checkArgument(validFrom.before(validTo) || validFrom.equals(validTo));
		Discount.checkRate(rate);

		Subscription subscription = em.find(Subscription.class, subscriptionId);
		checkNotNull(subscription);

		Discount discount = discountRp.createDiscount(subscription, validFrom, validTo, rate);
		List<LongTermInvoice> invoices = ir.findLastInvoices(singletonList(subscription), false).stream()
				.filter(i -> isSamePeriod(i, discount)).collect(Collectors.toList());
		for (LongTermInvoice invoice : invoices) {
			InvoicePlan plan = sas.recalculateOnAddingDiscount(invoice, discount);
			invoice.applyPlan(plan);
		}

		return discount;
	}

	public Discount changeDiscount(Long discountId, Date validTo, BigDecimal rate) {
		checkNotNull(discountId);
		Discount.checkRate(rate);

		Discount discount = em.find(Discount.class, discountId);
		checkNotNull(discount);

		boolean validToChanged = !Objects.equals(discount.getValidTo(), validTo);
		boolean rateChanged = !Objects.equals(discount.getRate(), rate);

		if (validToChanged) {
			Date prevValidTo = discount.getValidTo();
			discountRp.changeDiscountValidToDate(discount, validTo);
			List<LongTermInvoice> invoices = ir
					.findLastInvoices(singletonList(discount.getSubscription()), false).stream()
					.filter(i -> isSamePeriod(i, discount)).collect(Collectors.toList());
			for (LongTermInvoice invoice : invoices) {
				if (invoice.inState(InvoiceState.CLOSED) && validTo.before(invoice.getEndDate())) {
					throw new BusinessException(String.format(
							"Со скидкой связан закрытый инвойс, поэтому окончание действия скидки не может быть раньше %s",
							validTo));
				}

				Range<Date> range = Range.closed(invoice.getStartDate(), invoice.getEndDate());
				if (range.contains(prevValidTo) || range.contains(discount.getValidTo())) {
					InvoicePlan newValidToDatePlan = sas.recalculate(invoice);
					checkBalance(invoice.getSubscription(), invoice.getPlan(), newValidToDatePlan);
					invoice.applyPlan(newValidToDatePlan);
				}
			}
		}
		if (rateChanged) {
			discount.setRate(rate);
			List<LongTermInvoice> invoices = ir
					.findLastInvoices(singletonList(discount.getSubscription()), false).stream()
					.filter(i -> isSamePeriod(i, discount)).collect(Collectors.toList());
			for (LongTermInvoice invoice : invoices) {
				InvoicePlan newRatePlan = sas.recalculate(invoice);
				checkBalance(invoice.getSubscription(), invoice.getPlan(), newRatePlan);
				invoice.applyPlan(newRatePlan);
			}
		}
		return discount;
	}

	public void removeDiscount(Long discountId) {
		checkNotNull(discountId);

		Discount discount = em.find(Discount.class, discountId);
		checkNotNull(discount);

		List<LongTermInvoice> invoices = ir.findInvoices(discount);
		for (LongTermInvoice invoice : invoices) {
			if (invoice.inState(InvoiceState.CLOSED)) {
				throw new BusinessException("Скидка не может быть удалена. Имеется закрытый выставленный счет");
			}
			InvoicePlan planWithoutDiscount = sas.recalculateOnRemovingDiscount(invoice, discount);
			checkBalance(invoice.getSubscription(), invoice.getPlan(), planWithoutDiscount);
			invoice.applyPlan(planWithoutDiscount);
		}

		em.remove(discount);
	}

	private void checkBalance(Subscription subscription, InvoicePlan oldPlan, InvoicePlan newPlan) {
		BalanceCheckingResult result = pabs.checkBalance(subscription, oldPlan, newPlan, true);
		if (result.getResolution() == DISALLOWED) {
			throw new BusinessException(String.format(
					"Скидка не может быть изменена. Недостаточно средств на лицевом счете (необходимо: %s, доступно: %s, порог отключения: %s)",
					result.getRequired(), result.getAvailable(), result.getThreshold()));
		}
	}

	public List<Discount> findDiscounts(Long subscriptionId) {
		checkNotNull(subscriptionId);

		Subscription subscription = em.find(Subscription.class, subscriptionId);
		checkNotNull(subscription);

		return discountRp.findDiscounts(subscription);
	}

	private boolean isSamePeriod(LongTermInvoice invoice, Discount discount) {
		return !invoice.inState(InvoiceState.CLOSED) && (invoice.getEndDate().after(discount.getValidFrom())
				|| invoice.getStartDate().before(discount.getValidTo()));
	}

}