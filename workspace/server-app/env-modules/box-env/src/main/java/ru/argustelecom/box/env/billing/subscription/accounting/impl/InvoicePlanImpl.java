package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.google.common.collect.Range;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanPeriod;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanTimeline;
import ru.argustelecom.box.env.stl.period.AbstractPeriod;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.env.stl.period.Period;

@AllArgsConstructor(access = AccessLevel.PACKAGE)
public class InvoicePlanImpl implements InvoicePlan {

	@NonNull
	private ChargingPeriod chargingPeriod;

	@NonNull
	private RoundingPolicy roundingPolicy;

	@NonNull
	private InvoicePlannedPeriod plannedPeriod;

	private Long invoiceId;

	@NonNull
	private InvoicePlanTimeline timeline;

	@NonNull
	private InvoicePlanPeriodImpl summary;

	@NonNull
	private List<InvoicePlanPeriodImpl> details;

	@Override
	public ChargingPeriod chargingPeriod() {
		return chargingPeriod;
	}

	@Override
	public RoundingPolicy roundingPolicy() {
		return roundingPolicy;
	}

	@Override
	public Period plannedPeriod() {
		return plannedPeriod;
	}

	@Override
	public Long invoiceId() {
		return invoiceId;
	}

	@Override
	public InvoicePlanTimeline timeline() {
		return timeline;
	}

	@Override
	public InvoicePlanPeriod summary() {
		return summary;
	}

	@Override
	public List<InvoicePlanPeriod> details() {
		return Collections.unmodifiableList(details);
	}

	@Override
	public Iterator<InvoicePlanPeriod> iterator() {
		return details().iterator();
	}

	public static class InvoicePlannedPeriod extends AbstractPeriod {
		public InvoicePlannedPeriod(Range<LocalDateTime> boundaries) {
			super(boundaries);
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Plan: ");
		sb.append(plannedPeriod);
		sb.append(", units:").append(summary.baseUnitCount());
		sb.append(", cost:").append(summary.cost());
		sb.append(", delta:").append(summary.deltaCost());
		sb.append(", total:").append(summary.totalCost());
		return sb.toString();
	}
}
