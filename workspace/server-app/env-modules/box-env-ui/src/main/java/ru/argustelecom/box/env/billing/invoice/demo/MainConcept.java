package ru.argustelecom.box.env.billing.invoice.demo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.extensions.event.timeline.TimelineSelectEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;

import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceDto;
import ru.argustelecom.box.env.billing.provision.model.PrematureActionPolicy;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms.RecurrentTermsBuilder;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.history.LifecycleHistoryService;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.AccountingPeriod;
import ru.argustelecom.box.env.stl.period.AbstractPeriod;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.stl.period.config.AbstractPeriodConfig;
import ru.argustelecom.system.inf.chrono.DateUtils;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "mainConcept")
@PresentationModel
public class MainConcept implements Serializable {

	@Inject
	private LifecycleHistoryService historySvc;

	private AccountingMode accountingMode;
	private DebugCalculatePeriods debugCalculatePeriods;

	private Date poi;
	private Date prematurePoi;

	private AccountingPeriod accountingPeriod;
	private Subscription subscription;

	private LongTermInvoiceDto currentInvoice;
	private LifecycleHistoryItem currentHistoryItem;

	private TimelineModel timeline = new TimelineModel();

	public void preRender(Subscription subscription, List<LongTermInvoiceDto> invoices, LifecycleObject<?> businessObject) {
		this.subscription = subscription;
		LineGroup.INVOICES.setInvoices(invoices);
		LineGroup.HISTORY.setLifecycleHistoryItems(historySvc.getHistory(businessObject));

		debugCalculatePeriods = new DebugCalculatePeriods(subscription);

		poi = new Date();

		initTimelineGroups();
		calculatePeriod();

		RequestContext.getCurrentInstance().execute("PF('lifecycleRoutingInfoDialog').hide()");
		RequestContext.getCurrentInstance().execute("PF('invoiceInfoDialog').hide()");
	}

	public void nextStep() {
		if (accountingMode.nextStep()) {
			updateTimeLine();
		}
	}

	public void previousStep() {
		if (accountingMode.previousStep()) {
			updateTimeLine();
		}
	}

	public void calculatePeriod() {
		CustomBillingPeriodConfig config = new CustomBillingPeriodConfig(convertDateTime(subscription.getCreationDate()),
				convertDateTime(subscription.getCloseDate()), subscription.getCost(), subscription.getProvisionTerms(),
				subscription.getAccountingDuration());

		accountingPeriod = AccountingPeriod.create(config.setPoi(convertDateTime(poi)));

		accountingMode = new AccountingMode(accountingPeriod, subscription.getCreationDate());

		addEventsToTimeline();
	}

	public void updateModel() {
		CustomBillingPeriodConfig config = new CustomBillingPeriodConfig(convertDateTime(subscription.getCreationDate()),
				convertDateTime(subscription.getCloseDate()), subscription.getCost(), subscription.getProvisionTerms(),
				subscription.getAccountingDuration());

		accountingPeriod = AccountingPeriod.create(config.setPoi(convertDateTime(poi)));

		accountingMode.updateModel(accountingPeriod);

		addEventsToTimeline();
	}

	private void initTimelineGroups() {
		for (LineGroup group : LineGroup.values()) {
			timeline.addGroup(new TimelineGroup(group.getGroupId(), group.getDesc()));
		}
	}

	private void addEventsToTimeline() {
		timeline.clear();

		for (AccountingPeriod accountingPeriod : accountingMode.getPeriods()) {
			for (LineGroup group : LineGroup.values()) {
				timeline.addAll(group.createEvents(accountingPeriod));
			}
		}
	}

	public void updateTimeLine() {
		addEventsToTimeline();
	}

	public void onSelect(TimelineSelectEvent e) {

		if (e.getTimelineEvent().getData() instanceof LifecycleHistoryItem) {
			currentHistoryItem = (LifecycleHistoryItem) e.getTimelineEvent().getData();

			RequestContext.getCurrentInstance().update("lifecycle_routing_info_dialog");
			RequestContext.getCurrentInstance().execute("PF('lifecycleRoutingInfoDialog').show()");
		}
		if (e.getTimelineEvent().getData() instanceof LongTermInvoiceDto) {
			currentInvoice = (LongTermInvoiceDto) e.getTimelineEvent().getData();

			RequestContext.getCurrentInstance().update("invoice_info_dialog");
			RequestContext.getCurrentInstance().execute("PF('invoiceInfoDialog').show()");
		}

	}

