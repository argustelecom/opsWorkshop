package ru.argustelecom.box.env.stl.period;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.NANOS;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.min;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalAmount;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.google.common.collect.Range;

public final class PeriodUtils {

	private PeriodUtils() {
	}

	/**
	 * Расчитывает все периоды, которые входят в указанные границы.
	 * 
	 * @param boundaries
	 *            границы, внутри которых необходимо расчитать периоды.
	 * @param duration
	 *            продолжительность периода.
	 * @return Список периодов, входящих в указанные границы.
	 */
	public static List<Range<LocalDateTime>> createBoundedRanges(Range<LocalDateTime> boundaries,
			PeriodDuration duration) {

		LinkedList<Range<LocalDateTime>> result = new LinkedList<>();

		LocalDateTime limiterStart = boundaries.lowerEndpoint();
		LocalDateTime limiterEnd = boundaries.upperEndpoint();

		LocalDateTime rangeStart = limiterStart;
		LocalDateTime rangeEnd = min(rangeStart.plus(duration.getTemporalAmount()).minus(1, MILLIS), limiterEnd);

		while (boundaries.contains(rangeStart) && boundaries.contains(rangeEnd)) {
			result.add(Range.closed(rangeStart, rangeEnd));
			rangeStart = rangeEnd.plus(1, MILLIS);
			rangeEnd = min(rangeStart.plus(duration.getTemporalAmount()).minus(1, MILLIS), limiterEnd);
		}

		checkState(!result.isEmpty());
		Range<LocalDateTime> last = result.pollLast();

		// Необходимо компенсировать недостающую миллисекунду аналогично компенсации, при определении BaseUnits.between
		// здесь это безопасно даже в том случае, если lowerEndpoint == upperEndpoint, т.к. длительность полученного
		// периода используется только для определения его отношения к длительности целевого периода
		long lastDuration = MILLIS.between(last.lowerEndpoint(), last.upperEndpoint()) + 1;
		if (lastDuration < duration.getDuration().toMillis() >>> 1) {
			checkState(!result.isEmpty());
			Range<LocalDateTime> penult = result.pollLast();
			result.add(Range.closed(penult.lowerEndpoint(), last.upperEndpoint()));
		} else {
			result.add(last);
		}

		return result;
	}

	/**
	 * Расчитывает период для указанной точки интереса. Период расчитывается от точки отсчёта:
	 * <ol>
	 * <li>Расчитывается кол-во {@linkplain PeriodUnit единиц периода} между точками отсчёта и интереса.</li>
	 * <li>Из кол-ва единиц периода между точками отсчёта и интереса, и длительностью периода. Расчитывается сколько
	 * целых периодов помещается между точками отсчёта и интереса.</li>
	 * <li>К точке отсчёта прибавляем полученное кол-во периодов и принимаем результат за начальную дату периода.</li>
	 * <li>К начальной дате периода прибавляем его длительность и получаем конечную дату периода.</li>
	 * </ol>
	 *
	 * Если от точки отсчёта, до точки интереса не прошёл ни один целый период, то начальной датой будет точка отсчёта.
	 *
	 * @param soi
	 *            абстрактная дата, с которой нам интересно начать считать кол-во прошедших периодов до {@code poi}.
	 * @param poi
	 *            точка интереса, для которой мы хотим получить период, в который она входит.
	 * @param duration
	 *            продолжительность периода.
	 * @return Период с начальной и конечной границей, в которые входи {@code poi}.
	 */
	public static Range<LocalDateTime> createPeriod(LocalDateTime soi, LocalDateTime poi, PeriodDuration duration) {
		checkArgument(soi != null, "Не указана точка начала интереса 'soi'");
		checkArgument(poi != null, "Не указана точка интереса 'poi'");
		checkArgument(soi.compareTo(poi) <= 0, "Точка начала интереса должна быть меньше точки интереса [%s > %s]", soi,
				poi);

		long unitsBetween = duration.getUnit().between(soi, poi);
		long wholePeriods = unitsBetween / duration.getAmount();

		TemporalAmount temporalAmountBetween = null;
		TemporalAmount periodTemporalAmount = duration.getTemporalAmount();
		if (periodTemporalAmount instanceof Period) {
			temporalAmountBetween = ((Period) periodTemporalAmount).multipliedBy((int) wholePeriods);
		} else if (periodTemporalAmount instanceof Duration) {
			temporalAmountBetween = ((Duration) periodTemporalAmount).multipliedBy((int) wholePeriods);
		} else {
			checkState(false, "Неподдерживаемый тип периода: " + periodTemporalAmount.getClass());
		}

		LocalDateTime start = soi.plus(temporalAmountBetween);
		LocalDateTime end = start.plus(duration.getTemporalAmount()).minus(1, MILLIS);

		Range<LocalDateTime> result = Range.closed(start, end);
		checkState(result.contains(poi), "BOX-514");

		return result;
	}

