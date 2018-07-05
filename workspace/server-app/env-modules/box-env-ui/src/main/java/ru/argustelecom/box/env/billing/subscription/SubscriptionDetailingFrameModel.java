package ru.argustelecom.box.env.billing.subscription;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.MILLIS;
import static ru.argustelecom.box.env.billing.period.PeriodBuilderService.accountingOf;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.min;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.temporal.TemporalAmount;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import javax.inject.Named;

import org.primefaces.extensions.event.timeline.TimelineSelectEvent;
import org.primefaces.extensions.model.timeline.TimelineEvent;
import org.primefaces.extensions.model.timeline.TimelineGroup;
import org.primefaces.extensions.model.timeline.TimelineModel;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.stl.period.AbstractPeriod;
import ru.argustelecom.box.env.stl.period.AccountingPeriod;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
@Named("subscriptionDetailingFm")
public class SubscriptionDetailingFrameModel implements Serializable {
	private static final long serialVersionUID = -3579549551518391426L;
	private static final int MAX_OFFSET_COUNT = 3;

	private transient Subscription subscription;
	private transient LocalDateTime startOfInterest;
	private transient LocalDateTime endOfInterest;
	private transient AccountingPeriod cursor;
	private transient LinkedList<TimelineCache> cache = new LinkedList<>();

	@Getter
	private TimelineModel timeline = new TimelineModel();

	public void preRender(Subscription subscription) {
		if (!Objects.equals(this.subscription, subscription)) {
			this.subscription = subscription;
			defineTimelineLimits();
			createTimeline();
		}
	}

	public Date getStartOfInterest() {
		return startOfInterest != null ? fromLocalDateTime(startOfInterest) : null;
	}

	public Date getEndOfInterest() {
		return endOfInterest != null ? fromLocalDateTime(endOfInterest) : null;
	}

	public Date getTimelineStart() {
		return cache != null && !cache.isEmpty() ? cache.getFirst().getPeriod().startDate() : null;
	}

	public Date getTimelineEnd() {
		return cache != null && !cache.isEmpty() ? cache.getLast().getPeriod().endDate() : null;
	}

	public Long getTimelineMinZoom() {
		return cursor != null ? MILLIS.between(cursor.startDateTime(), cursor.endDateTime()) + 1 : null;
	}

	public boolean canGoPrev() {
		return cursor != null && canGoPrev(cursor);
	}

	public boolean canGoNext() {
		return cursor != null && canGoNext(cursor);
	}

	public void goPrev() {
		if (canGoPrev()) {
			setCursor(cursor.prev());
			createTimeline();
		}
	}

	public void goNext() {
		if (canGoNext()) {
			setCursor(cursor.next());
			createTimeline();
		}
	}

	public void onTimelineSelectEvent(TimelineSelectEvent e) {
		// TODO
	}

	// ================================================================================================================

	private boolean canGoPrev(AccountingPeriod cursor) {
		return !cursor.contains(startOfInterest) || cursor.startDateTime().isAfter(startOfInterest);
	}

	private boolean canGoNext(AccountingPeriod cursor) {
		return !cursor.contains(endOfInterest) || cursor.endDateTime().isBefore(endOfInterest);
	}

	private void defineTimelineLimits() {
		AccountingPeriod period;
		LocalDateTime now = LocalDateTime.now();

		startOfInterest = toLocalDateTime(subscription.getValidFrom());
		if (subscription.getValidTo() == null) {
			PeriodDuration duration = subscription.getAccountingDuration();
			TemporalAmount offset = duration.getUnit().amountOf(duration.getAmount() * MAX_OFFSET_COUNT);

			period = accountingOf(subscription, now);
			endOfInterest = period.endDateTime().plus(offset);
		} else {
			period = accountingOf(subscription, min(subscription.getValidTo(), now));
			endOfInterest = toLocalDateTime(subscription.getValidTo());
		}

		setCursor(period);
	}

	private void setCursor(AccountingPeriod cursor) {
		LinkedList<TimelineCache> newCache = new LinkedList<>();

		if (canGoPrev(cursor)) {
			newCache.add(findOrCreateCacheItem(cursor.prev()));
		}

		newCache.add(findOrCreateCacheItem(cursor));

		if (canGoNext(cursor)) {
			newCache.add(findOrCreateCacheItem(cursor.next()));
		}

		this.cursor = cursor;
		this.cache = newCache;
	}

	private TimelineCache findOrCreateCacheItem(AccountingPeriod period) {
		TimelineCache cacheItem = null;
		for (TimelineCache it : cache) {
			if (Objects.equals(it.getPeriod(), period)) {
				cacheItem = it;
				break;
			}
		}

		//@formatter:off
		if (cacheItem == null) {
			// FIXME anton
//			cacheItem = new TimelineCache(period,
//				accountingSvc.createInvoicePlans(subscription, period.startDate(), period.endDate())
//			);
		}
		//@formatter:on

		return checkNotNull(cacheItem);
	}

