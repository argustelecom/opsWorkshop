package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import ru.argustelecom.box.env.stl.period.ChargingPeriod;

public abstract class AbstractAccountingParameterizedTestCase {

	private String name;
	private ChargingPeriod cp;

	protected AbstractAccountingParameterizedTestCase(String name, ChargingPeriod cp) {
		this.name = name;
		this.cp = cp;
	}

	public ChargingPeriod getCp() {
		return cp;
	}

	@Override
	public String toString() {
		return name;
	}

}
