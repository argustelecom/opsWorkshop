package ru.argustelecom.box.env.stl.period;

import static java.time.LocalDateTime.of;
import static java.time.Month.APRIL;
import static java.time.Month.AUGUST;
import static java.time.Month.FEBRUARY;
import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;
import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.HOUR;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.MINUTE;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.WEEK;
import static ru.argustelecom.box.env.stl.period.PeriodUtils.createPeriod;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Range;

public class PeriodUtilsTest {

	private LocalDateTime poi;

	@Before
	public void setUp() throws Exception {
		poi = of(2017, SEPTEMBER, 20, 10, 30);
	}

	@Test
	public void shouldCreateMinutesPeriod() {
		LocalDateTime baseDate = poi.minusMinutes(10);
		LocalDateTime expectedLowerPoint = of(2017, SEPTEMBER, 20, 10, 30);
		LocalDateTime expectedUpperPoint = of(2017, SEPTEMBER, 20, 10, 34, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.of(5, MINUTE), expectedLowerPoint, expectedUpperPoint);

		// from base date

		baseDate = poi.minusMinutes(2);
		expectedUpperPoint = of(2017, SEPTEMBER, 20, 10, 32, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.of(5, MINUTE), baseDate, expectedUpperPoint);
	}

	@Test
	public void shouldCreateHoursPeriod() {
		LocalDateTime baseDate = poi.minusHours(27);
		LocalDateTime expectedLowerPoint = of(2017, SEPTEMBER, 20, 8, 30);
		LocalDateTime expectedUpperPoint = of(2017, SEPTEMBER, 20, 13, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.of(5, HOUR), expectedLowerPoint, expectedUpperPoint);

		// from base date

		baseDate = poi.minusHours(4);
		expectedUpperPoint = of(2017, SEPTEMBER, 20, 11, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.of(5, HOUR), baseDate, expectedUpperPoint);
	}

	@Test
	public void shouldCreateDaysPeriod() {
		LocalDateTime baseDate = poi.minusDays(52);
		LocalDateTime expectedLowerPoint = of(2017, SEPTEMBER, 18, 10, 30);
		LocalDateTime expectedUpperPoint = of(2017, SEPTEMBER, 28, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofDays(10), expectedLowerPoint, expectedUpperPoint);

		// from base date

		baseDate = poi.minusDays(9);
		expectedUpperPoint = of(2017, SEPTEMBER, 21, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofDays(10), baseDate, expectedUpperPoint);
	}

	@Test
	public void shouldCreateWeekPeriod() {
		LocalDateTime baseDate = poi.minusWeeks(13);
		LocalDateTime expectedLowerPoint = of(2017, SEPTEMBER, 13, 10, 30);
		LocalDateTime expectedUpperPoint = of(2017, SEPTEMBER, 27, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.of(2, WEEK), expectedLowerPoint, expectedUpperPoint);

		// from base date

		baseDate = poi.minusDays(4);
		expectedUpperPoint = of(2017, SEPTEMBER, 30, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.of(2, WEEK), baseDate, expectedUpperPoint);
	}

	@Test
	public void shouldCreateMonthsPeriod() {
		LocalDateTime baseDate = poi.minusWeeks(7);
		LocalDateTime expectedLowerPoint = of(2017, SEPTEMBER, 2, 10, 30);
		LocalDateTime expectedUpperPoint = of(2017, OCTOBER, 2, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofMonths(1), expectedLowerPoint, expectedUpperPoint);

		// from base date

		baseDate = poi.minusWeeks(1);
		expectedUpperPoint = of(2017, OCTOBER, 13, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofMonths(1), baseDate, expectedUpperPoint);
	}

	@Test
	public void shouldCreateQuartersPeriod() {
		LocalDateTime baseDate = poi.minusMonths(13);
		LocalDateTime expectedLowerPoint = of(2017, AUGUST, 20, 10, 30);
		LocalDateTime expectedUpperPoint = of(2017, NOVEMBER, 20, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofQuarters(1), expectedLowerPoint, expectedUpperPoint);

		// from base date

		baseDate = poi.minusMonths(1);
		expectedUpperPoint = of(2017, NOVEMBER, 20, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofQuarters(1), baseDate, expectedUpperPoint);
	}

	@Test
	public void shouldCreateSemestersPeriod() {
		LocalDateTime baseDate = poi.minusMonths(7);
		LocalDateTime expectedLowerPoint = of(2017, AUGUST, 20, 10, 30);
		LocalDateTime expectedUpperPoint = of(2018, FEBRUARY, 20, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofSemesters(1), expectedLowerPoint, expectedUpperPoint);

		// from base date

		baseDate = poi.minusMonths(1);
		expectedUpperPoint = of(2018, FEBRUARY, 20, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofSemesters(1), baseDate, expectedUpperPoint);
	}

	@Test
	public void shouldCreateYearsPeriod() {
		LocalDateTime baseDate = poi.minusMonths(29);
		LocalDateTime expectedLowerPoint = of(2017, APRIL, 20, 10, 30);
		LocalDateTime expectedUpperPoint = of(2018, APRIL, 20, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofYears(1), expectedLowerPoint, expectedUpperPoint);

		// from base date

		baseDate = poi.minusMonths(1);
		expectedUpperPoint = of(2018, AUGUST, 20, 10, 29, 59, 999_000_000);

		checkPeriodCreation(baseDate, poi, PeriodDuration.ofYears(1), baseDate, expectedUpperPoint);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationPeriodWithEmptyBaseDate() {
		createPeriod(null, poi, PeriodDuration.ofDays(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationPeriodWithEmptyPoi() {
		createPeriod(LocalDateTime.now(), null, PeriodDuration.ofDays(1));
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailCreationPeriodWhenBaseDateAfterPoi() {
		createPeriod(poi.plusMonths(1), poi, PeriodDuration.ofDays(10));
	}

	private void checkPeriodCreation(LocalDateTime baseDate, LocalDateTime poi, PeriodDuration duration,
			LocalDateTime expectedLowerPoint, LocalDateTime expectedUpperPoint) {
		assertEquals(Range.closed(expectedLowerPoint, expectedUpperPoint), createPeriod(baseDate, poi, duration));
	}

}