package ru.argustelecom.box.env.stl.period;

import static com.google.common.base.Preconditions.checkArgument;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.util.Collections.unmodifiableList;
import static ru.argustelecom.box.env.stl.period.PeriodUtils.createBoundedRanges;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.nls.PeriodMessagesBundle;
import ru.argustelecom.box.env.stl.period.config.TemplateBasedConfig;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;

public class AccountingPeriod extends AbstractCostingPeriod
		implements TypedPeriod, DurablePeriod, IterablePeriod<AccountingPeriod> {

	private LinkedList<ChargingPeriod> chargingPeriods = new LinkedList<>();
	private Money baseUnitCost;

	private LocalDateTime startOfInterest;
	private LocalDateTime endOfInterest;

	private PeriodType type;
	private PeriodDuration chargingDuration;
	private PeriodDuration accountingDuration;

	AccountingPeriod(Range<LocalDateTime> boundaries, Money cost, long baseUnitCount, Money baseUnitCost,
			LocalDateTime startOfInterest, LocalDateTime endOfInterest, PeriodType type,
			PeriodDuration chargingDuration, PeriodDuration accountingDuration) {

		super(boundaries, cost, baseUnitCount);
		this.baseUnitCost = checkRequiredArgument(baseUnitCost, "baseUnitCost");
		this.startOfInterest = checkRequiredArgument(startOfInterest, "startOfInterest");
		this.endOfInterest = endOfInterest;
		this.type = checkRequiredArgument(type, "type");
		this.chargingDuration = checkRequiredArgument(chargingDuration, "chargingDuration");
		this.accountingDuration = checkRequiredArgument(accountingDuration, "accountingDuration");
	}

	void addChargingPeriod(Range<LocalDateTime> chargingRange, Money chargingCost, long chargingUnits) {
		chargingPeriods.add(new ChargingPeriod(chargingRange, chargingCost, chargingUnits, this));
	}

	public static AccountingPeriod create(PeriodConfig config) {
		//@formatter:off
		config.validate();
		
		Range<LocalDateTime> accountingRange = config.getPeriodType().calculatePeriodBoundaries(
			config.getStartOfInterest(),
			config.getPoi(), 
			config.getAccountingDuration()
		);
		
		long accountingUnits = config.getPeriodType().getBaseUnit().between(
			accountingRange.lowerEndpoint(), accountingRange.upperEndpoint().plus(1, MILLIS)
		);
		
		Money baseUnitCost = config.getTotalCost().divide(accountingUnits);

		AccountingPeriod accountingPeriod = new AccountingPeriod(
			accountingRange, 
			config.getTotalCost(),
			accountingUnits,
			baseUnitCost,
			config.getStartOfInterest(),
			config.getEndOfInterest(),
			config.getPeriodType(),
			config.getChargingDuration(),
			config.getAccountingDuration()
		);

		for (Range<LocalDateTime> chargingRange : createBoundedRanges(accountingRange, config.getChargingDuration())) {
			long chargingUnits = config.getPeriodType().getBaseUnit().between(
				chargingRange.lowerEndpoint(), chargingRange.upperEndpoint().plus(1, MILLIS)
			);
			Money chargingCost = baseUnitCost.multiply(chargingUnits);
			accountingPeriod.addChargingPeriod(chargingRange, chargingCost, chargingUnits);
		}

		return accountingPeriod;
		//@formatter:on
	}

	public LocalDateTime startOfInterest() {
		return startOfInterest;
	}

	public LocalDateTime endOfInterest() {
		return endOfInterest;
	}

	@Override
	public PeriodType getType() {
		return type;
	}

	@Override
	public PeriodDuration duration() {
		return accountingDuration;
	}

	public PeriodDuration chargingDuration() {
		return chargingDuration;
	}

	@Override
	public Money baseUnitCost() {
		return baseUnitCost;
	}

	public List<ChargingPeriod> chargingPeriods() {
		return unmodifiableList(chargingPeriods);
	}

	public ChargingPeriod chargingPeriodAt(Date poi) {
		return chargingPeriodAt(toLocalDateTime(poi));
	}

	public ChargingPeriod chargingPeriodAt(Date poi, ZoneId zoneId) {
		return chargingPeriodAt(toLocalDateTime(poi, zoneId));
	}

	public ChargingPeriod chargingPeriodAt(LocalDateTime poi) {
		for (ChargingPeriod period : chargingPeriods) {
			if (period.boundaries().contains(poi)) {
				return period;
			}
		}

		PeriodMessagesBundle messages = LocaleUtils.getMessages(PeriodMessagesBundle.class);
		throw new BusinessException(messages.accountingPeriodNotContainDate(poi.toString()));
	}

	public ChargingPeriod firstChargingPeriod() {
		return chargingPeriods.getFirst();
	}

	public ChargingPeriod lastChargingPeriod() {
		return chargingPeriods.getLast();
	}

	ChargingPeriod nextChargingPeriod(ChargingPeriod from) {
		checkArgument(Objects.equals(from.accountingPeriod(), this));

		if (Objects.equals(chargingPeriods.getLast(), from)) {
			AccountingPeriod nextAccountingPeriod = this.next();
			return nextAccountingPeriod.chargingPeriods.getFirst();
		}

		int index = chargingPeriods.indexOf(from);
		if (index >= 0 && index < chargingPeriods.size() - 1) {
			return chargingPeriods.get(++index);
		}

		throw new IllegalStateException("Текущий период расчета не содержит указанный период списания");
	}

	ChargingPeriod prevChargingPeriod(ChargingPeriod from) {
		checkArgument(Objects.equals(from.accountingPeriod(), this));

		if (Objects.equals(chargingPeriods.getFirst(), from)) {
			AccountingPeriod prevAccountingPeriod = this.prev();
			return prevAccountingPeriod.chargingPeriods.getLast();
		}

		int index = chargingPeriods.indexOf(from);
		if (index > 0 && index < chargingPeriods.size()) {
			return chargingPeriods.get(--index);
		}

		throw new IllegalStateException("Текущий период расчета не содержит указанный период списания");
	}

	@Override
	public AccountingPeriod next() {
		TemplateBasedConfig cfg = new TemplateBasedConfig(this);
		cfg.setPoi(boundaries().upperEndpoint().plus(1, MILLIS));
		return AccountingPeriod.create(cfg);
	}

	@Override
	public AccountingPeriod prev() {
		TemplateBasedConfig cfg = new TemplateBasedConfig(this);
		cfg.setPoi(boundaries().lowerEndpoint().minus(1, MILLIS));
		return AccountingPeriod.create(cfg);
	}

	@Override
	protected void writePeriodInfo(StringBuilder sb) {
		sb.append("Ta{").append(getType().getBaseUnit().name()).append(", ");
		super.writePeriodInfo(sb);
		sb.append("}");
	}

	//@formatter:off
	
	@Override
	public int hashCode() {
		return new HashCodeBuilder()
			.append(this.startOfInterest())
			.append(this.endOfInterest())
			.append(this.boundaries())
			.append(this.cost())
			.append(this.baseUnitCost())
			.append(this.baseUnitCount())
			.append(this.getType())
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

		AccountingPeriod that = (AccountingPeriod) obj;
		
		return new EqualsBuilder()
			.append(this.startOfInterest(), that.startOfInterest())
			.append(this.endOfInterest(), that.endOfInterest())
			.append(this.boundaries(), that.boundaries())
			.append(this.cost(), that.cost())
			.append(this.baseUnitCost(), that.baseUnitCost())
			.append(this.baseUnitCount(), that.baseUnitCount())
			.append(this.getType(), that.getType())
			.isEquals();
	}
	
	//@formatter:on
}
