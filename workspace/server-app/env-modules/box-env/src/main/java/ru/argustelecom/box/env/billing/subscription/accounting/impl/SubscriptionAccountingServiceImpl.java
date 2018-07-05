package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;
import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.period.PeriodBuilderService.chargingOf;
import static ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan.ascendingOrder;
import static ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan.intersectsWith;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlanner.nextPlannedEnd;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.chrono.DateUtils.after;
import static ru.argustelecom.system.inf.chrono.DateUtils.before;
import static ru.argustelecom.system.inf.chrono.DateUtils.equal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceRepository;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.SubscriptionAccountingService;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.privilege.PrivilegeRepository;
import ru.argustelecom.box.env.privilege.discount.DiscountRepository;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.inf.chrono.ChronoUtils;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class SubscriptionAccountingServiceImpl implements SubscriptionAccountingService {

	private static final Logger log = Logger.getLogger(SubscriptionAccountingServiceImpl.class);
	private static final long serialVersionUID = -5081466292309850556L;

	@Inject
	private LongTermInvoiceRepository invoiceRp;

	@Inject
	private PrivilegeRepository privilegeRp;

	@Inject
	private DiscountRepository discountRp;

	@Override
	public InvoicePlan calculateNextAccruals(Subscription subscription, Date calculationDate) {
		checkRequiredArgument(subscription, "subscription");
		LongTermInvoice lastInvoice = invoiceRp.findLastInvoice(subscription, true);
		return calculateNextAccruals(subscription, lastInvoice, calculationDate);
	}

	@Override
	public InvoicePlan calculateNextAccruals(Subscription subscription, LongTermInvoice lastInvoice,
			Date calculationDate) {

		checkRequiredArgument(calculationDate, "calculationDate");
		checkRequiredArgument(subscription, "subscription");

		InvoicePlannerConfig config = new InvoicePlannerConfig();
		config.setSubscription(subscription);
		config.setLastPlan(lastInvoice != null ? lastInvoice.getPlan() : null);
		config.setRenewalDate(calculationDate);
		config.setAllowPrimaryActivation(true);
		config.setRequireResult(true);

		InvoicePlanner planner = new InvoicePlanner(config);

		LocalDateTime poi = planner.plannedStartDate();
		if (poi == null) {
			return null;
		}

		ChargingPeriod cp = config.chargingPeriod(poi);
		findPrivileges(subscription, cp.startDate(), cp.endDate()).forEach(config::addPeriodModifier);
		findDiscounts(subscription, cp.startDate(), cp.endDate()).forEach(config::addPriceModifier);

		planner.updateConfiguration(config);
		List<InvoicePlan> resultPlans = planner.createFuturePlans(poi);

		// Период планирования не ограничен, поэтому не может быть ситуации, при которой дату начала планирования
		// смогли определить, а сам план не получили.
		checkState(!resultPlans.isEmpty());

		resultPlans.sort(ascendingOrder());
		return resultPlans.get(0);
	}

	@Override
	public Map<Subscription, List<InvoicePlan>> calculateBillAccruals(List<Subscription> subscriptions, Date startDate,
			Date endDate, Date renewalDate) {

		checkRequiredArgument(subscriptions, "subscriptions");
		checkRequiredArgument(renewalDate, "renewalDate");
		checkRequiredArgument(startDate, "startDate");
		checkRequiredArgument(endDate, "endDate");

		if (subscriptions.isEmpty()) {
			return emptyMap();
		}

		Map<Subscription, List<InvoicePlan>> past = restorePast(subscriptions, startDate, endDate);
		Map<Subscription, List<InvoicePlan>> future = calculateFuture(subscriptions, startDate, endDate, renewalDate);

		return mergeAndCut(subscriptions, past, future, startDate, endDate);
	}

	@Override
	public InvoicePlan recalculate(LongTermInvoice invoice) {
		checkRequiredArgument(invoice, "invoice");
		checkArgument(invoice.inState(asList(InvoiceState.CREATED, InvoiceState.ACTIVE)));

		return initializeBuilder(invoice).build();
	}

	@Override
	public InvoicePlan recalculateOnPrematureClosing(LongTermInvoice invoice, Date closeDate) {
		checkRequiredArgument(invoice, "invoice");
		checkRequiredArgument(closeDate, "closeDate");
		checkArgument(invoice.inState(asList(InvoiceState.CREATED, InvoiceState.ACTIVE)));

		if (equal(invoice.getEndDate(), closeDate)) {
			return null;
		}
		checkState(after(closeDate, invoice.getStartDate()));
		Date actualCloseDate = ChronoUtils.minDate(closeDate, invoice.getEndDate());

		InvoicePlanBuilder builder = initializeBuilder(invoice);
		builder.setPlannedEnd(actualCloseDate);

		return builder.build();
	}

	@Override
	public InvoicePlan recalculateOnPrivilegeExpansion(LongTermInvoice invoice, Privilege privilege) {
		checkRequiredArgument(invoice, "invoice");
		checkRequiredArgument(privilege, "privilege");
		checkArgument(invoice.inState(asList(InvoiceState.CREATED, InvoiceState.ACTIVE)));
		checkArgument(Objects.equals(invoice.getPrivilege(), privilege));

		if (equal(invoice.getEndDate(), privilege.getValidTo())) {
			return null;
		}
		checkState(after(privilege.getValidTo(), invoice.getEndDate()));

		LocalDateTime newPlannedEnd = nextPlannedEnd(chargingOf(invoice), invoice.getSubscription(), privilege);
		InvoicePlanBuilder builder = initializeBuilder(invoice);
		builder.setPlannedEnd(newPlannedEnd);

		return builder.build();
	}

	@Override
	public InvoicePlan recalculateOnAddingDiscount(LongTermInvoice invoice, Discount discount) {
		checkRequiredArgument(invoice, "invoice");
		checkRequiredArgument(discount, "discount");
		checkArgument(invoice.inState(asList(InvoiceState.CREATED, InvoiceState.ACTIVE)));

		InvoicePlanBuilder builder = initializeBuilder(invoice);
		builder.addPriceModifier(discount);

		return builder.build();
	}

	@Override
	public InvoicePlan recalculateOnRemovingDiscount(LongTermInvoice invoice, Discount discount) {
		checkRequiredArgument(invoice, "invoice");
		checkRequiredArgument(discount, "discount");
		checkArgument(invoice.inState(asList(InvoiceState.CREATED, InvoiceState.ACTIVE)));

		InvoicePlanBuilder builder = initializeBuilder(invoice);
		builder.removePriceModifier(discount);

		return builder.build();
	}

	// **************************************************************************************************************

	/**
	 * Восстанавливает планы по историческим данным для указанных подписок в пределах startDate и endDate. Результат
	 * группируется по подпискам. Метод предназначен для массового вычисления начислений во время генерации счетов
	 * 
	 * @param subs
	 *            - целевые подписки
	 * @param startDate
	 *            - дата начала интереса
	 * @param endDate
	 *            - дата окончания интереса
	 * 
	 * @return карту исторических планов в разрезе подписок
	 */
	Map<Subscription, List<InvoicePlan>> restorePast(List<Subscription> subs, Date startDate, Date endDate) {
		Map<Subscription, List<InvoicePlan>> result = new HashMap<>();
		Map<Subscription, List<LongTermInvoice>> past = invoiceRp.findInvoices(subs, startDate, endDate);

		for (Subscription subscription : subs) {
			List<LongTermInvoice> pastInvoices = past.get(subscription);
			if (pastInvoices == null || pastInvoices.isEmpty()) {
				result.put(subscription, emptyList());
				continue;
			}
			result.put(subscription, pastInvoices.stream().map(LongTermInvoice::getPlan).collect(toList()));
		}

		return result;
	}

	/**
	 * Расчитывает планы будущих начислений для указанных подписок в пределах startDate и endDate. Результат
	 * группируется по подпискам. Метод предназначен для массового вычисления начислений во время генерации счетов. Для
	 * расчета будущих начислений также используются привилегии и скидки, как если бы для каждой подписки вызывался
	 * метод {@link #calculateNextAccruals(Subscription, Date)}, однако этот метод оптимизирован для одновременной
	 * работы с большим количеством подписок и работает гораздо быстрее, нежели последовательный вызов единичного
	 * расчета
	 * 
	 * @param subs
	 *            - целевые подписки
	 * @param startDate
	 *            - дата начала интереса
	 * @param endDate
	 *            - дата окончания интереса
	 * @param renewalDate
	 *            - дата предполагаемого возобновления тарификации для приостановленных подписок
	 * 
	 * @return карту предрасчитанных планов за период в разрезе подписок
	 */
	Map<Subscription, List<InvoicePlan>> calculateFuture(List<Subscription> subs, Date startDate, Date endDate,
			Date renewalDate) {

		Map<Subscription, List<InvoicePlan>> result = new HashMap<>();
		Map<Subscription, List<LongTermInvoice>> invoicesAgg = findLastInvoices(subs);
		Map<Subscription, List<Discount>> discountsAgg = discountRp.findDiscounts(subs, startDate, endDate);
		Map<Subscription, List<Privilege>> privilegeAgg = privilegeRp.findPrivileges(subs, startDate, endDate);

		InvoicePlanner planner = new InvoicePlanner();
		InvoicePlannerConfig config = new InvoicePlannerConfig();
		config.setBoundaries(startDate, endDate);
		config.setRenewalDate(renewalDate);
		config.setAllowPrimaryActivation(false);
		config.setRequireResult(true);

		for (Subscription subscription : subs) {
			config.setSubscription(subscription);
			config.setLastPlan(null);
			config.cleanModifiers();

			List<LongTermInvoice> invoices = invoicesAgg.getOrDefault(subscription, emptyList());
			if (!invoices.isEmpty()) {
				if (invoices.size() != 1) {
					log.warnv("Для {0} найдено {1} инвойсов. BOX-2176 не исправлен!", subscription, invoices.size());
				}
				config.setLastPlan(invoices.get(0).getPlan());
			}

			privilegeAgg.getOrDefault(subscription, emptyList()).forEach(config::addPeriodModifier);
			discountsAgg.getOrDefault(subscription, emptyList()).forEach(config::addPriceModifier);

			planner.updateConfiguration(config);
			result.put(subscription, planner.createFuturePlans());
		}

		return result;
	}

	/**
	 * Объединяет результаты {@linkplain #restorePast(List, Date, Date) восстановления исторических данных} и
	 * {@link #calculateFuture(List, Date, Date, Date) расчета будущих начислений}, отфильтровывает планы, не входящие в
	 * период интереса и формирует сгруппированный по подпискам и строго упорядоченный по периодам итоговый результат
	 * планирования начислений в сценарии выставления счетов.
	 * 
	 * @param subscriptions
	 *            - целевые подписки
	 * @param past
	 *            - восстановленные исторические планы
	 * @param future
	 *            - расчитанные будущие планы
	 * @param startDate
	 *            - дата начала интререса
	 * @param endDate
	 *            - дата окончания интереса
	 * 
	 * @return итоговый группированный и упорядоченный результат
	 */
	Map<Subscription, List<InvoicePlan>> mergeAndCut(List<Subscription> subscriptions,
			Map<Subscription, List<InvoicePlan>> past, Map<Subscription, List<InvoicePlan>> future, Date startDate,
			Date endDate) {

		Map<Subscription, List<InvoicePlan>> result = new HashMap<>();

		for (Subscription subscription : subscriptions) {
			List<InvoicePlan> pastPlans = past.getOrDefault(subscription, emptyList());
			List<InvoicePlan> futurePlans = future.getOrDefault(subscription, emptyList());
			List<InvoicePlan> resultPlans = new ArrayList<>(pastPlans.size() + futurePlans.size());

			pastPlans.stream().filter(intersectsWith(startDate, endDate)).forEach(resultPlans::add);
			futurePlans.stream().filter(intersectsWith(startDate, endDate)).forEach(resultPlans::add);

			resultPlans.sort(ascendingOrder());
			result.put(subscription, resultPlans);
		}

		return result;
	}

	/**
	 * Инициализирует {@linkplain InvoicePlanBuilder билдер} инвойсов для пересчета указанного инвойса. Для этого в
	 * билдер копируются параметры инвойса, дополнительно находится предыдущий инвойс для возможного выравнивания по
	 * границам базовых единиц и недопущения повторной тарификации уже тарифицированной базовой единицы. После
	 * первоначальной подготовки билдера по шаблону, некоторые параметры могут быть переопределены, например, добавлена
	 * новая скидка, котору нужно учесть и т.д.
	 * 
	 * @param invoice
	 *            - инвойс для пересчета
	 * 
	 * @return подготовленный для расчета билдер.
	 */
	InvoicePlanBuilder initializeBuilder(LongTermInvoice invoice) {
		InvoicePlanBuilder result = new InvoicePlanBuilder();
		result.initFromInvoice(invoice);

		LongTermInvoice previousInvoice = invoiceRp.findPrevInvoice(invoice, true);
		if (previousInvoice != null) {
			result.setPreviousPlan(previousInvoice.getPlan());
		}

		return result;
	}

	//@formatter:off
	
	/**
	 * Находит последние инвойсы для указанных подписок
	 */
	Map<Subscription, List<LongTermInvoice>> findLastInvoices(List<Subscription> subscriptions) {
		List<LongTermInvoice> lastInvoicesRaw = invoiceRp.findLastInvoices(subscriptions, true);
		return lastInvoicesRaw.stream().collect(groupingBy(LongTermInvoice::getSubscription));
	}

	/**
	 * Для указанной подписки определяет скидки в пределах периода интереса  
	 */
	List<Discount> findDiscounts(Subscription subscription, Date startDate, Date endDate) {
		return discountRp
				.findDiscounts(singletonList(subscription), startDate, endDate)
				.getOrDefault(subscription, emptyList());
	}

	/**
	 * Для указанной подписки определяет привилегии в пределах периода интереса  
	 */
	List<Privilege> findPrivileges(Subscription subscription, Date startDate, Date endDate) {
		return privilegeRp
				.findPrivileges(singletonList(subscription), startDate, endDate)
				.getOrDefault(subscription, emptyList());
	}
}
