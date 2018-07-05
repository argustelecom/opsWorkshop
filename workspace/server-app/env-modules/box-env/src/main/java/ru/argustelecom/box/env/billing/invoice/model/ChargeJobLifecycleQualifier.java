package ru.argustelecom.box.env.billing.invoice.model;

import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.FORMALIZATION;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.SYNCHRONIZATION;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
public enum ChargeJobLifecycleQualifier {
	SHORT(SYNCHRONIZATION), FULL(FORMALIZATION);

	@Getter
	private ChargeJobState startState;
}