	private LocalDateTime convertDateTime(Date date) {
		return date != null ? LocalDateTime.ofInstant(date.toInstant(), TZ.getServerZoneId()) : null;
	}

	public String periodStart(AbstractPeriod period) {
		if (period != null) {
			Date d = Date.from(period.boundaries().lowerEndpoint().atZone(TZ.getServerZoneId()).toInstant());
			return DateUtils.format(d, "dd.MM.yyyy HH:mm:ss.SSS", TZ.getServerTimeZone());
		} else {
			return null;
		}
	}

	public String periodEnd(AbstractPeriod period) {
		if (period != null) {
			Date d = Date.from(period.boundaries().upperEndpoint().atZone(TZ.getServerZoneId()).toInstant());
			return DateUtils.format(d, "dd.MM.yyyy HH:mm:ss.SSS", TZ.getServerTimeZone());
		} else {
			return null;
		}
	}

	public Date getPoi() {
		return poi;
	}

	public void setPoi(Date currentDate) {
		poi = currentDate;
	}

	public Date getPrematurePoi() {
		return prematurePoi;
	}

	public void setPrematurePoi(Date prematurePoi) {
		this.prematurePoi = prematurePoi;
	}

	public AccountingPeriod getAccountingPeriod() {
		return accountingPeriod;
	}

	public void setAccountingPeriod(AccountingPeriod accountingPeriod) {
		this.accountingPeriod = accountingPeriod;
	}

	public TimelineModel getTimeline() {
		return timeline;
	}

	public LongTermInvoiceDto getCurrentInvoice() {
		return currentInvoice;
	}

	public AccountingMode getAccountingMode() {
		return accountingMode;
	}

	public LifecycleHistoryItem getCurrentHistoryItem() {
		return currentHistoryItem;
	}

