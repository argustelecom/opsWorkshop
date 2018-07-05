
package ru.argustelecom.box.integration.mediation.impl;

import static lombok.AccessLevel.PRIVATE;
import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_WS_NSURI;
import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_WS_PORT_NAME;
import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_WS_SERVICE_NAME;

import lombok.NoArgsConstructor;
import ru.argustelecom.box.integration.mediation.BillingToMediationApi;
import ru.argustelecom.system.inf.wsclient.CachedWSClient;
import ru.argustelecom.system.inf.wsclient.configuration.CachedWSClientConfiguration;
import ru.argustelecom.system.inf.wsclient.configuration.CachedWSClientRuntimeManagedConfiguration;

@NoArgsConstructor(access = PRIVATE)
public final class BillingToMediationWSClient {

	// Блок настроек веб-сервиса
	private static final String PROP_BILLING_TO_MEDIATION_URL = "box.billing.mediation.endpoint-url";
	private static final String PROP_BILLING_TO_MEDIATION_SVC_NAME = "box.billing.mediation.service-name";
	private static final String PROP_BILLING_TO_MEDIATION_PORT_NAME = "box.billing.mediation.port";
	private static final String PROP_BILLING_TO_MEDIATION_CONNECTION_TIMEOUT = "box.billing.mediation.connection-timeout";
	private static final String PROP_BILLING_TO_MEDIATION_RECEIVE_TIMEOUT = "box.billing.mediation.receive-timeout";
	private static final String PROP_BILLING_TO_MEDIATION_WSDL = "box.billing.mediation.wsdl-location";

	// Instance клиента веб-сервиса
	private static final CachedWSClient<BillingToMediationApi> INSTANCE = new CachedWSClient<>(getConfig());

	public static BillingToMediationApi getEndpoint() {
		return INSTANCE.getEndpoint();
	}

	private static CachedWSClientConfiguration<BillingToMediationApi> getConfig() {
		CachedWSClientRuntimeManagedConfiguration<BillingToMediationApi> config = new CachedWSClientRuntimeManagedConfiguration<>();
		// @formatter:off
		config.setEndpointNamespace(BILLING_TO_MEDIATION_WS_NSURI)
				.setEndpointUrl(PROP_BILLING_TO_MEDIATION_URL)
				.setServiceName(PROP_BILLING_TO_MEDIATION_SVC_NAME)
				.setPortName(PROP_BILLING_TO_MEDIATION_PORT_NAME)
				.setConnectionTimeout(PROP_BILLING_TO_MEDIATION_CONNECTION_TIMEOUT)
				.setReceiveTimeout(PROP_BILLING_TO_MEDIATION_RECEIVE_TIMEOUT)
				.setEndpointInterface(BillingToMediationApi.class)
				.setShouldUseLocalWsdl(true)
				.setWsdlLocation(PROP_BILLING_TO_MEDIATION_WSDL);
		//@formatter:on
		config.setServiceNameDefault(BILLING_TO_MEDIATION_WS_SERVICE_NAME);
		config.setPortNameDefault(BILLING_TO_MEDIATION_WS_PORT_NAME);
		return config;
	}

}
