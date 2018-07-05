package ru.argustelecom.box.integration.mediation.impl;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_CONTEXT_ROOT;
import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_WS_NSURI;
import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_WS_PORT_NAME;
import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_WS_SERVICE_NAME;
import static ru.argustelecom.box.integration.mediation.MediationConst.BILLING_TO_MEDIATION_WS_URL_PATTERN;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.jws.WebService;

import org.jboss.logging.Logger;
import org.jboss.ws.api.annotation.WebContext;

import ru.argustelecom.box.integration.mediation.BillingToMediationApi;
import ru.argustelecom.box.publang.billing.model.IChargeJob;
import ru.argustelecom.box.publang.billing.model.IFilter;
import ru.argustelecom.box.publang.billing.model.IUnsuitableCall;
import ru.argustelecom.system.inf.exception.ws.WSBusinessException;
import ru.argustelecom.system.inf.exception.ws.WSSystemException;
import ru.argustelecom.system.inf.exception.ws.wrapper.WSWrappedFault;

//@formatter:off
@Stateless
@RolesAllowed({ "User" })
@WSWrappedFault
@WebContext(
		contextRoot = BILLING_TO_MEDIATION_CONTEXT_ROOT,
		urlPattern = BILLING_TO_MEDIATION_WS_URL_PATTERN
)
@WebService(
		targetNamespace = BILLING_TO_MEDIATION_WS_NSURI,
		serviceName = BILLING_TO_MEDIATION_WS_SERVICE_NAME,
		portName = BILLING_TO_MEDIATION_WS_PORT_NAME
)
//@formatter:on
public class BillingToMediationApiStub implements BillingToMediationApi {

	private static final Logger logger = Logger.getLogger(BillingToMediationApiStub.class);

	@Override
	public void doRechargeJob(IChargeJob chargeJob) throws WSSystemException, WSBusinessException {
		logger.info("Пришел запрос о необходимости инициировать задание повторной обработки на заглушку");
		logIChargeJob(chargeJob);
	}

	@Override
	public void processedJob(IChargeJob chargeJob) throws WSSystemException, WSBusinessException {
		logger.info(
				"Пришел запрос о завершении обработки данных на стороне АСР, что позволяет произвести очистку буфера "
						+ "от данных, которые были обработаны в рамках указанного chargeJob.Id на заглушку");
		logIChargeJob(chargeJob);
	}

	@Override
	public void updateUnsuitableCalls(List<IUnsuitableCall> calls) throws WSSystemException, WSBusinessException {
		logger.info("Пришел запрос об обновлении данных отсева");
		logIUnsuitableCalls(calls);
	}

	private void logIChargeJob(IChargeJob chargeJob) {
		checkRequiredArgument(chargeJob, "chargeJob");
		logger.infov("> MediationId: {0}", checkRequiredArgument(chargeJob.getMediationId(), "mediationId"));
		logger.infov("> DataType: {0}", checkRequiredArgument(chargeJob.getDataType(), "dataType"));
		logIFilter(chargeJob.getFilter());
	}

	private void logIFilter(IFilter filter) {
		if (filter != null) {
			logger.infov("> DateFrom: {0}", filter.getDateFrom());
			logger.infov("> DateTo: {0}", filter.getDateTo());
			logger.infov("> TariffId: {0}", filter.getTariffId());
			logger.infov("> ServiceId: {0}", filter.getServiceId());
			logger.infov("> ProcessingStage: {0}", filter.getProcessingStage());
		} else {
			logger.info("> Фильтр для выборки: null");
		}
	}

	private void logIUnsuitableCalls(List<IUnsuitableCall> calls) {
		for (IUnsuitableCall call : calls) {
			logger.info("> call");
			logger.infov(">> CallId: {0}", call.getCallId());
			logger.infov(">> CallDate: {0}", call.getCallDate());
			logger.infov(">> CallDirection: {0}", call.getCallDirection());
			logger.infov(">> Duration: {0}", call.getDuration());
			logger.infov(">> CdrUnit: {0}", call.getCdrUnit());
			logger.infov(">> CallingNumber: {0}", call.getCallingNumber());
			logger.infov(">> CalledNumber: {0}", call.getCalledNumber());
			logger.infov(">> OutgoingChannel: {0}", call.getOutgoingChannel());
			logger.infov(">> OutgoingTrunk: {0}", call.getOutgoingTrunk());
			logger.infov(">> IncomingChannel: {0}", call.getIncomingChannel());
			logger.infov(">> IncomingTrunk: {0}", call.getIncomingTrunk());
			logger.infov(">> ProcessingStage: {0}", call.getProcessingStage());
		}
	}
}
