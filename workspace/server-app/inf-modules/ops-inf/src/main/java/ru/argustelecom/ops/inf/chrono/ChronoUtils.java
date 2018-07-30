package ru.argustelecom.ops.inf.chrono;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.system.inf.chrono.DateUtils.after;
import static ru.argustelecom.system.inf.chrono.DateUtils.before;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import ru.argustelecom.system.inf.chrono.TZ;

public class ChronoUtils {

	private ChronoUtils() {
		//
	}

	public static Date fromLocalDateTime(LocalDateTime localDateTime) {
		return fromLocalDateTime(localDateTime, TZ.getServerZoneId());
	}

	public static Date fromLocalDateTime(LocalDateTime localDateTime, ZoneId zoneId) {
		checkNotNull(localDateTime);
		return Date.from(localDateTime.atZone(zoneId).toInstant());
	}

	public static Date fromLocalDate(LocalDate localDate) {
		return fromLocalDate(localDate, TZ.getServerZoneId());
	}

	public static Date fromLocalDate(LocalDate localDate, ZoneId zoneId) {
		checkNotNull(localDate);
		return Date.from(localDate.atStartOfDay().atZone(zoneId).toInstant());
	}

	public static LocalDateTime fromLong(Long date) {
		return LocalDateTime.ofInstant(Instant.ofEpochMilli(date), TZ.getServerZoneId());
	}

	public static LocalDateTime toLocalDateTime(Date date) {
		return toLocalDateTime(date, TZ.getServerZoneId());
	}

	public static LocalDateTime toLocalDateTime(Date date, ZoneId zoneId) {
		checkNotNull(date);
		return date.toInstant().atZone(zoneId).toLocalDateTime();
	}

	public static LocalDate toLocalDate(Date date) {
		checkNotNull(date);
		return toLocalDate(date, TZ.getServerZoneId());
	}

	public static LocalDate toLocalDate(Date date, ZoneId zoneId) {
		checkNotNull(date);
		return date.toInstant().atZone(zoneId).toLocalDate();
	}

	public static LocalDate localDateAtZone(LocalDate localDate, ZoneId zoneId) {
		checkNotNull(localDate);
		return localDate.atStartOfDay(zoneId).toLocalDate();
	}

	public static LocalDateTime localDateTimeAtZone(LocalDateTime localDateTime, ZoneId zoneId) {
		checkNotNull(localDateTime);
		return localDateTime.atZone(zoneId).toLocalDateTime();
	}

	public static Date minDate(Date a, Date b) {
		return before(a, b) ? a : b;
	}

	public static Date minDate(Date a, Date b, Date c) {
		return minDate(minDate(a, b), c);
	}

	public static Date maxDate(Date a, Date b) {
		return after(a, b) ? a : b;
	}

	public static Date maxDate(Date a, Date b, Date c) {
		return maxDate(maxDate(a, b), c);
	}

	public static LocalDateTime min(Date a, LocalDateTime b) {
		return min(a, b, TZ.getServerZoneId());
	}

	public static LocalDateTime min(Date a, LocalDateTime b, ZoneId zoneId) {
		return min(toLocalDateTime(a, zoneId), b);
	}

	public static LocalDateTime min(LocalDateTime a, LocalDateTime b) {
		return a.compareTo(b) <= 0 ? a : b;
	}

	public static LocalDateTime max(Date a, LocalDateTime b) {
		return max(a, b, TZ.getServerZoneId());
	}

	public static LocalDateTime max(Date a, LocalDateTime b, ZoneId zoneId) {
		return max(toLocalDateTime(a, zoneId), b);
	}

	public static LocalDateTime max(LocalDateTime a, LocalDateTime b) {
		return a.compareTo(b) >= 0 ? a : b;
	}

	public static Date plusMillis(Date date, long millis) {
		return new Date(date.getTime() + millis);
	}

}