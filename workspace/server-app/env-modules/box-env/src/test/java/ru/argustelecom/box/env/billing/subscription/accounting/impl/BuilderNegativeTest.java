package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;

import java.time.LocalDateTime;

import org.junit.Test;

import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanTimeline;

public class BuilderNegativeTest extends AbstractAccountingTest {

	@Test(expected = IllegalStateException.class)
	public void shouldFallIfPeriodsOfModifiersAreIntersects() {
		InvoicePlanBuilder builder = new InvoicePlanBuilder();
		builder.setChargingPeriod(defaultCp);
		builder.addPriceModifier(priceModifier("2018-01-08 00:00:00.000", "2018-01-11 23:59:59.999", "0.1"));
		builder.addPriceModifier(priceModifier("2018-01-11 00:00:00.000", "2018-01-12 23:59:59.999", "0.1"));
		builder.createModifierWrappers(defaultCp.boundaries());
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFallIfChargingPeriodIsNotDefined() {
		createSufficientlyConfiguredBuilder().setChargingPeriod(null).validateInput();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFallIfRoundingPolicyIsNotDefined() {
		createSufficientlyConfiguredBuilder().setRoundingPolicy(null).validateInput();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFallIfPlannedStartIsNotDefined() {
		createSufficientlyConfiguredBuilder().setPlannedStart((LocalDateTime) null).validateInput();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFallIfPlannedEndIsNotDefined() {
		createSufficientlyConfiguredBuilder().setPlannedEnd((LocalDateTime) null).validateInput();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFallIfTimelineIsNotDefined() {
		createSufficientlyConfiguredBuilder().setTimeline(null).validateInput();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFallIfPlannedStartExcludedFromChargingPeriod() {
		createSufficientlyConfiguredBuilder().setPlannedStart(strToLdt("2018-01-07 00:00:00.000")).validateInput();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFallIfPlannedEndExcludedFromChargingPeriod() {
		createSufficientlyConfiguredBuilder().setPlannedEnd(strToLdt("2018-01-15 00:00:00.000")).validateInput();
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldCheckThatPeriodModifierIsAroundPlannedPeriod() {
		InvoicePlanBuilder builder = createSufficientlyConfiguredBuilder();
		builder.setPeriodModifier(periodModifier("2018-01-08 00:00:00.001", "2018-01-14 23:59:59.999", ""));
		builder.validateInput();
	}

	private InvoicePlanBuilder createSufficientlyConfiguredBuilder() {
		InvoicePlanBuilder builder = new InvoicePlanBuilder();
		builder.setChargingPeriod(defaultCp);
		builder.setRoundingPolicy(RoundingPolicy.UP);
		builder.setPlannedStart(defaultCp.startDateTime());
		builder.setPlannedEnd(defaultCp.endDateTime());
		builder.setTimeline(InvoicePlanTimeline.FUTURE);

		return builder;
	}

}
