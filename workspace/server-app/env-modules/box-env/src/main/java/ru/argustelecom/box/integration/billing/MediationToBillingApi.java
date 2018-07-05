package ru.argustelecom.box.integration.billing;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.jboss.ws.api.annotation.EndpointConfig;

import ru.argustelecom.box.publang.billing.model.IChargeJob;
import ru.argustelecom.box.publang.billing.model.IReport;
import ru.argustelecom.system.inf.exception.ws.WSBusinessException;
import ru.argustelecom.system.inf.exception.ws.WSSystemException;

import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_WS_NSURI;

@WebService(targetNamespace = MEDIATION_TO_BILLING_WS_NSURI)
@EndpointConfig(configName="Endpoint-Config-Wo-Schema-Validation")
public interface MediationToBillingApi {

	@WebMethod
	void completeJob(
			@WebParam(targetNamespace = MEDIATION_TO_BILLING_WS_NSURI, name = "chargeJob") IChargeJob chargeJob,
			@WebParam(targetNamespace = MEDIATION_TO_BILLING_WS_NSURI, name = "report") IReport report
	) throws WSSystemException, WSBusinessException;
}
