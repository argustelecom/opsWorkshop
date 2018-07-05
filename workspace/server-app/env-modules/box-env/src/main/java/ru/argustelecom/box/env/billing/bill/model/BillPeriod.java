package ru.argustelecom.box.env.billing.bill.model;

import static java.time.temporal.ChronoUnit.MILLIS;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriodType.CALENDARIAN;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriodType.CUSTOM;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.google.common.collect.Range;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.bill.model.Periods.Month;
import ru.argustelecom.box.env.billing.bill.model.Periods.Quarter;
import ru.argustelecom.box.env.billing.bill.model.Periods.Semester;
import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.env.stl.period.AbstractPeriod;
import ru.argustelecom.box.env.stl.period.DurablePeriod;
import ru.argustelecom.box.env.stl.period.IterablePeriod;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

// Необходим для JSF.
// Для восстановления состояния нужен публичный конструктор по-умолчанию в
// javax.faces.component.StateHolderSaver#restore.
// Не вызывать в прикладном коде!!!
@NoArgsConstructor(access = AccessLevel.PUBLIC)
public class BillPeriod extends AbstractPeriod implements DurablePeriod, IterablePeriod<BillPeriod> {

	public static final DateTimeFormatter DATETIME_DEFAULT_PATTERN = DateTimeFormatter.ofPattern("dd.MM.yyyy");

	@Getter
	private BillPeriodType type;
	private PeriodDuration duration;

	private BillPeriod(PeriodUnit periodUnit, LocalDateTime poi) {
		super(periodUnit.boundariesOf(poi));
		duration = PeriodDuration.of(1, periodUnit);
		type = CALENDARIAN;
	}

	private BillPeriod(LocalDateTime start, LocalDateTime end) {
		super(Range.closed(start, end));
		type = CUSTOM;
	}

	public static BillPeriod of(PeriodUnit periodUnit, LocalDateTime poi) {
		checkRequiredArgument(periodUnit, "periodUnit");
		checkRequiredArgument(poi, "poi");
		return new BillPeriod(periodUnit, poi);
	}

	public static BillPeriod of(LocalDateTime start, LocalDateTime end) {
		checkRequiredArgument(start, "start");
		checkRequiredArgument(end, "end");
		return new BillPeriod(start, end);
	}

	@Override
	public PeriodDuration duration() {
		return duration;
	}

	@Override
	public boolean isIterable() {
		return CALENDARIAN.equals(type);
	}

	@Override
	public BillPeriod next() {
		if (!isIterable()) {
			throw new SystemException("Итерации между периодами доступны только для календарного типа периода");
		}
		return of(getPeriodUnit(), boundaries().upperEndpoint().plus(1, MILLIS));
	}

	@Override
	public BillPeriod prev() {
		if (!isIterable()) {
			throw new SystemException("Итерации между периодами доступны только для календарного типа периода");
		}
		return of(getPeriodUnit(), boundaries().lowerEndpoint().minus(1, MILLIS));
	}

	public PeriodUnit getPeriodUnit() {
		return duration != null ? duration.getUnit() : null;
	}

	@Override
	public String toString() {
		if (duration() != null) {
			BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
			String year = boundaries().lowerEndpoint().getYear() + " " + messages.yearShortName();
			switch (getPeriodUnit()) {
			case MONTH:
				return Month.of(boundaries().lowerEndpoint()).getName() + " " + year;
			case QUARTER:
				return Quarter.of(boundaries().lowerEndpoint()).getName() + " " + year;
			case SEMESTER:
				return Semester.of(boundaries().lowerEndpoint()).getName() + " " + year;
			case YEAR:
				return year;
			default:
				throw new SystemException();
			}
		}
		return formatPeriod(DATETIME_DEFAULT_PATTERN, "{0} - {1}");
	}

}