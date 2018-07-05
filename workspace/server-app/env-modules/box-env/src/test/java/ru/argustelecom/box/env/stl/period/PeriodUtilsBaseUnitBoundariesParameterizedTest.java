package ru.argustelecom.box.env.stl.period;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.ldtToStr;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;
import static ru.argustelecom.box.env.stl.period.PeriodType.CALENDARIAN;
import static ru.argustelecom.box.env.stl.period.PeriodType.CUSTOM;
import static ru.argustelecom.box.env.stl.period.PeriodType.DEBUG;
import static ru.argustelecom.box.env.stl.period.PeriodUtils.createBaseUnitBounds;
import static ru.argustelecom.box.env.stl.period.PeriodUtilsBaseUnitBoundariesParameterizedTest.TestCase.calendarian;
import static ru.argustelecom.box.env.stl.period.PeriodUtilsBaseUnitBoundariesParameterizedTest.TestCase.custom;
import static ru.argustelecom.box.env.stl.period.PeriodUtilsBaseUnitBoundariesParameterizedTest.TestCase.debug;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

import com.google.common.collect.Range;

@RunWith(Parameterized.class)
public class PeriodUtilsBaseUnitBoundariesParameterizedTest {

	private TestCase tc;

	public PeriodUtilsBaseUnitBoundariesParameterizedTest(TestCase tc) {
		this.tc = tc;
	}

	@Test
	public void shouldCalculateBasePointBoundaries() {
		Range<LocalDateTime> baseUnitBoundaries = createBaseUnitBounds(tc.getSoi(), tc.getPoi(), tc.getPeriodType());

		assertThat(baseUnitBoundaries, is(notNullValue()));
		assertThat(baseUnitBoundaries.lowerEndpoint(), equalTo(tc.getExpectedBaseUnitStart()));
		assertThat(baseUnitBoundaries.upperEndpoint(), equalTo(tc.getExpectedBaseUnitEnd()));
		assertThat(baseUnitBoundaries.contains(tc.getPoi()), is(true));
	}

	@Parameters(name = "{0}")
	public static List<TestCase> getTestCases() {
		//@formatter:off
		return Arrays.asList(
			// TestCase( 
			//     StartOfInterest, PointOfInterest, 
			//     ExpectedBaseUnitStart, ExpectedBaseUnitEnd 
			// )
			
			calendarian(
				"2017-06-12 13:23:45.456", "2017-10-25 14:01:23.233", 
				"2017-10-25 00:00:00.000", "2017-10-25 23:59:59.999"
			),
			
			custom(
				"2017-06-12 13:23:45.456", "2017-10-25 14:27:23.233", 
				"2017-10-25 14:23:45.456", "2017-10-25 15:23:45.455"
			),
			custom(
				"2017-06-12 13:23:45.456", "2017-10-25 14:23:45.456", 
				"2017-10-25 14:23:45.456", "2017-10-25 15:23:45.455"
			),
			custom(
				"2017-06-12 13:23:45.456", "2017-10-25 15:23:45.455", 
				"2017-10-25 14:23:45.456", "2017-10-25 15:23:45.455"
			),
			custom(
				"2017-06-12 13:23:45.456", "2017-10-25 13:01:18.738", 
				"2017-10-25 12:23:45.456", "2017-10-25 13:23:45.455"
			),
			custom(
				"2017-06-12 23:59:59.999", "2017-10-25 12:00:00.000", 
				"2017-10-25 11:59:59.999", "2017-10-25 12:59:59.998"
			),
			custom(
				"2017-06-12 12:01:02.333", "2017-10-31 23:15:00.000", 
				"2017-10-31 23:01:02.333", "2017-11-01 00:01:02.332"
			),
			custom(
				"2017-06-12 12:01:02.333", "2017-12-31 23:15:00.000", 
				"2017-12-31 23:01:02.333", "2018-01-01 00:01:02.332"
			),
			custom(
				"2017-06-12 12:01:02.333", "2018-01-01 00:01:00.000", 
				"2017-12-31 23:01:02.333", "2018-01-01 00:01:02.332"
			),
			
			debug(
				"2017-06-12 13:23:45.456", "2017-10-25 12:00:00.000", 
				"2017-10-25 11:59:45.456", "2017-10-25 12:00:45.455"
			),
			debug(
				"2017-06-12 13:23:45.456", "2017-10-25 12:00:46.000", 
				"2017-10-25 12:00:45.456", "2017-10-25 12:01:45.455"
			)
		);
		//@formatter:on
	}

	static class TestCase {

		private PeriodType periodType;
		private LocalDateTime soi;
		private LocalDateTime poi;
		private LocalDateTime expectedBaseUnitStart;
		private LocalDateTime expectedBaseUnitEnd;

		public TestCase(PeriodType periodType, String soi, String poi, String expectedBuStart, String expectedBuEnd) {
			this.periodType = periodType;
			this.soi = strToLdt(soi);
			this.poi = strToLdt(poi);
			this.expectedBaseUnitStart = strToLdt(expectedBuStart);
			this.expectedBaseUnitEnd = strToLdt(expectedBuEnd);
		}

		public static TestCase calendarian(String soi, String poi, String expectedBuStart, String expectedBuEnd) {
			return new TestCase(CALENDARIAN, soi, poi, expectedBuStart, expectedBuEnd);
		}

		public static TestCase custom(String soi, String poi, String expectedBuStart, String expectedBuEnd) {
			return new TestCase(CUSTOM, soi, poi, expectedBuStart, expectedBuEnd);
		}

		public static TestCase debug(String soi, String poi, String expectedBuStart, String expectedBuEnd) {
			return new TestCase(DEBUG, soi, poi, expectedBuStart, expectedBuEnd);
		}

		public PeriodType getPeriodType() {
			return periodType;
		}

		public LocalDateTime getSoi() {
			return soi;
		}

		public LocalDateTime getPoi() {
			return poi;
		}

		public LocalDateTime getExpectedBaseUnitStart() {
			return expectedBaseUnitStart;
		}

		public LocalDateTime getExpectedBaseUnitEnd() {
			return expectedBaseUnitEnd;
		}

		@Override
		public String toString() {
			// @formatter:off
			return String.format("'%s' (from: %s)   %s -> [%s  --  %s]", 
				periodType.name(), 
				ldtToStr(soi), 
				ldtToStr(poi), 
				ldtToStr(expectedBaseUnitStart),
				ldtToStr(expectedBaseUnitEnd)
			);
			// @formatter:on	
		}
	}
}
