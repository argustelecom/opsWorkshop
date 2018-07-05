package ru.argustelecom.box.env.stl.period.config;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.AccountingPeriod;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;

public class TemplateBasedConfig extends AbstractPeriodConfig<TemplateBasedConfig> {

	private AccountingPeriod template;

	public TemplateBasedConfig(AccountingPeriod template) {
		this.template = checkRequiredArgument(template, "template");
	}

	@Override
	public LocalDateTime getStartOfInterest() {
		return template.startOfInterest();
	}

	@Override
	public LocalDateTime getEndOfInterest() {
		return template.endOfInterest();
	}

	@Override
	public Money getTotalCost() {
		return template.cost();
	}

	@Override
	public PeriodType getPeriodType() {
		return template.getType();
	}

	@Override
	public PeriodDuration getAccountingDuration() {
		return template.duration();
	}

	@Override
	public PeriodDuration getChargingDuration() {
		return template.chargingDuration();
	}

}