package ru.argustelecom.box.env.stl.period;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import com.google.common.collect.Range;

import ru.argustelecom.box.inf.nls.LocaleUtils;

/**
 * Абстрактный период
 */
public interface Period {

	/**
	 * Границы абстрактного периода. Воспринимаются всегда как "закрытые", т.е. периоду принадлежит и начало и конец
	 */
	Range<LocalDateTime> boundaries();

	/**
	 * Превращает текущий период в закрытый диапазон даты и времени, представленных в виде Instant
	 */
	Range<Date> toDateRange();

	/**
	 * Превращает текущий период в закрытый диапазон даты и времени, представленных в виде Instant
	 */
	Range<Date> toDateRange(ZoneId zoneId);

	/**
	 * Дата начала периода
	 */
	Date startDate();

	/**
	 * Дата начала периода в указанной зоне
	 */
	Date startDate(ZoneId zoneId);

	/**
	 * Дата окончания периода
	 */
	Date endDate();

	/**
	 * Дата окончания периода в указанной зоне
	 */
	Date endDate(ZoneId zoneId);

	/**
	 * Локальное датовремя начала текущего периода, используется для расчетов с использованием современного API
	 * java.time
	 */
	LocalDateTime startDateTime();

	/**
	 * Локальное датовремя окончания текущего периода, используется для расчетов с использованием современного API
	 * java.time
	 */
	LocalDateTime endDateTime();

	/**
	 * True, если текущий период содержит указанную poi
	 */
	boolean contains(Date poi);

	/**
	 * True, если текущий период содержит указанную poi, предствленную в указанной зоне
	 */
	boolean contains(Date poi, ZoneId zoneId);

	/**
	 * True, если текущий период содержит указанную poi
	 */
	boolean contains(LocalDateTime poi);

	/**
	 * Форматирует текущий период с использованием указанного форматерра по определенному шаблону. Шаблон должен быть
	 * представлен в формате MessageFormat и обязательно содержать два паттерна для подстановки: <code>{0}</code> для
	 * начала периода и <code>{1}</code> для окончания. Кроме этих подстановочных символов шаблон может содержать любые
	 * другие литералы: "с-по", "начало-конец" и т.д.
	 */
	default String formatPeriod(DateTimeFormatter dateTimeFormatter, String periodPattern) {
		String lowerBoundFmt = startDateTime().format(dateTimeFormatter);
		String upperBoundFmt = endDateTime().format(dateTimeFormatter);
		return LocaleUtils.format(periodPattern, lowerBoundFmt, upperBoundFmt);
	}
}