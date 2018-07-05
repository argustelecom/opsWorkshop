package ru.argustelecom.box.env.billing.bill.model;

import static java.time.Month.NOVEMBER;
import static java.time.Month.OCTOBER;
import static java.time.Month.SEPTEMBER;

import java.time.LocalDateTime;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import ru.argustelecom.box.env.stl.period.PeriodUnit;

public class BillPeriodTest {

	LocalDateTime poi;

	@Before
	public void setUp() throws Exception {
		poi = LocalDateTime.of(2017, OCTOBER, 17, 11, 28);
	}

	@Test
	public void shouldCreateCalendarianPeriod() {
		LocalDateTime start = LocalDateTime.of(2017, OCTOBER, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2017, OCTOBER, 31, 23, 59, 59, 999_000_000);

		BillPeriod period = BillPeriod.of(PeriodUnit.MONTH, poi);
		checkBoundaries(start, end, period);
	}

	@Test
	public void shouldCreateNextCalendarianPeriod() {
		LocalDateTime start = LocalDateTime.of(2017, NOVEMBER, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2017, NOVEMBER, 30, 23, 59, 59, 999_000_000);

		BillPeriod period = BillPeriod.of(PeriodUnit.MONTH, poi).next();
		checkBoundaries(start, end, period);
	}

	@Test
	public void shouldCreatePrevCalendarianPeriod() {
		LocalDateTime start = LocalDateTime.of(2017, SEPTEMBER, 1, 0, 0);
		LocalDateTime end = LocalDateTime.of(2017, SEPTEMBER, 30, 23, 59, 59, 999_000_000);

		BillPeriod period = BillPeriod.of(PeriodUnit.MONTH, poi).prev();
		checkBoundaries(start, end, period);
	}

	private void checkBoundaries(LocalDateTime start, LocalDateTime end, BillPeriod period) {
		Assert.assertEquals(start, period.startDateTime());
		Assert.assertEquals(end, period.endDateTime());
	}

}