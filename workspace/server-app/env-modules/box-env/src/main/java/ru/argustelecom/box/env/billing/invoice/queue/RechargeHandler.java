package ru.argustelecom.box.env.billing.invoice.queue;

import static java.math.BigDecimal.ZERO;
import static ru.argustelecom.box.env.billing.invoice.lifecycle.RechargingChargeJobLifecycle.Route.ABORT;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.invoice.UsageInvoiceRepository;
import ru.argustelecom.box.env.billing.invoice.lifecycle.UsageInvoiceLifecycle.Route;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.mediation.AggregateCallsByUsageInvoiceQr;
import ru.argustelecom.box.env.billing.mediation.AssociateUsageInvoicesWithCallsService;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

/**
 * Обработчик события {@linkplain ChargeContext перетарификации фактов использования телефонии}.
 */
@QueueHandlerBean
@Named(value = RechargeHandler.HANDLER_NAME)
public class RechargeHandler implements QueueHandler {

	public static final String HANDLER_NAME = "rechargeHandler";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private UsageInvoiceRepository usageInvoiceRp;

	@Inject
	private TransactionRepository transactionRp;

	@Inject
	private AssociateUsageInvoicesWithCallsService associateSrv;

	@Inject
	private LifecycleRoutingService routingSrv;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) {
		RechargeContext context = event.getContext(RechargeContext.class);

		ChargeJob rechargeJob = context.getRechargeJob(em);
		Service service = context.getService(em);

		List<UsageInvoice> invoices = usageInvoiceRp.findInvoices(service, context.getMinDate(), context.getMaxDate());

		associateSrv.untieInvoicesFromCalls(invoices);
		associateSrv.markCallsLikeSuitable(rechargeJob);

		List<AggregateCallsByUsageInvoiceQr> associateResult = associateSrv.associate(invoices, rechargeJob);
		Map<UsageInvoice, List<AggregateCallsByUsageInvoiceQr>> invoiceEntriesMap = associateResult.stream()
				.collect(Collectors.groupingBy(this::findInvoice));

		invoiceEntriesMap.forEach((invoice, calls) -> {
			BigDecimal newTotalPrice = calls.stream().map(call -> call.getAmount()).reduce(ZERO, BigDecimal::add);

			boolean invoiceNotClosed = !InvoiceState.CLOSED.equals(invoice.getState());
			boolean samePrice = invoice.getTotalPrice().getAmount().compareTo(newTotalPrice) == 0;
			if (samePrice || invoiceNotClosed) {
				invoice.removeAllEntries();
				associateSrv.addEntriesTo(invoice, calls);
			} else {
				Date now = new Date();

				// 1. создать корректирующий документ
				// 2. создать Tx по коректирующему документу
				transactionRp.createCancelTransaction(invoice, now, rechargeJob.getId());

				// 3. перевести старый инвойс в состояние CANCELLED
				routingSrv.performRouting(invoice, Route.CANCEL);

				// 4. создать новый инвойс и положить внего информацию о фактах использования
				UsageInvoice newInvoice = usageInvoiceRp.copyInvoice(invoice);
				associateSrv.addEntriesTo(newInvoice, calls);
				associateSrv.tieNewInvoiceWithCalls(newInvoice, calls);

				// 5. закрытие инвойса + создание транзакции по новому инвойсу (выполняется в рамках закрытия)
				routingSrv.performRouting(newInvoice, Route.CLOSE);
			}
		});

		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) {
		if (error.getAttemptsCount() > 3) {
			ChargeJob rechargeJob = event.getContext(RechargeContext.class).getRechargeJob(em);
			routingSrv.performRouting(rechargeJob, ABORT);
			return QueueErrorResult.FAIL_QUEUE;
		}
		return QueueErrorResult.RETRY_LATER;
	}

	public static String genQueueName(Service service) {
		return "RECHARGE_" + new EntityConverter().convertToString(service);
	}

	private UsageInvoice findInvoice(AggregateCallsByUsageInvoiceQr qr) {
		return em.find(UsageInvoice.class, qr.getInvoiceId());
	}

	private static final long serialVersionUID = 6255051290143977644L;

}