package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanPeriod;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.AbstractCostingPeriod;

public class InvoicePlanPeriodImpl extends AbstractCostingPeriod implements InvoicePlanPeriod {

	private InvoicePlanModifier modifier;
	private Money baseUnitCost;
	private Money totalCost;
	private Money deltaCost;

	InvoicePlanPeriodImpl(Range<LocalDateTime> boundaries, long baseUnitCount, Money baseUnitCost, Money cost,
			InvoicePlanModifier modifier, Money totalCost, Money deltaCost) {
		super(boundaries, cost, baseUnitCount);
		this.modifier = modifier;
		this.baseUnitCost = checkRequiredArgument(baseUnitCost, "baseUnitCost");
		this.totalCost = checkRequiredArgument(totalCost, "totalCost");
		this.deltaCost = checkRequiredArgument(deltaCost, "deltaCost");
	}

	@Override
	public InvoicePlanModifier modifier() {
		return modifier;
	}

	@Override
	public Money totalCost() {
		return totalCost;
	}

	@Override
	public Money deltaCost() {
		return deltaCost;
	}

	@Override
	public Money baseUnitCost() {
		return baseUnitCost;
	}
}
