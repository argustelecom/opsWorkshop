package ru.argustelecom.box.env.stl.period;

import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import ru.argustelecom.box.inf.nls.LocaleUtils;

public class UnboundedPeriod {

	public static final UnboundedPeriod INFINITE = new UnboundedPeriod(null, null);

	private LocalDateTime lowerBound;
	private LocalDateTime upperBound;

	protected UnboundedPeriod(LocalDateTime lowerBound, LocalDateTime upperBound) {
		this.lowerBound = lowerBound;
		this.upperBound = upperBound;
	}

	public static UnboundedPeriod of(LocalDateTime lowerBound, LocalDateTime upperBound) {
		return lowerBound == null && upperBound == null ? INFINITE : new UnboundedPeriod(lowerBound, upperBound);
	}

	public static UnboundedPeriod of(Date lowerBound, Date upperBound) {
		// @formatter:off
		return of(
			lowerBound != null ? toLocalDateTime(lowerBound) : null,
			upperBound != null ? toLocalDateTime(upperBound) : null
		); 
		// @formatter:on
	}

	/**
	 * Нижняя граница периода. Может отсутствовать. В этом случае считается, что нижняя граница условно равна минус
	 * бесконечности, т.е. любая дата всегда строго больше нижней границе и никогда не равна ей
	 */
	public LocalDateTime lowerBound() {
		return lowerBound;
	}

	/**
	 * Верхняя граница периода. Может отсутствовать. В этом случае считается, что верхняя граница условно равна плюс
	 * бесконечности, т.е. любая дата всегда строго меньше верхней границе и никогда не равна ей
	 */
	public LocalDateTime upperBound() {
		return upperBound;
	}

	/**
	 * Определяет, является ли период ограниченным. True, если имеются ограничения одновременно с обоих концов lower и
	 * upper
	 */
	public boolean isBounded() {
		return hasLowerBound() && hasUpperBound();
	}

	/**
	 * true если период ограничен нижней границей.
	 */
	public boolean hasLowerBound() {
		return lowerBound() != null;
	}

	/**
	 * true если период ограничен верхней границей.
	 */
	public boolean hasUpperBound() {
		return upperBound() != null;
	}

	/**
	 * true если указанная дата входит в текущий период
	 */
	public boolean contains(LocalDateTime poi) {
		return afterOrEqualLowerBound(poi) && beforeOrEqualUpperBound(poi);
	}

	/**
	 * true если указанный стандартный период пересекается с текущим периодом
	 */
	public boolean intersects(Period period) {
		return contains(period.startDateTime()) || contains(period.endDateTime());
	}

	/**
	 * true, если указанная дата находится на временной шкале СТРОГО ДО нижней границы периода. Если нижняя граница
	 * периода равна "минус бесконечности", то никакая дата НЕ МОЖЕТ НАХОДИТЬСЯ ДО левой границы, т.е. метод будет
	 * всегда возвращать false
	 */
	public boolean beforeLowerBound(LocalDateTime poi) {
		return hasLowerBound() ? poi.isBefore(lowerBound()) : false;
	}

	/**
	 * true, если указанная дата находится на временной шкале СТРОГО ДО верхней границы периода. Если верхняя граница
	 * периода равна "плюс бесконечности", то любая дата НАХОДИТСЯ ДО этой границы, т.е. метод всегда будет возвращать
	 * true
	 */
	public boolean beforeUpperBound(LocalDateTime poi) {
		return hasUpperBound() ? poi.isBefore(upperBound()) : true;
	}

	/**
	 * true, если указанная дата находится на временной шкале СТРОГО ПОСЛЕ нижней границы периода. Если нижняя граница
	 * периода равна "минус бесконечности", то любая дата НАХОДИТСЯ ПОСЛЕ этой границы, т.е. метод всегда будет
	 * возвращать true
	 */
	public boolean afterLowerBound(LocalDateTime poi) {
		return hasLowerBound() ? poi.isAfter(lowerBound()) : true;
	}

	/**
	 * true, если указанная дата находится на временной шкале СТРОГО ПОСЛЕ верхней границы периода. Если верхняя граница
	 * периода равна "плюс бесконечности", то никакая дата НЕ МОЖЕТ НАХОДИТЬСЯ после этой границы, т.е. метод всегда
	 * будет возвращать false
	 */
	public boolean afterUpperBound(LocalDateTime poi) {
		return hasUpperBound() ? poi.isAfter(upperBound()) : false;
	}

	/**
	 * true, если указанная дата находится на временной шкале до верхней границы, либо равна ей
	 */
	public boolean beforeOrEqualUpperBound(LocalDateTime poi) {
		return beforeUpperBound(poi) || hasUpperBound() && poi.isEqual(upperBound());
	}

	/**
	 * true, если указанная дата находится на временной шкале после нижней границы, либо равна ей
	 */
	public boolean afterOrEqualLowerBound(LocalDateTime poi) {
		return afterLowerBound(poi) || hasLowerBound() && poi.isEqual(lowerBound());
	}

	@Override
	public String toString() {
		String lower = hasLowerBound() ? lowerBound().format(AbstractPeriod.LDT_FORMATTER) : "INFINIT";
		String upper = hasUpperBound() ? upperBound().format(AbstractPeriod.LDT_FORMATTER) : "INFINIT";
		return LocaleUtils.format("[{0} - {1}]", lower, upper);
	}
}
