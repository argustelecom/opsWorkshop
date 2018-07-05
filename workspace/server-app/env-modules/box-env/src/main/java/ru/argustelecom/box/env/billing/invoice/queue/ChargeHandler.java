package ru.argustelecom.box.env.billing.invoice.queue;

import static ru.argustelecom.box.env.billing.period.PeriodBuilderService.chargingOf;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.invoice.UsageInvoiceRepository;
import ru.argustelecom.box.env.billing.invoice.UsageInvoiceSettingsRepository;
import ru.argustelecom.box.env.billing.invoice.lifecycle.UsageInvoiceLifecycle.Route;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings;
import ru.argustelecom.box.env.billing.mediation.AggregateCallsByUsageInvoiceQr;
import ru.argustelecom.box.env.billing.mediation.AssociateUsageInvoicesWithCallsService;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.stl.period.AccountingPeriod;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.queue.api.QueueStat;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

/**
 * Обработчик события {@linkplain ChargeContext тарификации фактов использования телефонии}. Если есть событие на
 * перетарификацию по данной услуге, то выполнение события должно быть отложено.
 */
@QueueHandlerBean
@Named(value = ChargeHandler.HANDLER_NAME)
public class ChargeHandler implements QueueHandler {

	public static final String HANDLER_NAME = "chargeHandler";

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private UsageInvoiceSettingsRepository usageInvoiceSettingsRp;

	@Inject
	private UsageInvoiceRepository usageInvoiceRp;

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private AssociateUsageInvoicesWithCallsService associateSrv;

	@Inject
	private LifecycleRoutingService routingSrv;

	@Inject
	private QueueProducer producer;

