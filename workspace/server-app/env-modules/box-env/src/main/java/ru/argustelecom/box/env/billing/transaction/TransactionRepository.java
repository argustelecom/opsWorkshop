package ru.argustelecom.box.env.billing.transaction;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.CancelReasonRepository;
import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.CancelReason;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.reason.model.InvoiceReason;
import ru.argustelecom.box.env.billing.reason.model.UserReason;
import ru.argustelecom.box.env.billing.reason.model.UserReasonType;
import ru.argustelecom.box.env.billing.transaction.event.TransactionCompletedEvent;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.billing.transaction.model.Transaction.TransactionQuery;
import ru.argustelecom.box.env.billing.transaction.model.TransactionReason;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.saldo.imp.model.PaymentDocReason;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@DomainService
public class TransactionRepository implements Serializable {

	private static final long serialVersionUID = 8987103860126513940L;

	private static final String FIND_SAME_TRANSACTIONS = "TransactionRepository.findSameTransactions";
	private static final String PAYMENT_DOC_ID_TEMPLATE = "%s_%s_%s_%s";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequenceService;

	@Inject
	private CancelReasonRepository cancelReasonRp;

	@Inject
	private Event<TransactionCompletedEvent> transactionCompletedEvent;

	public Transaction createUserTransaction(PersonalAccount personalAccount, Money amount, UserReasonType reasonType,
			String reasonNumber) {
		UserReason reason = new UserReason(idSequenceService.nextValue(TransactionReason.class), reasonType,
				reasonNumber);
		return createTransaction(personalAccount, amount, reason, null);
	}

	public Transaction createLongTermInvoiceTransaction(LongTermInvoice invoice) {
		return createInvoiceTransaction(invoice, invoice.getEndDate());
	}

	public Transaction createShortTermInvoiceTransaction(ShortTermInvoice invoice) {
		return createInvoiceTransaction(invoice, invoice.getCreationDate());
	}

	public Transaction createUsageInvoiceTransaction(UsageInvoice invoice) {
		return createInvoiceTransaction(invoice, invoice.getCreationDate());
	}

	public Transaction createPaymentDocTransaction(PersonalAccount personalAccount, String paymentDocId, Money amount,
			String paymentDocNumber, Date paymentDocDate, String paymentDocSource) {
		Long id = idSequenceService.nextValue(TransactionReason.class);

		String actualPaymentDocId = paymentDocId;
		if (actualPaymentDocId == null) {
			actualPaymentDocId = generatePaymentDocId(personalAccount.getId(), paymentDocNumber, paymentDocDate,
					amount);
		}

		PaymentDocReason reason = new PaymentDocReason(id, actualPaymentDocId, paymentDocNumber, paymentDocDate,
				paymentDocSource);

		return createTransaction(personalAccount, amount, reason, null);
	}

	public Transaction createCancelTransaction(UsageInvoice invoice, Date canceledDate, Long rechargeJobId) {
		checkNotNull(invoice);
		checkNotNull(canceledDate);
		checkNotNull(rechargeJobId);

		Money txSum = invoice.getTotalPrice();
		CancelReason cancelReason = cancelReasonRp.create(canceledDate, invoice, rechargeJobId);

		return createTransaction(invoice.getPersonalAccount(), txSum, cancelReason, canceledDate);
	}

	public List<Transaction> findTransactions(PersonalAccount personalAccount) {
		if (personalAccount == null)
			return Collections.emptyList();

		TransactionQuery query = new TransactionQuery();
		query.and(query.personalAccount().equal(personalAccount));
		return query.getResultList(em);
	}

	public List<Transaction> findTransactions(PersonalAccount personalAccount, Date startBound, Date endBound) {
		if (personalAccount == null)
			return Collections.emptyList();

		TransactionQuery query = new TransactionQuery();

		//@formatter:off
		query.and(
			query.personalAccount().equal(personalAccount),
			query.transactionDate().between(startBound, endBound)
		);
		//@formatter:on

		return query.getResultList(em);
	}

	public String generatePaymentDocId(Long personalAccountId, String paymentDocNumber, Date paymentDocDate,
			Money sum) {
		checkNotNull(personalAccountId);
		checkNotNull(paymentDocNumber);
		checkNotNull(paymentDocDate);
		checkNotNull(sum);

		return String.format(PAYMENT_DOC_ID_TEMPLATE, personalAccountId, paymentDocNumber, paymentDocDate, sum);
	}

	@NamedQuery(name = FIND_SAME_TRANSACTIONS, query = "select tr.paymentDocId from PaymentDocReason tr where tr.paymentDocId in (:paymentDocIds)")
	public List<String> findSameTransactions(List<String> paymentDocIds) {
		return em.createNamedQuery(FIND_SAME_TRANSACTIONS, String.class).setParameter("paymentDocIds", paymentDocIds)
				.getResultList();
	}

	private Transaction createInvoiceTransaction(AbstractInvoice invoice, Date date) {
		checkRequiredArgument(invoice, "invoice");

		Money invoiceCost = invoice.getTotalPrice();
		Money txSum = invoiceCost.isNegative() ? invoiceCost : invoiceCost.negate();

		InvoiceReason reason = new InvoiceReason(idSequenceService.nextValue(TransactionReason.class), invoice);
		return createTransaction(invoice.getPersonalAccount(), txSum, reason, date);
	}

	private Transaction createTransaction(PersonalAccount personalAccount, Money amount, TransactionReason reason,
			Date businessDate) {
		Transaction transaction = new Transaction(personalAccount, reason, amount, businessDate);
		em.persist(transaction);
		transactionCompletedEvent.fire(new TransactionCompletedEvent(transaction));
		return transaction;
	}

}