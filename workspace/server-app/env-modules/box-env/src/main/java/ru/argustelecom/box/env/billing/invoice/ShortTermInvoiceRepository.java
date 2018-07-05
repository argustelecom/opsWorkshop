package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.stripToEmpty;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.ACTIVE;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.CLOSED;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.chrono.DateUtils.before;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceEntry;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice.LongTermInvoiceQuery;
import ru.argustelecom.box.env.billing.invoice.model.ShortTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.pricing.model.MeasuredProductOffering;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQueries;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.BusinessException;

@Repository
public class ShortTermInvoiceRepository implements Serializable {

	private static final long serialVersionUID = 5267372621355297086L;

	private static final String NOT_VALID_ENTRY_FOR_SHORT_TERM_INVOICE = "Данный тип инвойса не может содержать услуги/товары предоставляемые по подписке";

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private TransactionRepository transactionRp;

	// @formatter:off
	
	/**
	 * Создает и сразу закрывает (т.е. генерирует транзакцию, изменяющую баланс лицевого счета) короткоживущий инвойс
	 * для указанного продуктового предложения. Инвойс создается с привязкой к указанному лицевому счету
	 * 
	 * @param personalAccount
	 *            - лицевой счет, на котором необходимо создать инвойс
	 * @param offering
	 *            - продуктовое предложение для включения в инвойс
	 * 
	 * @return созданный и инвойс в закрытом состоянии и привязанный к транзакции
	 */
	public ShortTermInvoice createInvoice(PersonalAccount personalAccount, MeasuredProductOffering offering) {
		return createInvoice(personalAccount, singletonList(offering));
	}

	/**
	 * Создает и сразу закрывает (т.е. генерирует транзакцию, изменяющую баланс лицевого счета) короткоживущий инвойс
	 * для указанного списка продуктовых предложении. Инвойс создается с привязкой к указанному лицевому счету
	 * 
	 * @param personalAccount
	 *            - лицевой счет, на котором необходимо создать инвойс
	 * @param entries
	 *            - список продуктовых предложений для включения в инвойс
	 * 
	 * @return созданный и инвойс в закрытом состоянии и привязанный к транзакции
	 */
	public ShortTermInvoice createInvoice(PersonalAccount personalAccount,
			Collection<MeasuredProductOffering> entries) {
		checkArgument(entries != null && !entries.isEmpty());

		ShortTermInvoice instance = ShortTermInvoice.builder()
			.id(idSequence.nextValue(ShortTermInvoice.class))
			.personalAccount(personalAccount)
			.build();		

		for (MeasuredProductOffering offering : entries) {
			if (offering.getProvisionTerms().isRecurrent()) {
				throw new BusinessException(NOT_VALID_ENTRY_FOR_SHORT_TERM_INVOICE);
			}
			InvoiceEntry invoiceEntry = instance.createEntry(idSequence.nextValue(InvoiceEntry.class), offering);
			invoiceEntry.setAmount(offering.getPrice());
		}

		// Короткий инвойс не поддерживает жизненный цикл, нужно руками проставить ему состояние "закрыто"
		instance.setState(CLOSED);
		instance.onStateChanged(ACTIVE, CLOSED);

		em.persist(instance);

		Transaction transaction = transactionRp.createShortTermInvoiceTransaction(instance);
		instance.joinTransaction(transaction);

		return instance;
	}
}