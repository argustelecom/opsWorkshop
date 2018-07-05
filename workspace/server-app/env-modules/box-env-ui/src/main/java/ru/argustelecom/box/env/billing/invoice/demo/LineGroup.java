package ru.argustelecom.box.env.billing.invoice.demo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.Interval;
import org.primefaces.extensions.model.timeline.TimelineEvent;

import ru.argustelecom.box.env.billing.invoice.LongTermInvoiceDto;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;
import ru.argustelecom.box.env.stl.period.AccountingPeriod;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.env.stl.period.AbstractPeriod;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.chrono.TZ;

public enum LineGroup {
	HISTORY("historyGroup", "История изменений", "lifecycleRoutingHistoryStyle") {
		@Override
		public List<TimelineEvent> createEvents(AccountingPeriod accountingPeriod) {
			List<TimelineEvent> events = new ArrayList<>();
			lifecycleHistoryItems.stream().filter(entry -> checkDate(periodStartDate(accountingPeriod),
					periodEndDate(accountingPeriod), entry.getTransitionTime())).forEach(entry -> {
						TimelineEvent lifecycleRoutingEvent = new TimelineEvent(entry, entry.getTransitionTime(),
								entry.getTransitionTime(), false, getGroupId(), getGroupStyle());
						events.add(lifecycleRoutingEvent);
					});

			return events;
		}
	},
	ACCOUNTING_PERIODS("accountingPeriodsGroup", "Период расчета", "accountingPeriodsStyle") {
		@Override
		public List<TimelineEvent> createEvents(AccountingPeriod accountingPeriod) {
			return Collections.singletonList(new TimelineEvent(
					LocaleUtils.format("Длительность: {0} {1}; Стоимость: {2}", accountingPeriod.baseUnitCount(),
							accountingPeriod.getType().getBaseUnit(), accountingPeriod.cost()),
					periodStartDate(accountingPeriod), periodEndDate(accountingPeriod), false, getGroupId(),
					getGroupStyle()));
		}
	},
	CHARGING_PERIODS("chargingPeriodsGroup", "Периоды списания", "accountingPeriodsStyle") {
		@Override
		public List<TimelineEvent> createEvents(AccountingPeriod accountingPeriod) {
			List<TimelineEvent> events = new ArrayList<>();
			for (ChargingPeriod period : accountingPeriod.chargingPeriods()) {
				TimelineEvent periodEvent = new TimelineEvent(LocaleUtils.format("{0} руб.", period.cost()),
						periodStartDate(period), periodEndDate(period), false, getGroupId(), getGroupStyle());
				events.add(periodEvent);

			}
			return events;
		}
	},
	INVOICES("invoicesGroup", "Инвойсы", "invoicesStyle") {

		@Override
		public List<TimelineEvent> createEvents(AccountingPeriod accountingPeriod) {
			List<TimelineEvent> events = new ArrayList<>();

			invoices.stream()
					.filter(entry -> checkDate(periodStartDate(accountingPeriod), periodEndDate(accountingPeriod),
							entry.getStartDate())
							&& checkDate(periodStartDate(accountingPeriod), periodEndDate(accountingPeriod),
									entry.getEndDate()))
					.forEach(entry -> {
						TimelineEvent invoiceEvent = new TimelineEvent(entry, entry.getStartDate(), entry.getEndDate(),
								false, getGroupId(), getGroupStyle());
						events.add(invoiceEvent);
					});

			return events;
		}
	};

	private String groupId;
	private String desc;
	private String groupStyle;
	private static List<LongTermInvoiceDto> invoices;
	private static List<LifecycleHistoryItem> lifecycleHistoryItems;

	LineGroup(String groupId, String desc, String groupStyle) {
		this.groupId = groupId;
		this.desc = desc;
		this.groupStyle = groupStyle;
	}

	public String getGroupId() {
		return groupId;
	}

	public String getDesc() {
		return desc;
	}

	public String getGroupStyle() {
		return groupStyle;
	}

	public Boolean checkDate(Date startDate, Date endDate, Date checkDate) {
		Interval interval = new Interval(new DateTime(startDate).minusMinutes(1), new DateTime(endDate).plusMinutes(1));
		return interval.contains(new DateTime(checkDate));
	}

	public Date periodStartDate(AbstractPeriod period) {
		return Date.from(period.boundaries().lowerEndpoint().atZone(TZ.getServerZoneId()).toInstant());
	}

	public Date periodEndDate(AbstractPeriod period) {
		return Date.from(period.boundaries().upperEndpoint().atZone(TZ.getServerZoneId()).toInstant());
	}

	public abstract List<TimelineEvent> createEvents(AccountingPeriod accountingPeriod);

	public void setLifecycleHistoryItems(List<LifecycleHistoryItem> lifecycleHistoryItems) {
		LineGroup.lifecycleHistoryItems = lifecycleHistoryItems;
	}

	public void setInvoices(List<LongTermInvoiceDto> invoices) {
		LineGroup.invoices = invoices;
	}
}
