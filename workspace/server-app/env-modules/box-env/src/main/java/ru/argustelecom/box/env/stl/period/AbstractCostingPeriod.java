package ru.argustelecom.box.env.stl.period;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.stl.Money;

public abstract class AbstractCostingPeriod extends AbstractPeriod implements CostingPeriod {
	private long baseUnitCount;
	private Money cost;

	protected AbstractCostingPeriod(Range<LocalDateTime> boundaries, Money cost, long baseUnitCount) {
		super(boundaries);
		checkRequiredArgument(cost, "cost");
		checkArgument(baseUnitCount >= 0);

		this.baseUnitCount = baseUnitCount;
		this.cost = cost;
	}

	@Override
	public Money cost() {
		return cost;
	}

	@Override
	public long baseUnitCount() {
		return baseUnitCount;
	}

	@Override
	protected void writePeriodInfo(StringBuilder sb) {
		super.writePeriodInfo(sb);
		sb.append(baseUnitCount()).append(" ");
		sb.append(cost().getRoundAmount()).append(" y.e.");
	}
}
