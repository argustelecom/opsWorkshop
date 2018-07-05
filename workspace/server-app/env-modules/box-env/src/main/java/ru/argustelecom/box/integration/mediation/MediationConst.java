package ru.argustelecom.box.integration.mediation;

import static lombok.AccessLevel.PRIVATE;

import lombok.NoArgsConstructor;

@NoArgsConstructor(access = PRIVATE)
public final class MediationConst {
	public static final String BILLING_TO_MEDIATION_WS_NSURI = "http://argustelecom.ru/box/service/mediation/billing-to-mediation";
	public static final String BILLING_TO_MEDIATION_CONTEXT_ROOT = "box/service";
	public static final String BILLING_TO_MEDIATION_WS_URL_PATTERN = "/BillingToMediationApi";
	public static final String BILLING_TO_MEDIATION_WS_SERVICE_NAME = "BillingToMediationApiService";
	public static final String BILLING_TO_MEDIATION_WS_PORT_NAME = "BillingToMediationApiPort";
}
