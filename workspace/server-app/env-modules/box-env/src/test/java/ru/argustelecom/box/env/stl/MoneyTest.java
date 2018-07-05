package ru.argustelecom.box.env.stl;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.*;

public class MoneyTest {
	@Test
	public void periodicFractureValue() {
		Money division = new Money(new BigDecimal("10"));
		BigDecimal divider = new BigDecimal("3");

		Money result = division.divide(divider);
		assertEquals(new BigDecimal("3.333333333333"), result.getAmount());
		result = result.multiply(divider);
		assertEquals(new BigDecimal("10.00"), result.getRoundAmount());
	}
}