package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;

import java.time.LocalDateTime;
import java.util.Date;

import org.junit.Test;

// TODO дописать тесты на остальные методы InvoicePlannerConfig
public class InvoicePlannerConfigTest extends AbstractAccountingTest {

	// @formatter:off
	
	@Test(expected = IllegalArgumentException.class)
	public void shouldThrownExceptionWhenPoiIsUndefinedOnMaxOfChargingStartAndPoiComputation() {
		config(null, null, null).maxOfChargingStartAndPoi((Date) null);
	}

	@Test
	public void shouldComputeOnMaxOfChargingStartAndPoi() {
		InvoicePlannerConfig config = config(null, null, null);

		LocalDateTime poi = strToLdt("2017-10-26 00:00:00.000");
		assertThat(
			"Если период планирования не ограничен слева, то максимальным всегда будет POI",
			config.maxOfChargingStartAndPoi(poi), 
			equalTo(poi)
		);

		config.setBoundaries(strToLdt("2017-10-20 00:00:00.000"), null);
		config.prepare();
		assertThat(
			"Если нижняя граница периода предсказателя меньше чем POI, то ожидается POI",
			config.maxOfChargingStartAndPoi(poi), 
			equalTo(poi)
		);

		config.setBoundaries(strToLdt("2017-11-02 00:00:00.000"), null);
		config.prepare();
		assertThat(
			"Если нижняя граница периода предсказателя больше чем POI, ожидается Tc(lowerBound).startDate",
			config.maxOfChargingStartAndPoi(poi), 
			equalTo(strToLdt("2017-11-01 00:00:00.000"))
		);

		config.setBoundaries(strToLdt("2017-10-26 12:00:00.000"), null);
		config.prepare();
		assertThat(
			"Если нижняя граница периода предсказателя больше чем POI, но POI больше чем Tc(lowerBound).startDate " + 
			"то ожидается POI",
			config.maxOfChargingStartAndPoi(poi), 
			equalTo(poi)
		);
	}

}
