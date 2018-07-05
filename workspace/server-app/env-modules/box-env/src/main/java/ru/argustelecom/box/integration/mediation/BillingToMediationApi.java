package ru.argustelecom.box.integration.mediation;

import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_WS_NSURI;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebService;

import org.jboss.ws.api.annotation.EndpointConfig;
import ru.argustelecom.box.publang.billing.model.IChargeJob;
import ru.argustelecom.box.publang.billing.model.IUnsuitableCall;
import ru.argustelecom.system.inf.exception.ws.WSBusinessException;
import ru.argustelecom.system.inf.exception.ws.WSSystemException;

import java.util.List;

//@formatter:off
@WebService(targetNamespace = BILLING_TO_MEDIATION_WS_NSURI)
@EndpointConfig(configName="Endpoint-Config-Wo-Schema-Validation")
public interface BillingToMediationApi {
	@WebMethod
	void doRechargeJob(
			@WebParam(targetNamespace = BILLING_TO_MEDIATION_WS_NSURI, name = "chargeJob") IChargeJob chargeJob
	) throws WSSystemException, WSBusinessException;

	@WebMethod
	void processedJob(
			@WebParam(targetNamespace = BILLING_TO_MEDIATION_WS_NSURI, name = "chargeJob") IChargeJob chargeJob
	) throws WSSystemException, WSBusinessException;

	@WebMethod
	void updateUnsuitableCalls(
			@WebParam(targetNamespace = BILLING_TO_MEDIATION_WS_NSURI, name = "calls") List<IUnsuitableCall> calls
	) throws WSSystemException, WSBusinessException;
}
//@formatter:on
