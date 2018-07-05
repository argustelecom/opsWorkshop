package ru.argustelecom.box.integration.billing;

import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PRIVATE;

@NoArgsConstructor(access = PRIVATE)
public final class BillingConst {
	public static final String MEDIATION_TO_BILLING_WS_NSURI = "http://argustelecom.ru/box/service/billing/mediation-to-billing";
	public static final String MEDIATION_TO_BILLING_CONTEXT_ROOT = "box/service";
	public static final String MEDIATION_TO_BILLING_WS_URL_PATTERN = "/MediationToBillingApi";
	public static final String MEDIATION_TO_BILLING_WS_SERVICE_NAME = "MediationToBillingApiService";
	public static final String MEDIATION_TO_BILLING_WS_PORT_NAME = "MediationToBillingApiPort";
}