	/**
	 * Расчитывает период для указанной точки интереса. В качестве точки отсчета берется указанная точка интереса.
	 * Алгоритм расчета такой же, как и в {@linkplain #createPeriod(LocalDateTime, LocalDateTime, PeriodDuration)}. По
	 * факту, представляет собой шоткат для указанного метода в случае, когда точка отсчета и точка интереса совпадают
	 * 
	 * @param poi
	 *            - точка интереса, начиная с которой мы хотим получить период с указанной длительностью
	 * @param duration
	 *            - длительность периода
	 * 
	 * @return полученный период с корректными границами по правилу inclusive - inclusive
	 */
	public static Range<LocalDateTime> createPeriod(LocalDateTime poi, PeriodDuration duration) {
		checkRequiredArgument(poi, "poi");
		checkRequiredArgument(duration, "duration");

		return createPeriod(poi, poi, duration);
	}

	/**
	 * Корректно вычисляет границы базовой единицы указанной точки интереса для предоставленного типа периода. Для
	 * расчета произвольных периодов необходимо знать точку начала интереса (soi), т.е. с какого момента считать период
	 * 
	 * @param periodType
	 *            тип периода
	 * @param soi
	 *            точка начала интереса. Абстрактная дата, с которой нам интересно начать считать кол-во прошедших
	 *            периодов до {@code poi}.
	 * @param poi
	 *            точка интереса, для которой мы хотим получить границы базовой единицы, в которую она (poi) входит.
	 * 
	 * @return границы базовой единицы poi, представленные в виде закрытого непрерывного диапазона
	 */
	public static Range<LocalDateTime> createBaseUnitBounds(LocalDateTime soi, LocalDateTime poi,
			PeriodType periodType) {
		checkRequiredArgument(soi, "StartOfInterest");
		checkRequiredArgument(poi, "PointOfInterest");
		checkRequiredArgument(periodType, "PeriodType");

		Range<LocalDateTime> poiCalendarianBounds = periodType.getBaseUnit().boundariesOf(poi);
		if (periodType == PeriodType.CALENDARIAN) {
			// Если тип периода календарный, то необходимо выровнять базовую единицу по календарной сетке, т.е. вернуть
			// календарыне границы базовой единицы
			return poiCalendarianBounds;
		}

		// Для любого другого типа периода (Произвольный или Отладочный) начало периода (soi или StartOfInterest) скорее
		// всего будет сильно отличаться от календарных границ, поэтому необходимо вычислить несколько дополнительных
		// величин, таких как величину сдвига soi относительно календарной границы, величину сдвига poi (точка инетереса
		// или PointOfInterest) относительно календарной границы, а также точные календарные границы начала и конца
		// базовой единицы poi
		LocalDateTime soiCalendarianStart = periodType.getBaseUnit().boundariesOf(soi).lowerEndpoint();
		LocalDateTime poiCalendarianStart = poiCalendarianBounds.lowerEndpoint();
		LocalDateTime poiCalendarianEnd = poiCalendarianBounds.upperEndpoint();

		// Для расчета величины сдвига soi и poi использованы наносекунды как самые точные единицы, предоставляемые
		// java.time. Переполнения здесь опасаться не стоит, потому что для произвольных периодов базовые единицы час
		// или минута, поэтому величина сдвига будет относительно небольшой и не будет превышать часа.
		Long soiOffsetInNanos = NANOS.between(soiCalendarianStart, soi);
		Long poiOffsetInNanos = NANOS.between(poiCalendarianStart, poi);

		// После того, как мы вычислили величину сдвига soi относительно календарной границы, мы можем вычислить где в
		// текущем poi будет находиться начало и где конец базовой единицы. Для этого необходимо к календарным границам
		// poi добавить величину сдвига soi относительно левой календарной границы своей базовой единицы...
		LocalDateTime baseUnitOffsetStart = poiCalendarianStart.plusNanos(soiOffsetInNanos);
		LocalDateTime baseUnitOffsetEnd = poiCalendarianEnd.plusNanos(soiOffsetInNanos);

		// ... однако, если сдвиг soi относительно своей левой календарной границы превышает сдвиг poi относительно его
		// левой календарной границы, то текущая точка интереса относится к предыдущей базовой единице, поэтому
		// полученные границы необходимо сместить назад на величину длительности базовой единицы. Ожидается, что базовая
		// единица будет "точной", т.е. меньше дня, т.к. со дня и выше базовые единицы имеют округленное значение
		// длительности в соответствии с ISO
		if (poiOffsetInNanos < soiOffsetInNanos) {
			checkState(!periodType.getBaseUnit().isEstimatedDuration(), "BOX-1357");
			baseUnitOffsetStart = baseUnitOffsetStart.minus(periodType.getBaseUnit().getDuration());
			baseUnitOffsetEnd = baseUnitOffsetEnd.minus(periodType.getBaseUnit().getDuration());
		}

		// Две полученные датовремени и есть правильные границы базовой единицы указанной точки интереса. Следовательно,
		// базовая единица указанной точки интереса обязана включать в себя эту точку интереса.
		Range<LocalDateTime> result = Range.closed(baseUnitOffsetStart, baseUnitOffsetEnd);
		checkState(result.contains(poi), "BOX-1357");

		return result;
	}

	public static Range<Date> toDateRange(Range<LocalDateTime> ldtRange) {
		return Range.closed(fromLocalDateTime(ldtRange.lowerEndpoint()), fromLocalDateTime(ldtRange.upperEndpoint()));
	}
}