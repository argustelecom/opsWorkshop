
package ru.argustelecom.box.integration.billing.impl;

import static lombok.AccessLevel.PRIVATE;
import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_WS_NSURI;
import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_WS_PORT_NAME;
import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_WS_SERVICE_NAME;

import lombok.NoArgsConstructor;

import ru.argustelecom.box.integration.billing.MediationToBillingApi;
import ru.argustelecom.system.inf.wsclient.CachedWSClient;
import ru.argustelecom.system.inf.wsclient.configuration.CachedWSClientConfiguration;
import ru.argustelecom.system.inf.wsclient.configuration.CachedWSClientRuntimeManagedConfiguration;

@NoArgsConstructor(access = PRIVATE)
public final class MediationToBillingWSClient {

	// Блок настроек веб-сервиса
	private static final String PROP_MEDIATION_TO_BILLING_URL = "box.mediation.billing.endpoint-url";
	private static final String PROP_MEDIATION_TO_BILLING_SVC_NAME = "box.mediation.billing.service-name";
	private static final String PROP_MEDIATION_TO_BILLING_PORT_NAME = "box.mediation.billing.port";
	private static final String PROP_MEDIATION_TO_BILLING_CONNECTION_TIMEOUT = "box.mediation.billing.connection-timeout";
	private static final String PROP_MEDIATION_TO_BILLING_RECEIVE_TIMEOUT = "box.mediation.billing.receive-timeout";
	private static final String PROP_MEDIATION_TO_BILLING_WSDL = "box.mediation.billing.wsdl-location";

	// Instance клиента веб-сервиса
	private static final CachedWSClient<MediationToBillingApi> INSTANCE = new CachedWSClient<>(getConfig());

	public static MediationToBillingApi getPort() {
		return INSTANCE.getEndpoint();
	}

	private static CachedWSClientConfiguration<MediationToBillingApi> getConfig() {
		CachedWSClientRuntimeManagedConfiguration<MediationToBillingApi> config = new CachedWSClientRuntimeManagedConfiguration<>();
		// @formatter:off
		config.setEndpointNamespace(MEDIATION_TO_BILLING_WS_NSURI)
				.setEndpointUrl(PROP_MEDIATION_TO_BILLING_URL)
				.setServiceName(PROP_MEDIATION_TO_BILLING_SVC_NAME)
				.setPortName(PROP_MEDIATION_TO_BILLING_PORT_NAME)
				.setConnectionTimeout(PROP_MEDIATION_TO_BILLING_CONNECTION_TIMEOUT)
				.setReceiveTimeout(PROP_MEDIATION_TO_BILLING_RECEIVE_TIMEOUT)
				.setEndpointInterface(MediationToBillingApi.class)
				.setShouldUseLocalWsdl(true)
				.setWsdlLocation(PROP_MEDIATION_TO_BILLING_WSDL);
		//@formatter:on
		config.setServiceNameDefault(MEDIATION_TO_BILLING_WS_SERVICE_NAME);
		config.setPortNameDefault(MEDIATION_TO_BILLING_WS_PORT_NAME);
		return config;
	}

}
