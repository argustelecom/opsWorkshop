package ru.argustelecom.box.env.stl.period;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.LocalDateTime;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.stl.Money;

public class ChargingPeriod extends AbstractCostingPeriod
		implements TypedPeriod, DurablePeriod, IterablePeriod<ChargingPeriod> {

	private AccountingPeriod accountingPeriod;

	ChargingPeriod(Range<LocalDateTime> boundaries, Money cost, long baseUnitCount, AccountingPeriod accountingPeriod) {
		super(boundaries, cost, baseUnitCount);
		this.accountingPeriod = checkNotNull(accountingPeriod);
	}

	@Override
	public PeriodType getType() {
		return accountingPeriod.getType();
	}

	@Override
	public PeriodDuration duration() {
		return accountingPeriod.chargingDuration();
	}

	@Override
	public Money baseUnitCost() {
		return accountingPeriod.baseUnitCost();
	}

	public AccountingPeriod accountingPeriod() {
		return accountingPeriod;
	}

	@Override
	public ChargingPeriod next() {
		return accountingPeriod.nextChargingPeriod(this);
	}

	@Override
	public ChargingPeriod prev() {
		return accountingPeriod.prevChargingPeriod(this);
	}

	public boolean isBaseUnitBoundary(LocalDateTime poi) {
		if (poi == null) {
			return false;
		}
		LocalDateTime soi = accountingPeriod().startOfInterest();
		Range<LocalDateTime> baseUnit = getType().calculateBaseUnitBoundaries(soi, poi);
		return poi.isEqual(baseUnit.lowerEndpoint()) || poi.isEqual(baseUnit.upperEndpoint());
	}

	@Override
	protected void writePeriodInfo(StringBuilder sb) {
		sb.append("Tc{").append(getType().getBaseUnit().name()).append(", ");
		super.writePeriodInfo(sb);
		sb.append("}");
	}

	//@formatter:off
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.accountingPeriod())
			.append(this.boundaries())
			.append(this.cost())
			.append(this.baseUnitCount())
			.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}

		ChargingPeriod that = (ChargingPeriod) obj;
		return new EqualsBuilder()
			.append(this.accountingPeriod(), that.accountingPeriod())
			.append(this.boundaries(), that.boundaries())
			.append(this.cost(), that.cost())
			.append(this.baseUnitCount(), that.baseUnitCount())
			.isEquals();
	}
	
	//@formatter:on
}
