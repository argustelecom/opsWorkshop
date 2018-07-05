package ru.argustelecom.box.env.stl.period;

import static java.time.Month.AUGUST;
import static java.time.Month.DECEMBER;
import static java.time.Month.FEBRUARY;
import static java.time.Month.JANUARY;
import static java.time.Month.JULY;
import static java.time.Month.SEPTEMBER;
import static org.junit.Assert.assertEquals;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.DAY;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.HOUR;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.MINUTE;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.MONTH;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.QUARTER;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.SEMESTER;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.WEEK;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.YEAR;

import java.time.LocalDateTime;
import java.util.Arrays;

import org.junit.Test;

import com.google.common.collect.Range;

public class PeriodUnitTest {

	@Test
	public void shouldCreateMinuteBoundary() {
		LocalDateTime expectedLowerEndpoint = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 19, 0);
		LocalDateTime expectedUpperEndpoint = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 19, 59, 999_000_000);

		LocalDateTime lowerPoi = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 19, 0);
		LocalDateTime middlePoi = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 19, 33);
		LocalDateTime upperPoi = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 19, 59);

		checkBoundaries(MINUTE, expectedLowerEndpoint, expectedUpperEndpoint, lowerPoi, middlePoi, upperPoi);
	}

	@Test
	public void shouldCreateHourBoundary() {
		LocalDateTime expectedLowerEndpoint = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 0, 0);
		LocalDateTime expectedUpperEndpoint = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 59, 59, 999_000_000);

		LocalDateTime lowerPoi = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 0, 0);
		LocalDateTime middlePoi = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 30, 2, 343_034);
		LocalDateTime upperPoi = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 59, 59, 999_000_000);

		checkBoundaries(HOUR, expectedLowerEndpoint, expectedUpperEndpoint, lowerPoi, middlePoi, upperPoi);
	}

	@Test
	public void shouldCreateDayBoundary() {
		LocalDateTime expectedLowerEndpoint = LocalDateTime.of(2017, SEPTEMBER, 8, 0, 0, 0);
		LocalDateTime expectedUpperEndpoint = LocalDateTime.of(2017, SEPTEMBER, 8, 23, 59, 59, 999_000_000);

		LocalDateTime lowerPoi = LocalDateTime.of(2017, SEPTEMBER, 8, 0, 0, 0);
		LocalDateTime middlePoi = LocalDateTime.of(2017, SEPTEMBER, 8, 18, 30, 2, 343_034);
		LocalDateTime upperPoi = LocalDateTime.of(2017, SEPTEMBER, 8, 23, 59, 59, 999_000_000);

		checkBoundaries(DAY, expectedLowerEndpoint, expectedUpperEndpoint, lowerPoi, middlePoi, upperPoi);
	}

	@Test
	public void shouldCreateWeekBoundary() {
		LocalDateTime expectedLowerEndpoint = LocalDateTime.of(2017, SEPTEMBER, 4, 0, 0, 0);
		LocalDateTime expectedUpperEndpoint = LocalDateTime.of(2017, SEPTEMBER, 10, 23, 59, 59, 999_000_000);

		LocalDateTime lowerPoi = LocalDateTime.of(2017, SEPTEMBER, 4, 0, 0, 0);
		LocalDateTime middlePoi = LocalDateTime.of(2017, SEPTEMBER, 9, 18, 30, 2, 343_034);
		LocalDateTime upperPoi = LocalDateTime.of(2017, SEPTEMBER, 10, 23, 59, 59, 999_000_000);

		checkBoundaries(WEEK, expectedLowerEndpoint, expectedUpperEndpoint, lowerPoi, middlePoi, upperPoi);
	}

	@Test
	@SuppressWarnings("Duplicates")
	public void shouldCreateMonthBoundary() {
		LocalDateTime expectedLowerEndpoint = LocalDateTime.of(2017, SEPTEMBER, 1, 0, 0, 0);
		LocalDateTime expectedUpperEndpoint = LocalDateTime.of(2017, SEPTEMBER, SEPTEMBER.maxLength(), 23, 59, 59,
				999_000_000);

		LocalDateTime lowerPoi = LocalDateTime.of(2017, SEPTEMBER, 1, 0, 0, 0);
		LocalDateTime middlePoi = LocalDateTime.of(2017, SEPTEMBER, 9, 18, 30, 2, 343_034);
		LocalDateTime upperPoi = LocalDateTime.of(2017, SEPTEMBER, SEPTEMBER.maxLength(), 23, 59, 59, 999_000_000);

		checkBoundaries(MONTH, expectedLowerEndpoint, expectedUpperEndpoint, lowerPoi, middlePoi, upperPoi);
	}

	@Test
	@SuppressWarnings("Duplicates")
	public void shouldCreateQuarterBoundary() {
		LocalDateTime expectedLowerEndpoint = LocalDateTime.of(2017, JULY, 1, 0, 0, 0);
		LocalDateTime expectedUpperEndpoint = LocalDateTime.of(2017, SEPTEMBER, SEPTEMBER.maxLength(), 23, 59, 59,
				999_000_000);

		LocalDateTime lowerPoi = LocalDateTime.of(2017, JULY, 1, 0, 0, 0);
		LocalDateTime middlePoi = LocalDateTime.of(2017, AUGUST, 9, 18, 30, 2, 343_034);
		LocalDateTime upperPoi = LocalDateTime.of(2017, SEPTEMBER, SEPTEMBER.maxLength(), 23, 59, 59, 999_000_000);

		checkBoundaries(QUARTER, expectedLowerEndpoint, expectedUpperEndpoint, lowerPoi, middlePoi, upperPoi);
	}

	@Test
	public void shouldCreateSemesterBoundary() {
		LocalDateTime expectedLowerEndpoint = LocalDateTime.of(2017, JULY, 1, 0, 0, 0);
		LocalDateTime expectedUpperEndpoint = LocalDateTime.of(2017, DECEMBER, DECEMBER.maxLength(), 23, 59, 59,
				999_000_000);

		LocalDateTime lowerPoi = LocalDateTime.of(2017, JULY, 1, 0, 0, 0);
		LocalDateTime middlePoi = LocalDateTime.of(2017, AUGUST, 9, 18, 30, 2, 343_034);
		LocalDateTime upperPoi = LocalDateTime.of(2017, DECEMBER, DECEMBER.maxLength(), 23, 59, 59, 999_000_000);

		checkBoundaries(SEMESTER, expectedLowerEndpoint, expectedUpperEndpoint, lowerPoi, middlePoi, upperPoi);
	}

	@Test
	public void shouldCreateYearBoundary() {
		LocalDateTime expectedLowerEndpoint = LocalDateTime.of(2017, JANUARY, 1, 0, 0, 0);
		LocalDateTime expectedUpperEndpoint = LocalDateTime.of(2017, DECEMBER, DECEMBER.maxLength(), 23, 59, 59,
				999_000_000);

		LocalDateTime lowerPoi = LocalDateTime.of(2017, JANUARY, 1, 0, 0, 0);
		LocalDateTime middlePoi = LocalDateTime.of(2017, AUGUST, 9, 18, 30, 2, 343_034);
		LocalDateTime upperPoi = LocalDateTime.of(2017, DECEMBER, DECEMBER.maxLength(), 23, 59, 59, 999_000_000);

		checkBoundaries(PeriodUnit.YEAR, expectedLowerEndpoint, expectedUpperEndpoint, lowerPoi, middlePoi, upperPoi);
	}

	@Test
	public void between() {
		LocalDateTime start = LocalDateTime.of(2017, JANUARY, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2018, FEBRUARY, 1, 0, 0);

		checkBetween(MINUTE, start, end, 570240);
		checkBetween(HOUR, start, end, 9504);
		checkBetween(DAY, start, end, 396);
		checkBetween(WEEK, start, end, 56);
		checkBetween(MONTH, start, end, 13);
		checkBetween(QUARTER, start, end, 4);
		checkBetween(SEMESTER, start, end, 2);
		checkBetween(PeriodUnit.YEAR, start, end, 1);
	}

	@Test
	public void shouldAddMinutes() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(MINUTE, 5, poi, poi.plusMinutes(5));
		checkAddTo(MINUTE, 33, poi, poi.plusMinutes(33));
		checkAddTo(MINUTE, 525600, poi, poi.plusMinutes(525600));
	}

	@Test
	public void shouldAddHours() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(HOUR, 5, poi, poi.plusHours(5));
		checkAddTo(HOUR, 24, poi, poi.plusHours(24));
		checkAddTo(HOUR, 8760, poi, poi.plusHours(8760));
	}

	@Test
	public void shouldAddDays() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(DAY, 1, poi, poi.plusDays(1));
		checkAddTo(DAY, 31, poi, poi.plusDays(31));
		checkAddTo(DAY, 365, poi, poi.plusDays(365));
	}

	@Test
	public void shouldAddWeeks() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(WEEK, 1, poi, poi.plusWeeks(1));
		checkAddTo(WEEK, 5, poi, poi.plusWeeks(5));
		checkAddTo(WEEK, 60, poi, poi.plusWeeks(60));
	}

	@Test
	public void shouldAddMonth() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(MONTH, 1, poi, poi.plusMonths(1));
		checkAddTo(MONTH, 12, poi, poi.plusMonths(12));
	}

	@Test
	public void shouldAddQuarter() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(QUARTER, 1, poi, poi.plusMonths(3));
		checkAddTo(QUARTER, 2, poi, poi.plusMonths(6));
		checkAddTo(QUARTER, 4, poi, poi.plusMonths(12));
	}

	@Test
	public void shouldAddSemester() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(SEMESTER, 1, poi, poi.plusMonths(6));
		checkAddTo(SEMESTER, 2, poi, poi.plusMonths(12));
		checkAddTo(SEMESTER, 3, poi, poi.plusMonths(18));
	}

	@Test
	public void shouldAddYear() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(YEAR, 1, poi, poi.plusYears(1));
	}

	@Test
	public void shouldAddZero() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(MINUTE, 0, poi, poi);
	}

	@Test
	public void shouldAddNegative() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkAddTo(MINUTE, -1, poi, poi.minusMinutes(1));
	}

	@Test
	public void shouldSubtractMinutes() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(MINUTE, 5, poi, poi.minusMinutes(5));
		checkSubtractFrom(MINUTE, 33, poi, poi.minusMinutes(33));
		checkSubtractFrom(MINUTE, 525600, poi, poi.minusMinutes(525600));
	}

	@Test
	public void shouldSubtractHours() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(HOUR, 5, poi, poi.minusHours(5));
		checkSubtractFrom(HOUR, 24, poi, poi.minusHours(24));
		checkSubtractFrom(HOUR, 8760, poi, poi.minusHours(8760));
	}

	@Test
	public void shouldSubtractDays() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(DAY, 1, poi, poi.minusDays(1));
		checkSubtractFrom(DAY, 31, poi, poi.minusDays(31));
		checkSubtractFrom(DAY, 365, poi, poi.minusDays(365));
	}

	@Test
	public void shouldSubtractWeeks() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(WEEK, 1, poi, poi.minusWeeks(1));
		checkSubtractFrom(WEEK, 5, poi, poi.minusWeeks(5));
		checkSubtractFrom(WEEK, 60, poi, poi.minusWeeks(60));
	}

	@Test
	public void shouldSubtractMonth() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(MONTH, 1, poi, poi.minusMonths(1));
		checkSubtractFrom(MONTH, 12, poi, poi.minusMonths(12));
	}

	@Test
	public void shouldSubtractQuarter() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(QUARTER, 1, poi, poi.minusMonths(3));
		checkSubtractFrom(QUARTER, 2, poi, poi.minusMonths(6));
		checkSubtractFrom(QUARTER, 4, poi, poi.minusMonths(12));
	}

	@Test
	public void shouldSubtractSemester() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(SEMESTER, 1, poi, poi.minusMonths(6));
		checkSubtractFrom(SEMESTER, 2, poi, poi.minusMonths(12));
		checkSubtractFrom(SEMESTER, 3, poi, poi.minusMonths(18));
	}

	@Test
	public void shouldSubtractYear() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(YEAR, 1, poi, poi.minusYears(1));
	}

	@Test
	public void shouldSubtractZero() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(MINUTE, 0, poi, poi);
	}

	@Test
	public void shouldSubtractNegative() {
		LocalDateTime poi = LocalDateTime.of(2017, SEPTEMBER, 27, 12, 29);

		checkSubtractFrom(MINUTE, -1, poi, poi.plusMinutes(1));
	}

	private void checkBoundary(PeriodUnit unit, LocalDateTime expectedLowerEndpoint,
			LocalDateTime expectedUpperEndpoint, LocalDateTime poi) {
		Range<LocalDateTime> result = unit.boundariesOf(poi);
		assertEquals(expectedLowerEndpoint, result.lowerEndpoint());
		assertEquals(expectedUpperEndpoint, result.upperEndpoint());
	}

	private void checkBoundaries(PeriodUnit unit, LocalDateTime expectedLowerEndpoint,
			LocalDateTime expectedUpperEndpoint, LocalDateTime... poiList) {
		Arrays.stream(poiList).forEach(poi -> checkBoundary(unit, expectedLowerEndpoint, expectedUpperEndpoint, poi));
	}

	private void checkBetween(PeriodUnit unit, LocalDateTime start, LocalDateTime end, long expectedDiff) {
		assertEquals(expectedDiff, unit.between(start, end));
	}

	private void checkAddTo(PeriodUnit unit, int amountToAdd, LocalDateTime poi, LocalDateTime expectedDate) {
		assertEquals(expectedDate, unit.addTo(poi, amountToAdd));
	}

	private void checkSubtractFrom(PeriodUnit unit, int amountToAdd, LocalDateTime poi, LocalDateTime expectedDate) {
		assertEquals(expectedDate, unit.subtractFrom(poi, amountToAdd));
	}

}