	private void createTimeline() {
		checkState(cursor != null);
		checkState(cache != null && !cache.isEmpty());

		timeline.clear();
		createTimelineGroups();
		createTimelineEvents();
	}

	private void createTimelineGroups() {
		for (TimelineSwimlane swimlane : TimelineSwimlane.values()) {
			timeline.addGroup(new TimelineGroup(swimlane.getGroupId(), swimlane.getDesc()));
		}
	}

	private void createTimelineEvents() {
		List<TimelineEvent> events = new ArrayList<>();
		for (TimelineSwimlane swimlane : TimelineSwimlane.values()) {
			events.clear();
			for (TimelineCache cacheItem : cache) {
				swimlane.createEvents(cacheItem, events);
			}
			timeline.addAll(events);
		}
	}

	@Getter
	@AllArgsConstructor
	static class TimelineCache {
		private AccountingPeriod period;
		private List<InvoicePlan> plans;
	}

	@Getter
	@AllArgsConstructor
	static class TimelineEventPayload {

		private String caption;
		private Object data;

		public boolean isPeriod() {
			return data instanceof AbstractPeriod;
		}

		public boolean isAccountingPeriod() {
			return data instanceof AccountingPeriod;
		}

		public boolean isChargingPeriod() {
			return data instanceof ChargingPeriod;
		}

		public boolean isInvoicePlan() {
			return data instanceof InvoicePlan;
		}

		public AbstractPeriod toPeriod() {
			checkState(isPeriod());
			return (AbstractPeriod) data;
		}

		public AccountingPeriod toAccountingPeriod() {
			checkState(isAccountingPeriod());
			return (AccountingPeriod) data;
		}

		public ChargingPeriod toChargingPeriod() {
			checkState(isChargingPeriod());
			return (ChargingPeriod) data;
		}

		public InvoicePlan toInvoicePlan() {
			checkState(isInvoicePlan());
			return (InvoicePlan) data;
		}

		@Override
		public String toString() {
			return caption;
		}
	}

	enum TimelineSwimlane {

		//@formatter:off
		
		ACCOUNTING_PERIOD("accountingPeriodsGroup", "Периоды расчета", "accountingPeriodsStyle") {
			@Override
			public void createEvents(TimelineCache cacheItem, List<TimelineEvent> createdEvents) {
				AccountingPeriod accountingPeriod = cacheItem.getPeriod();
				String caption = LocaleUtils.format("Длительность: {0} {1}; Стоимость: {2}",
					accountingPeriod.baseUnitCount(),
					accountingPeriod.getType().getBaseUnit(),
					accountingPeriod.cost()
				);
				createdEvents.add(new TimelineEvent(
					new TimelineEventPayload(caption, accountingPeriod),
					accountingPeriod.startDate(),
					accountingPeriod.endDate(),
					false,
					getGroupId(),
					getGroupStyle()
				));
			}
		},

		CHARGING_PERIOD("chargingPeriodsGroup", "Периоды списания", "accountingPeriodsStyle") {
			@Override
			public void createEvents(TimelineCache cacheItem, List<TimelineEvent> createdEvents) {
				for (ChargingPeriod chargingPeriod : cacheItem.getPeriod().chargingPeriods()) {
					String caption = LocaleUtils.format("{0} руб.", chargingPeriod.cost());
					createdEvents.add(new TimelineEvent(
						new TimelineEventPayload(caption, chargingPeriod),
						chargingPeriod.startDate(),
						chargingPeriod.endDate(),
						false,
						getGroupId(),
						getGroupStyle()
					));
				}
			}
		},

		INVOICE_PLANNED_PERIOD("invoicesPlannedGroup", "Плановые даты выставления счетов", "invoicesStyle") {
			@Override
			public void createEvents(TimelineCache cacheItem, List<TimelineEvent> createdEvents) {
				for (InvoicePlan plan : cacheItem.getPlans()) {
					createdEvents.add(new TimelineEvent(
						new TimelineEventPayload("", plan),
						plan.plannedPeriod().startDate(),
						plan.plannedPeriod().endDate(),
						false,
						getGroupId(),
						getGroupStyle()
					));
				}
			}
		},

		INVOICE_BILLED_PERIOD("invoicesFactualGroup", "Фактические даты выставления счетов", "invoicesStyle") {
			@Override
			public void createEvents(TimelineCache cacheItem, List<TimelineEvent> createdEvents) {
				for (InvoicePlan plan : cacheItem.getPlans()) {
					createdEvents.add(new TimelineEvent(
						new TimelineEventPayload("", plan),
						plan.plannedPeriod().startDate(),
						plan.plannedPeriod().endDate(),
						false,
						getGroupId(),
						getGroupStyle()
					));
				}
			}
		};

		//@formatter:off
		
		@Getter
		private String groupId;

		@Getter
		private String desc;

		@Getter
		private String groupStyle;

		TimelineSwimlane(String groupId, String desc, String groupStyle) {
			this.groupId = groupId;
			this.desc = desc;
			this.groupStyle = groupStyle;
		}

		public abstract void createEvents(TimelineCache cacheItem, List<TimelineEvent> createdEvents);
	}
}