	@Inject
	private QueueStat queueStat;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) {
		ChargeContext context = event.getContext(ChargeContext.class);
		Service service = context.getService(em);

		if (hasActiveRechargeEvent(service)) {
			return QueueHandlingResult.REPEAT_5;
		}

		UsageInvoiceSettings settings = usageInvoiceSettingsRp.find();
		LocalDateTime now = LocalDateTime.now();

		usageInvoiceRp.createMissedUsageInvoices(service, now);

		List<UsageInvoice> activeInvoices = usageInvoiceRp.findActiveInvoices(service);
		associateInvoiceWithCalls(activeInvoices);

		Map<UsageInvoice, ProcessingProps> invoiceProcessingPropsMap = createProcessingProps(activeInvoices, settings);
		invoiceProcessingPropsMap.forEach((invoice, props) -> {
			if (props.needCloseInvoice(now)) {
				routingSrv.performRouting(invoice, Route.CLOSE);
			}
		});

		createNextEvent(service, settings);
		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) {
		return QueueErrorResult.RETRY_LATER;
	}

	public static String genQueueName(Service service) {
		return "CHARGE_" + new EntityConverter().convertToString(service);
	}

	/**
	 * Производит связывание инвойсов и фактов использования. Связывание происходит по следующим правилам.
	 * <p>
	 * Для инвоса с договором, должны быть совпадения по:
	 * <ul>
	 * <li>услуге</li>
	 * <li>поставщику</li>
	 * <li>зона телефонной нумерации есть в зонах опции</li>
	 * <li>дата вызова входит в период инвойса</li>
	 * </ul>
	 * </p>
	 * <p>
	 * Для инвойсов без договора, должны быть совпадения по:
	 * <ul>
	 * <li>услуге</li>
	 * <li>зона телефонной нумерации есть в зонах опции</li>
	 * <li>дата вызова входит в период инвойса</li>
	 * </ul>
	 * </p>
	 *
	 * @param invoices
	 *            список инвойсов, для которых необходимо выполнить связывание.
	 */
	private void associateInvoiceWithCalls(List<UsageInvoice> invoices) {
		List<AggregateCallsByUsageInvoiceQr> invoiceEntries = associateSrv.associate(invoices, null);
		Map<Long, List<AggregateCallsByUsageInvoiceQr>> invoiceEntriesMap = invoiceEntries.stream()
				.collect(Collectors.groupingBy(AggregateCallsByUsageInvoiceQr::getInvoiceId));

		invoiceEntriesMap.forEach((invoiceId, calls) -> {
			UsageInvoice invoice = em.find(UsageInvoice.class, invoiceId);
			invoice.removeAllEntries();
			associateSrv.addEntriesTo(invoice, calls);
		});
	}

	/**
	 * Создаёт следующие задание на тарификацию. Задание должно создаваться только при следующих условиях:
	 * <ul>
	 * <li>Услуга - активна</li>
	 * <li>У услуге есть активные инвойсы</li>
	 * </ul>
	 */
	private void createNextEvent(Service service, UsageInvoiceSettings settings) {
		boolean serviceIsActive = service.getState().equals(ServiceState.ACTIVE);
		boolean serviceHasActiveInvoices = !usageInvoiceRp.findActiveInvoices(service).isEmpty();

		if (!(serviceIsActive || serviceHasActiveInvoices)) {
			return;
		}

		ChargeContext eventCtx = new ChargeContext(service);
		String queueName = genQueueName(service);

		producer.remove(queueName);
		producer.schedule(queueName, null, QueueProducer.Priority.MEDIUM,
				settings.nextScheduledTime(LocalDateTime.now()), ChargeHandler.HANDLER_NAME, eventCtx);
	}

	/**
	 * Проверяет есть ли активное событие на перетарификацию фактов использования телефонии, по улсуге.
	 *
	 * @param service
	 *            услуга, для которой выполняется проверка.
	 * @return <strong>true</strong> если такое событие есть.
	 */
	private boolean hasActiveRechargeEvent(Service service) {
		String rechargeJobId = RechargeHandler.genQueueName(service);
		QueueStat.Statistic rechargeJobStat = queueStat.gatherByQueue(rechargeJobId);
		return !rechargeJobStat.isCompleted();
	}

	private Map<UsageInvoice, ProcessingProps> createProcessingProps(List<UsageInvoice> invoices,
			UsageInvoiceSettings settings) {

		Subscription subs = subscriptionRp.findSubscription(invoices.get(0).getService().getSubject());

		Map<UsageInvoice, ProcessingProps> invoiceProcessingPropsMap = new HashMap<>();

		invoices.forEach(invoice -> {
			//@formatter:off
			ProcessingProps props = new ProcessingProps(chargingOf(subs, invoice.getEndDate()));
			props.setClosingDate(determineClosingDate(props, settings));
			invoiceProcessingPropsMap.put(invoice, props);
			//@formatter:on
		});
		return invoiceProcessingPropsMap;
	}

	private LocalDateTime determineClosingDate(ProcessingProps props, UsageInvoiceSettings settings) {
		LocalDateTime periodUpperEndpoint = props.getClosingDate();
		switch (settings.getInvoicePeriodEnd()) {
		case ACCOUNTING_PERIOD_END:
			periodUpperEndpoint = props.getAccountPeriod().boundaries().upperEndpoint();
			break;
		case CHARGING_PERIOD_END:
			periodUpperEndpoint = props.getChargingPeriod().boundaries().upperEndpoint();
			break;
		}
		return periodUpperEndpoint.plus(settings.getCloseInvoiceUnit().amountOf(settings.getCloseInvoiceUnitAmount()));
	}

	/**
	 * Класс с настройками, для обработки закрытия инвойсов в зависимости от их настроек.
	 */
	@Getter
	private class ProcessingProps {

		private ChargingPeriod chargingPeriod;

		@Setter
		private LocalDateTime closingDate;

		ProcessingProps(ChargingPeriod chargingPeriod) {
			this.chargingPeriod = chargingPeriod;
		}

		AccountingPeriod getAccountPeriod() {
			return chargingPeriod.accountingPeriod();
		}

		boolean needCloseInvoice(LocalDateTime poi) {
			return DateUtils.after(fromLocalDateTime(poi), fromLocalDateTime(closingDate));
		}
	}

	private static final long serialVersionUID = -4737962235865467925L;

}