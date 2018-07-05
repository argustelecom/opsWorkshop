package ru.argustelecom.box.env.saldo.export.model;

import java.time.LocalDateTime;

import com.google.common.collect.Range;

import lombok.AllArgsConstructor;
import ru.argustelecom.box.env.saldo.nls.SaldoExportMessagesBundle;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@AllArgsConstructor
public enum CalculationType {

	//@formatter:off
	AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD;
	//@formatter:on

	public String getName() {
    	return LocaleUtils.getMessages(SaldoExportMessagesBundle.class).atTheEndOfPeriodForNextPeriod();
	}

	public LocalDateTime getExportDate(PeriodUnit periodUnit, LocalDateTime poi) {
		switch (this) {
		case AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD:
			Range<LocalDateTime> range = periodUnit.boundariesOf(poi);
			return range.upperEndpoint();
		default:
			throw new SystemException("Unsupported period type");
		}
	}

	public LocalDateTime getNextExportDate(PeriodUnit periodUnit, LocalDateTime poi) {
		switch (this) {
		case AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD:
			LocalDateTime poiForNextPeriod = periodUnit.addTo(poi, 1);
			Range<LocalDateTime> range = periodUnit.boundariesOf(poiForNextPeriod);
			return range.upperEndpoint();
		default:
			throw new SystemException("Unsupported period type");
		}
	}

	public Range<LocalDateTime> getRange(PeriodUnit periodUnit, LocalDateTime poi) {
		switch (this) {
		case AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD:
			return periodUnit.boundariesOf(getExportDate(periodUnit, poi).plusDays(1));
		default:
			throw new SystemException("Unsupported period type");
		}
	}

}