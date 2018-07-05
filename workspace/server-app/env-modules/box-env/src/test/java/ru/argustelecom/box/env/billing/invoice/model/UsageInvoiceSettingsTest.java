package ru.argustelecom.box.env.billing.invoice.model;

import static java.time.LocalDateTime.of;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;

import java.time.LocalDateTime;
import java.util.Date;

import org.junit.Test;

import ru.argustelecom.box.env.stl.period.PeriodUnit;

public class UsageInvoiceSettingsTest {

	@Test
	public void shouldBeToday() {
		UsageInvoiceSettings settings = new UsageInvoiceSettings();
		settings.setScheduleUnitAmount(1);
		settings.setScheduleUnit(PeriodUnit.DAY);
		settings.setScheduleStartTime(fromLocalDateTime(of(1970, 1, 1, 15, 0))); // 15:00

		LocalDateTime poi = of(2018, 5, 16, 10, 0);
		Date expected = fromLocalDateTime(of(2018, 5, 16, 15, 0));

		assertThat(settings.nextScheduledTime(poi), equalTo(expected));
	}

	@Test
	public void shouldBeTomorrow() {
		UsageInvoiceSettings settings = new UsageInvoiceSettings();
		settings.setScheduleUnitAmount(1);
		settings.setScheduleUnit(PeriodUnit.DAY);
		settings.setScheduleStartTime(fromLocalDateTime(of(1970, 1, 1, 15, 0))); // 15:00

		LocalDateTime poi = of(2018, 5, 16, 15, 1);
		Date expected = fromLocalDateTime(of(2018, 5, 17, 15, 0));

		assertThat(settings.nextScheduledTime(poi), equalTo(expected));
	}

	@Test
	public void shouldBeTomorrowPartTwo() {
		UsageInvoiceSettings settings = new UsageInvoiceSettings();
		settings.setScheduleUnitAmount(1);
		settings.setScheduleUnit(PeriodUnit.DAY);
		settings.setScheduleStartTime(fromLocalDateTime(of(1970, 1, 1, 23, 55))); // 23:55

		LocalDateTime poi = of(2018, 5, 16, 0, 1);
		Date expected = fromLocalDateTime(of(2018, 5, 16, 23, 55));

		assertThat(settings.nextScheduledTime(poi), equalTo(expected));
	}

	@Test
	public void shouldBeOnTheNextWeek() {
		UsageInvoiceSettings settings = new UsageInvoiceSettings();
		settings.setScheduleUnitAmount(1);
		settings.setScheduleUnit(PeriodUnit.WEEK);
		settings.setScheduleStartTime(fromLocalDateTime(of(1970, 1, 1, 11, 30))); // 11:30

		LocalDateTime poi = of(2018, 5, 17, 12, 1);
		Date expected = fromLocalDateTime(of(2018, 5, 24, 11, 30));

		assertThat(settings.nextScheduledTime(poi), equalTo(expected));
	}

}