	public DebugCalculatePeriods getDebugCalculatePeriods() {
		return debugCalculatePeriods;
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public class DebugCalculatePeriods {
		private Date subscriptionStart;
		private Date subscriptionEnd;

		private Money subscriptionCost;
		private Money prematureOpeningCost;
		private Money prematureClosingCost;

		private Integer accountingDurationAmount;
		private Integer chargingDurationAmount;

		private PeriodType periodType;
		private PeriodUnit accountingDurationUnit;
		private PeriodUnit chargingDurationUnit;

		private PrematureActionPolicy openingPolicy;
		private PrematureActionPolicy closingPolicy;

		public DebugCalculatePeriods(Subscription subscription) {
			subscriptionStart = subscription.getCreationDate();
			subscriptionEnd = subscription.getCloseDate();
			subscriptionCost = subscription.getCost();

			accountingDurationAmount = subscription.getAccountingDuration().getAmount();
			chargingDurationAmount = subscription.getProvisionTerms().getChargingDuration().getAmount();
			accountingDurationUnit = subscription.getAccountingDuration().getUnit();
			chargingDurationUnit = subscription.getProvisionTerms().getChargingDuration().getUnit();

			periodType = subscription.getProvisionTerms().getPeriodType();

			openingPolicy = PrematureActionPolicy.ACCOUNTING_PERIOD_COST;
			closingPolicy = PrematureActionPolicy.ACCOUNTING_PERIOD_COST;
		}

		public void calculatePeriod() {
			RecurrentTerms rt = new RecurrentTermsBuilder().withId(1L).setPeriodType(periodType)
					.setChargingDuration(PeriodDuration.of(chargingDurationAmount, chargingDurationUnit)).build();

			CustomBillingPeriodConfig config = new CustomBillingPeriodConfig(convertDateTime(subscriptionStart),
					convertDateTime(subscriptionEnd), subscriptionCost, rt,
					PeriodDuration.of(accountingDurationAmount, accountingDurationUnit));

			accountingPeriod = AccountingPeriod.create(config.setPoi(convertDateTime(poi)));

			accountingMode = new AccountingMode(accountingPeriod, subscriptionStart);

			updateTimeLine();
		}

		public PeriodType[] getPeriodTypes() {
			return PeriodType.values();
		}

		public PrematureActionPolicy[] getPrematureActionPolicies() {
			return PrematureActionPolicy.values();
		}

		public PeriodUnit[] getPeriodUnits() {
			return PeriodUnit.values();
		}

		public Money getPrematureOpeningCost() {
			return prematureOpeningCost;
		}

		public void setPrematureOpeningCost(Money prematureOpeningCost) {
			this.prematureOpeningCost = prematureOpeningCost;
		}

		public Money getPrematureClosingCost() {
			return prematureClosingCost;
		}

		public void setPrematureClosingCost(Money prematureClosingCost) {
			this.prematureClosingCost = prematureClosingCost;
		}

		public Integer getAccountingDurationAmount() {
			return accountingDurationAmount;
		}

		public void setAccountingDurationAmount(Integer accountingDurationAmount) {
			this.accountingDurationAmount = accountingDurationAmount;
		}

		public Integer getChargingDurationAmount() {
			return chargingDurationAmount;
		}

		public void setChargingDurationAmount(Integer chargingDurationAmount) {
			this.chargingDurationAmount = chargingDurationAmount;
		}

		public Date getSubscriptionStart() {
			return subscriptionStart;
		}

		public void setSubscriptionStart(Date subscriptionStart) {
			this.subscriptionStart = subscriptionStart;
		}

		public Date getSubscriptionEnd() {
			return subscriptionEnd;
		}

		public void setSubscriptionEnd(Date subscriptionEnd) {
			this.subscriptionEnd = subscriptionEnd;
		}

		public Money getSubscriptionCost() {
			return subscriptionCost;
		}

		public void setSubscriptionCost(Money subscriptionCost) {
			this.subscriptionCost = subscriptionCost;
		}

		public PeriodType getPeriodType() {
			return periodType;
		}

		public void setPeriodType(PeriodType periodType) {
			this.periodType = periodType;
		}

		public PeriodUnit getAccountingDurationUnit() {
			return accountingDurationUnit;
		}

		public void setAccountingDurationUnit(PeriodUnit accountingDurationUnit) {
			this.accountingDurationUnit = accountingDurationUnit;
		}

		public PeriodUnit getChargingDurationUnit() {
			return chargingDurationUnit;
		}

		public void setChargingDurationUnit(PeriodUnit chargingDurationUnit) {
			this.chargingDurationUnit = chargingDurationUnit;
		}

		public PrematureActionPolicy getOpeningPolicy() {
			return openingPolicy;
		}

		public void setOpeningPolicy(PrematureActionPolicy openingPolicy) {
			this.openingPolicy = openingPolicy;
		}

		public PrematureActionPolicy getClosingPolicy() {
			return closingPolicy;
		}

		public void setClosingPolicy(PrematureActionPolicy closingPolicy) {
			this.closingPolicy = closingPolicy;
		}
	}

	static class CustomBillingPeriodConfig extends AbstractPeriodConfig {
		private LocalDateTime subscriptionStart;
		private LocalDateTime subscriptionEnd;
		private Money subscriptionCost;
		private RecurrentTerms provisionTerms;
		private PeriodDuration billingPeriodDuration;

		public CustomBillingPeriodConfig(LocalDateTime subscriptionStart, LocalDateTime subscriptionEnd,
				Money subscriptionCost, RecurrentTerms provisionTerms, PeriodDuration billingPeriodDuration) {
			this.subscriptionStart = subscriptionStart;
			this.subscriptionEnd = subscriptionEnd;
			this.subscriptionCost = subscriptionCost;
			this.provisionTerms = provisionTerms;
			this.billingPeriodDuration = billingPeriodDuration;
		}

		public LocalDateTime getSubscriptionStart() {
			return this.subscriptionStart;
		}

		public LocalDateTime getSubscriptionEnd() {
			return subscriptionEnd;
		}

		public Money getSubscriptionCost() {
			return subscriptionCost;
		}

		public RecurrentTerms getProvisionTerms() {
			return provisionTerms;
		}

		@Override
		public PeriodDuration getAccountingDuration() {
			return billingPeriodDuration;
		}

		@Override
		public LocalDateTime getStartOfInterest() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public LocalDateTime getEndOfInterest() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Money getTotalCost() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PeriodType getPeriodType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public PeriodDuration getChargingDuration() {
			// TODO Auto-generated method stub
			return null;
		}
	}

	private static final long serialVersionUID = -4452913731182390840L;
}
