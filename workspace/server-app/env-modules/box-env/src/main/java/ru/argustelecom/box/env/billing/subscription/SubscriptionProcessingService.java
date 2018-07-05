package ru.argustelecom.box.env.billing.subscription;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.env.billing.subscription.queue.SubscriptionProcessingHandler.HANDLER_NAME;
import static ru.argustelecom.box.env.billing.subscription.queue.SubscriptionProcessingHandler.genQueueName;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.MEDIUM;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.chrono.DateUtils.after;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution;
import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResult;
import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceRepository;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.SubscriptionAccountingService;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionRoutingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.queue.SubscriptionContext;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.privilege.PrivilegeChanged;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class SubscriptionProcessingService implements Serializable {

	private static final long serialVersionUID = -2389330171616307113L;
	private static final Logger log = Logger.getLogger(SubscriptionProcessingService.class);

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private SubscriptionAccountingService accountingSvc;

	@Inject
	private PersonalAccountBalanceService balanceSvc;

	@Inject
	private LongTermInvoiceRepository invoiceRp;

	@Inject
	private SubscriptionRoutingService routingSvc;

	@Inject
	private LifecycleRoutingService lifecycleSvc;

	@Inject
	private QueueProducer producer;

	@Inject
	@PrivilegeChanged
	private Event<Privilege> privilegeChanged;

	/**
	 * 
	 * @param subscription
	 */
	public void lock(Subscription subscription) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.lock.timeout", 5 * 60 * 1000);
		em.lock(subscription, LockModeType.PESSIMISTIC_WRITE, properties);
	}

	/**
	 * Выполняет проверку баланса лицевого счета в момент перехода подписки в новое состояние.
	 */
	public BalanceCheckingResult checkBalanceOnRouting(Subscription subscription, SubscriptionState toState,
			Date routingDate, boolean lockAccount) {
		log.debugv("Checking personal account balance for {0} on routing to {1}", subscription, toState);

		InvoicePlan invoicePlan = accountingSvc.calculateNextAccruals(subscription, routingDate);
		return balanceSvc.checkBalance(subscription, invoicePlan, lockAccount);
	}

	/**
	 * 
	 * @param subscription
	 * @param toState
	 * @param routingDate
	 */
	public void openInvoiceOnRouting(Subscription subscription, SubscriptionState toState, Date routingDate) {
		checkRequiredArgument(subscription, "subscription");
		checkRequiredArgument(toState, "destinationState");
		checkArgument(toState.isChargeable());

		InvoicePlan invoicePlan = accountingSvc.calculateNextAccruals(subscription, routingDate);
		LongTermInvoice invoice = invoiceRp.createInvoice(subscription, invoicePlan);
		lifecycleSvc.performRouting(invoice, InvoiceState.ACTIVE, true);
		scheduleProcessingEvent(subscription, invoice.getEndDate());
	}

	/**
	 * 
	 * @param subscription
	 * @param toState
	 * @param routingDate
	 */
	public void closeInvoiceOnRouting(Subscription subscription, SubscriptionState toState, Date routingDate) {
		LongTermInvoice invoice = findInvoiceForProcessing(subscription, routingDate, false);
		if (invoice == null) {
			log.warnv("Invoice for {0} on routing to {1} is not found", subscription, toState);
			return;
		}

		// Залоггировать

		InvoicePlan closingPlan = accountingSvc.recalculateOnPrematureClosing(invoice, routingDate);
		invoice.applyPlan(closingPlan);

		lifecycleSvc.performRouting(invoice, InvoiceState.CLOSED, true);
		cancelProcessingEvent(subscription);
	}

	/**
	 * Осуществляет закрытие привилегии, а также:
	 * <ul>
	 * <li>досрочное закрытие инвойсов по подпискам согласно выбранному контексту ДП</li>
	 * <li>принимает решение по дальнейшей обработке подписок согласно конфигурации условий предоставления (ничего не
	 * делать/приостановить/вогнать в задолженность если приостановка не предусмотрена ЖЦ)</li>
	 * </ul>
	 */
	public void closePrivilege(Privilege privilege) {
		checkRequiredArgument(privilege, "privilege");

		Date closureDate = new Date();
		if (after(privilege.getValidFrom(), closureDate)) {
			em.remove(privilege);
			return;
		}

		privilege.close();

		List<LongTermInvoice> invoices = invoiceRp.findInvoices(privilege);
		for (LongTermInvoice invoice : invoices) {
			Subscription subscription = invoice.getSubscription();
			InvoicePlan closurePlan = accountingSvc.recalculateOnPrematureClosing(invoice, closureDate);
			invoice.applyPlan(closurePlan);

			cancelProcessingEvent(subscription);
			processSubscription(subscription, invoice, closureDate);
		}

		privilegeChanged.fire(privilege);
	}

	public void extendPrivilege(Privilege privilege, Date newValidTo) {
		checkRequiredArgument(privilege, "privilege");

		privilege.extend(newValidTo);

		List<LongTermInvoice> invoices = invoiceRp.findInvoices(privilege);
		for (LongTermInvoice invoice : invoices) {
			Subscription subscription = invoice.getSubscription();
			InvoicePlan extensionPlan = accountingSvc.recalculateOnPrivilegeExpansion(invoice, privilege);
			invoice.applyPlan(extensionPlan);

			cancelProcessingEvent(subscription);
			scheduleProcessingEvent(subscription, invoice.getEndDate());
		}

		privilegeChanged.fire(privilege);
	}

	/**
	 * 
	 * @param subscription
	 * @param processingDate
	 * @return
	 */
	public boolean processSubscription(Subscription subscription, Date processingDate) {
		checkRequiredArgument(subscription, "subscription");
		checkRequiredArgument(processingDate, "processingDate");

		LongTermInvoice currentInvoice = findInvoiceForProcessing(subscription, processingDate, true);
		if (currentInvoice == null) {
			log.warnv("Invoice for regular processing of {0} is not found", subscription);
			return false;
		}

		return processSubscription(subscription, currentInvoice, processingDate);
	}

	/**
	 * 
	 * @param subscription
	 * @param currentInvoice
	 * @param processingDate
	 * @return
	 */
	public boolean processSubscription(Subscription subscription, LongTermInvoice currentInvoice, Date processingDate) {
		checkRequiredArgument(subscription, "subscription");
		checkRequiredArgument(currentInvoice, "currentInvoice");
		checkRequiredArgument(processingDate, "processingDate");

		// Закрываем текущий инвойс. Здесь нам совсем все равно, будем ли мы уходить в минуса или нет
		lifecycleSvc.performRouting(currentInvoice, InvoiceState.CLOSED, true);

		// Расчитаем следующий инвойс по всем правилам, с поиском доверительных периодов, скидок, определением
		// лучшей даты для закрытия и т.д. На этот момент проверка баланса еще не выполнялась, мы просто получаем
		// план для следующего периода
		InvoicePlan nextInvoicePlan = accountingSvc.calculateNextAccruals(subscription, currentInvoice, processingDate);
		if (nextInvoicePlan == null) {
			log.warnv("Have no next invoice plan for regular processing of {0}", subscription);
			return true;
		}

		BalanceCheckingResult bcr = balanceSvc.checkBalance(subscription, nextInvoicePlan, true);
		// Если в условиях предоставления стоит флаг "закрывать вручную", то мы не можем закрыть
		// подписку за неуплату
		if (bcr.getResolution() == BalanceCheckingResolution.DISALLOWED
				&& !subscription.getProvisionTerms().isManualControl()) {
			// Проверка баланса не пройдена. Нужно приостановить подписку за неуплату и мы действительно можем это
			// сделать, потому что нет никаких доверительных периодов, и, самое главное, жизненный цикл подписки это
			// позволяет сделать.

			routingSvc.suspendForDebt(subscription);
		} else {
			if (bcr.getResolution() == BalanceCheckingResolution.ALLOWED_WITH_DEBT) {
				// Проверка баланса условно не пройдена. Однако, необходимо продолжить тарификацию
				// LOG
			}

			LongTermInvoice invoice = invoiceRp.createInvoice(subscription, nextInvoicePlan);
			lifecycleSvc.performRouting(invoice, InvoiceState.ACTIVE, true);
			scheduleProcessingEvent(subscription, invoice.getEndDate());
		}

		return true;
	}

	/**
	 * 
	 * @param subscription
	 * @param processingDate
	 * @param checkClosingDate
	 * @return
	 */
	private LongTermInvoice findInvoiceForProcessing(Subscription subscription, Date processingDate,
			boolean checkClosingDate) {

		// По текущим правилам биллинга инвойс может быть только один либо может отсутствовать совсем
		LongTermInvoice invoice = invoiceRp.findLastInvoice(subscription, false);

		// Если инвойса нет, то и делать дальше ничего не нужно
		if (invoice == null || !invoice.inState(InvoiceState.ACTIVE)) {
			log.warnv("Could not found opened invoice for {0}. Processing cancelled", subscription);
			return null;
		}

		// Если по какой-то причине мы вызвали этот метод (например, не только из очереди, но и напрямую из прикладного
		// кода), то нужно гарантировать, что закрытие инвойса состоится не раньше, чем это указано в его сроках
		// действия
		if (checkClosingDate && after(invoice.getEndDate(), processingDate)) {
			log.warnv("{0} must closed not earlier than {1}. Processing cancelled", invoice, invoice.getEndDate());
			return null;
		}

		return invoice;
	}

	/**
	 * 
	 * @param subscription
	 */
	private void cancelProcessingEvent(Subscription subscription) {
		producer.remove(genQueueName(subscription));
	}

	/**
	 * 
	 * @param subscription
	 * @param scheduledTime
	 */
	private void scheduleProcessingEvent(Subscription subscription, Date scheduledTime) {
		String queueName = genQueueName(subscription);
		producer.remove(queueName);
		producer.schedule(queueName, null, MEDIUM, scheduledTime, HANDLER_NAME, new SubscriptionContext(subscription));
	}
}
