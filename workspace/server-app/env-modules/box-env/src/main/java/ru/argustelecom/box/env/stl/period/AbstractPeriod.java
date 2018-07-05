package ru.argustelecom.box.env.stl.period;

import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.nls.LocaleUtils.format;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.google.common.collect.Range;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.exception.BusinessException;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractPeriod implements Period {

	protected static final DateTimeFormatter LDT_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

	private Range<LocalDateTime> boundaries;

	protected AbstractPeriod(Range<LocalDateTime> boundaries) {
		checkRequiredArgument(boundaries, "boundaries");
		this.boundaries = boundaries;
	}

	@Override
	public Range<LocalDateTime> boundaries() {
		return boundaries;
	}

	@Override
	public Range<Date> toDateRange() {
		return toDateRange(TZ.getServerZoneId());
	}

	@Override
	public Range<Date> toDateRange(ZoneId zoneId) {
		Date lower = fromLocalDateTime(boundaries().lowerEndpoint(), zoneId);
		Date upper = fromLocalDateTime(boundaries().upperEndpoint(), zoneId);
		return Range.closed(lower, upper);
	}

	@Override
	public Date startDate() {
		return startDate(TZ.getServerZoneId());
	}

	@Override
	public Date startDate(ZoneId zoneId) {
		return fromLocalDateTime(boundaries().lowerEndpoint(), zoneId);
	}

	@Override
	public Date endDate() {
		return endDate(TZ.getServerZoneId());
	}

	@Override
	public Date endDate(ZoneId zoneId) {
		return fromLocalDateTime(boundaries().upperEndpoint(), zoneId);
	}

	@Override
	public LocalDateTime startDateTime() {
		return boundaries().lowerEndpoint();
	}

	@Override
	public LocalDateTime endDateTime() {
		return boundaries().upperEndpoint();
	}

	@Override
	public boolean contains(Date poi) {
		return contains(toLocalDateTime(poi));
	}

	@Override
	public boolean contains(Date poi, ZoneId zoneId) {
		return contains(toLocalDateTime(poi, zoneId));
	}

	@Override
	public boolean contains(LocalDateTime poi) {
		return boundaries().contains(poi);
	}

	public void checkContains(Date poi) {
		checkContains(toLocalDateTime(poi));
	}

	public void checkContains(Date poi, ZoneId zoneId) {
		checkContains(toLocalDateTime(poi, zoneId));
	}

	public void checkContains(LocalDateTime poi) {
		if (!boundaries().contains(poi)) {
			String poiFmt = poi.format(LDT_FORMATTER);
			String periodFmt = formatPeriod(LDT_FORMATTER, "{0} - {1}");
			throw new BusinessException(format("Дата {0} не принадлежит периоду: {1}", poiFmt, periodFmt));
		}
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		writePeriodInfo(sb);
		return sb.toString();
	}

	protected void writePeriodInfo(StringBuilder sb) {
		sb.append(formatPeriod(LDT_FORMATTER, "[{0} - {1}] "));
	}

}