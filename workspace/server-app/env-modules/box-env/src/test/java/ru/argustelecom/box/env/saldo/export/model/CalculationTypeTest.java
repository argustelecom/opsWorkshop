package ru.argustelecom.box.env.saldo.export.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.saldo.export.model.CalculationType.AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.DAY;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.MONTH;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.WEEK;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;

import org.junit.Ignore;
import org.junit.Test;

import com.google.common.collect.Range;

public class CalculationTypeTest {

	@Test
	public void testExportDateForMonth() {
		LocalDateTime poi = LocalDateTime.now();

		boolean leapYear = LocalDate.from(poi).isLeapYear();

		LocalDateTime lastDayOfMonth = LocalDateTime.of(poi.getYear(), poi.getMonth(), poi.getMonth().length(leapYear),
				23, 59, 59, 999_000_000);
		LocalDateTime exportDate = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getExportDate(MONTH, poi);
		assertThat(exportDate, equalTo(lastDayOfMonth));
	}

	@Test
	public void testNextExportDateForMonth() {
		LocalDateTime poi = LocalDateTime.now();
		LocalDateTime nextMonth = LocalDateTime.now().plusMonths(1);
		boolean leapYear = LocalDate.from(nextMonth).isLeapYear();
		LocalDateTime lastDayOfNextMonth = LocalDateTime.of(nextMonth.getYear(), nextMonth.getMonth(),
				nextMonth.getMonth().length(leapYear), 23, 59, 59, 999_000_000);
		LocalDateTime nextExportDate = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getNextExportDate(MONTH, poi);
		assertThat(nextExportDate, equalTo(lastDayOfNextMonth));
	}

	@Test
	public void testRangeForMonth() {
		LocalDateTime poi = LocalDateTime.of(2017, Month.FEBRUARY, 28, 0, 0, 0, 0);

		LocalDateTime start = LocalDateTime.of(2017, Month.MARCH, 1, 0, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2017, Month.MARCH, 31, 23, 59, 59, 999_000_000);

		Range<LocalDateTime> range = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getRange(MONTH, poi);
		assertThat(range.lowerEndpoint(), equalTo(start));
		assertThat(range.upperEndpoint(), equalTo(end));
	}

	@Test
	public void testExportDateForWeek() {
		LocalDateTime poi = LocalDateTime.of(2017, Month.FEBRUARY, 3, 0, 0, 0, 0);
		LocalDateTime lastDayOfWeek = LocalDateTime.of(2017, Month.FEBRUARY, 5, 23, 59, 59, 999_000_000);

		LocalDateTime exportDate = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getExportDate(WEEK, poi);
		assertThat(exportDate, equalTo(lastDayOfWeek));
	}

	@Test
	public void testNextExportDateForWeek() {
		LocalDateTime poi = LocalDateTime.of(2017, Month.FEBRUARY, 3, 0, 0, 0, 0);
		LocalDateTime lastDayOfNextWeek = LocalDateTime.of(2017, Month.FEBRUARY, 12, 23, 59, 59, 999_000_000);

		LocalDateTime nextExportDate = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getNextExportDate(WEEK, poi);

		assertThat(nextExportDate, equalTo(lastDayOfNextWeek));
	}

	@Test
	public void testRangeForWeek() {
		LocalDateTime poi = LocalDateTime.of(2017, Month.FEBRUARY, 3, 0, 0, 0, 0);
		LocalDateTime start = LocalDateTime.of(2017, Month.FEBRUARY, 6, 0, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2017, Month.FEBRUARY, 12, 23, 59, 59, 999_000_000);

		Range<LocalDateTime> range = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getRange(WEEK, poi);
		assertThat(range.lowerEndpoint(), equalTo(start));
		assertThat(range.upperEndpoint(), equalTo(end));
	}

	@Test
	public void testExportDateForDay() {
		LocalDateTime poi = LocalDateTime.of(2017, Month.FEBRUARY, 28, 0, 0, 0, 0);
		LocalDateTime nextDay = LocalDateTime.of(2017, Month.FEBRUARY, 28, 23, 59, 59, 999_000_000);

		LocalDateTime exportDate = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getExportDate(DAY, poi);
		assertThat(exportDate, equalTo(nextDay));
	}

	@Test
	public void testNextExportDateForDay() {
		LocalDateTime poi = LocalDateTime.of(2017, Month.FEBRUARY, 27, 0, 0, 0, 0);
		LocalDateTime nextDay = LocalDateTime.of(2017, Month.FEBRUARY, 28, 23, 59, 59, 999_000_000);

		LocalDateTime nextExportDate = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getNextExportDate(DAY, poi);
		assertThat(nextExportDate, equalTo(nextDay));
	}

	@Test
	public void testRangeDay() {
		LocalDateTime poi = LocalDateTime.of(2017, Month.FEBRUARY, 28, 0, 0, 0, 0);
		LocalDateTime start = LocalDateTime.of(2017, Month.MARCH, 1, 0, 0, 0, 0);
		LocalDateTime end = LocalDateTime.of(2017, Month.MARCH, 1, 23, 59, 59, 999_000_000);

		Range<LocalDateTime> range = AT_THE_END_OF_PERIOD_FOR_NEXT_PERIOD.getRange(DAY, poi);
		assertThat(range.lowerEndpoint(), equalTo(start));
		assertThat(range.upperEndpoint(), equalTo(end));
	}

}