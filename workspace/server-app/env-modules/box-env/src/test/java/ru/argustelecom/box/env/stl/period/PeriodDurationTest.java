package ru.argustelecom.box.env.stl.period;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import ru.argustelecom.box.env.stl.period.PeriodDuration;

public class PeriodDurationTest {

	@Test
	public void shouldBeEquivalentToPeriodsWithBothAccurateUnits() {
		PeriodDuration d1, d2;
		{
			d1 = PeriodDuration.ofHours(48);
			d2 = PeriodDuration.ofDays(2);

			assertFalse(d1.getUnit().isEstimatedDuration());
			assertFalse(d2.getUnit().isEstimatedDuration());

			assertTrue("2 days equivalent to 48 hours", d1.equals(d2));
		}
	}

	@Test
	public void shouldBeEquivalentToPeriodsWithBothEstimatedUnits() {
		PeriodDuration d1, d2;
		{
			d1 = PeriodDuration.ofMonths(3);
			d2 = PeriodDuration.ofQuarters(1);

			assertTrue(d1.getUnit().isEstimatedDuration());
			assertTrue(d2.getUnit().isEstimatedDuration());

			assertTrue("3 months is equivalent to 1 quarter", d1.equals(d2));
		}

		{
			d1 = PeriodDuration.ofMonths(6);
			d2 = PeriodDuration.ofSemesters(1);

			assertTrue(d1.getUnit().isEstimatedDuration());
			assertTrue(d2.getUnit().isEstimatedDuration());

			assertTrue("6 months is equivalent to 1 semester", d1.equals(d2));
		}

		{
			d1 = PeriodDuration.ofMonths(12);
			d2 = PeriodDuration.ofYears(1);

			assertTrue(d1.getUnit().isEstimatedDuration());
			assertTrue(d2.getUnit().isEstimatedDuration());

			assertTrue("12 months is equivalent to 1 year", d1.equals(d2));
		}

		{
			d1 = PeriodDuration.ofQuarters(4);
			d2 = PeriodDuration.ofYears(1);

			assertTrue(d1.getUnit().isEstimatedDuration());
			assertTrue(d2.getUnit().isEstimatedDuration());

			assertTrue("4 quarters is equivalent to 1 year", d1.equals(d2));
		}

		{
			d1 = PeriodDuration.ofQuarters(2);
			d2 = PeriodDuration.ofSemesters(1);

			assertTrue(d1.getUnit().isEstimatedDuration());
			assertTrue(d2.getUnit().isEstimatedDuration());

			assertTrue("2 quarters is equivalent to 1 semester", d1.equals(d2));
		}

		{
			d1 = PeriodDuration.ofSemesters(2);
			d2 = PeriodDuration.ofYears(1);

			assertTrue(d1.getUnit().isEstimatedDuration());
			assertTrue(d2.getUnit().isEstimatedDuration());

			assertTrue("2 semesters is equivalent to 1 year", d1.equals(d2));
		}
	}

	@Test
	public void shouldBeNotEquivalentToPeriodsWithEstimatedAndAccurateUnits() {
		PeriodDuration d1, d2;
		{
			d1 = PeriodDuration.ofDays(30);
			d2 = PeriodDuration.ofMonths(1);

			assertFalse(d1.getUnit().isEstimatedDuration());
			assertTrue(d2.getUnit().isEstimatedDuration());

			assertFalse("1 month is not equivalent to 30 days", d1.equals(d2));
		}
	}
}
