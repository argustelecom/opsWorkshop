package ru.argustelecom.box.env.stl.period;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;

import org.junit.Test;

// @formatter:off

public class UnboundedPeriodTest {

	@Test
	public void shouldMeetExpectationsToBoundedPeriod() {
		UnboundedPeriod period = UnboundedPeriod.of(
			strToLdt("2017-11-01 12:00:00.000"), 
			strToLdt("2017-11-30 11:59:59.999")
		);
		
		assertThat("Период ограничен с обоих сторон", period.isBounded(), is(true));
		assertThat("Период ограничен слева", period.hasLowerBound(), is(true));
		assertThat("Период ограничен справа", period.hasUpperBound(), is(true));

		assertThat("POI принадлежит периоду", 
			period.contains(strToLdt("2017-11-15 12:00:00.000")), is(true)
		);
		assertThat("POI, эквивалентный левой границе периода, принадлежит периоду", 
			period.contains(strToLdt("2017-11-01 12:00:00.000")), is(true)
		);
		assertThat("POI, эквивалентный правой границе периода, принадлежит периоду", 
			period.contains(strToLdt("2017-11-30 11:59:59.999")), is(true)
		);
		assertThat("POI, находящийся хотя бы на 1мс слева от левой границы не принадлежит периоду", 
			period.contains(strToLdt("2017-11-01 11:59:59.999")), is(false)
		);
		assertThat("POI, находящийся хотя бы на 1мс справа от правой границы не принадлежит периоду", 
			period.contains(strToLdt("2017-11-30 12:00:00.000")), is(false)
		);

		assertThat("#afterLeftBound. True если POI строго больше левой границы периода",
			period.afterLowerBound(strToLdt("2017-11-01 12:00:00.001")), is(true)	
		);
		assertThat("#afterLeftBound. False если POI равна либо меньше левой границы периода",
			period.afterLowerBound(strToLdt("2017-11-01 12:00:00.000")), is(false)	
		);
		assertThat("#afterOrEqualLeftBound. True если POI больше либо равна левой границе периода",
			period.afterOrEqualLowerBound(strToLdt("2017-11-01 12:00:00.000")), is(true)	
		);
		assertThat("#afterRightBound. True если POI строго больше правой границы периода",
			period.afterUpperBound(strToLdt("2017-11-30 12:00:00.000")), is(true)	
		);
		assertThat("#afterRightBound. False, если POI меньше либо равна правой границе периода",
			period.afterUpperBound(strToLdt("2017-11-30 11:59:59.999")), is(false)	
		);
		assertThat("#beforeRightBound. True, если POI строго меньше правой границы периода",
			period.beforeUpperBound(strToLdt("2017-11-30 11:59:59.998")), is(true)	
		);
		assertThat("#beforeRightBound. False, если POI больше либо равна правой границе периода",
			period.beforeUpperBound(strToLdt("2017-11-30 11:59:59.999")), is(false)	
		);
		assertThat("#beforeOrEqualRightBound. True, если POI меньше либо равна правой границе периода",
			period.beforeOrEqualUpperBound(strToLdt("2017-11-30 11:59:59.999")), is(true)	
		);
		assertThat("#beforeLeftBound. True, если POI строго меньше левой границы периода",
			period.beforeLowerBound(strToLdt("2017-11-01 11:59:59.999")), is(true)	
		);
		assertThat("#beforeLeftBound. False, если POI больше либо равна левой границе",
			period.beforeLowerBound(strToLdt("2017-11-01 12:00:00.000")), is(false)	
		);
	}

	@Test
	public void shouldMeetExpectationsToUnboundedFromLowerBoundPeriod() {
		UnboundedPeriod period = UnboundedPeriod.of(
			null, 
			strToLdt("2017-11-30 11:59:59.999")
		);
		
		assertThat("Период не ограничен с обоих сторон", period.isBounded(), is(false));
		assertThat("Период не ограничен слева", period.hasLowerBound(), is(false));
		assertThat("Период ограничен справа", period.hasUpperBound(), is(true));

		assertThat("Любая POI всегда строго больше левой границы периода",
			period.afterLowerBound(strToLdt("1970-01-01 00:00:00.000")), is(true)	
		);
		assertThat("Любая POI всегда больше либо равна левой границе периода",
			period.afterOrEqualLowerBound(strToLdt("1970-01-01 00:00:00.000")), is(true)	
		);
		assertThat("Никакая POI никогда не будет меньше левой границы периода",
			period.beforeLowerBound(strToLdt("0001-01-01 00:00:00.000")), is(false)	
		);
	}
	
	@Test
	public void shouldMeetExpectationsToUnboundedFromUpperBoundPeriod() {
		UnboundedPeriod period = UnboundedPeriod.of(
			strToLdt("2017-11-01 12:00:00.000"),
			null
		);
		
		assertThat("Период не ограничен с обоих сторон", period.isBounded(), is(false));
		assertThat("Период ограничен слева", period.hasLowerBound(), is(true));
		assertThat("Период не ограничен справа", period.hasUpperBound(), is(false));

		assertThat("Любая POI всегда строго меньше правой границы периода",
			period.beforeUpperBound(strToLdt("2117-01-01 00:00:00.000")), is(true)	
		);
		assertThat("Любая POI всегда меньше либо равна правой границы периода",
			period.beforeOrEqualUpperBound(strToLdt("2117-01-01 00:00:00.000")), is(true)	
		);
		assertThat("Никакая POI никогда не будет больше правой границы периода",
			period.afterUpperBound(strToLdt("2999-01-01 00:00:00.000")), is(false)	
		);
	}
}
