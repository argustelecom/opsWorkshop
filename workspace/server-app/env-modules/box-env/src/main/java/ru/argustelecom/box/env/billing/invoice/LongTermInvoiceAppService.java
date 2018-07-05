package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class LongTermInvoiceAppService implements Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private LongTermInvoiceRepository invoiceRepository;

	public List<LongTermInvoice> findInvoicesForSubscription(Long subscriptionId) {
		Subscription subscription = em.find(Subscription.class, subscriptionId);
		return invoiceRepository.findInvoices(subscription);
	}

	public boolean doesDiscountHaveInvoices(Long discountId) {
		checkNotNull(discountId);

		Discount discount = em.find(Discount.class, discountId);
		checkNotNull(discount);

		return invoiceRepository.findLastClosedInvoice(discount.getSubscription()) != null;
	}

	public LongTermInvoice findLastInvoice(Long subscriptionId) {
		checkNotNull(subscriptionId);

		Subscription subscription = em.find(Subscription.class, subscriptionId);
		checkNotNull(subscription);

		return invoiceRepository.findLastInvoice(subscription, false);
	}

	public LongTermInvoice findLastClosedInvoice(Long subscriptionId) {
		checkNotNull(subscriptionId);

		Subscription subscription = em.find(Subscription.class, subscriptionId);
		checkNotNull(subscription);

		return invoiceRepository.findLastClosedInvoice(subscription);
	}

	private static final long serialVersionUID = -7315730227074310034L;

}
