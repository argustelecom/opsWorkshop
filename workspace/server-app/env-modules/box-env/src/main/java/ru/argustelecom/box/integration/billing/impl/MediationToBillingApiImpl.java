package ru.argustelecom.box.integration.billing.impl;

import java.util.Optional;
import java.util.stream.Collectors;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Optional.ofNullable;
import static ru.argustelecom.box.env.billing.invoice.lifecycle.RechargingChargeJobLifecycle.Route.ABORT_FROM_PRE_BILLING;
import static ru.argustelecom.box.env.billing.invoice.lifecycle.RechargingChargeJobLifecycle.Route.SYNCHRONIZE_RECHARGING;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.PERFORMED_PRE_BILLING;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.SYNCHRONIZATION;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.REGULAR;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.SUITABLE;
import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_CONTEXT_ROOT;
import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_WS_NSURI;
import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_WS_PORT_NAME;
import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_WS_SERVICE_NAME;
import static ru.argustelecom.box.integration.billing.BillingConst.MEDIATION_TO_BILLING_WS_URL_PATTERN;
import static ru.argustelecom.box.publang.billing.model.IResult.ERROR;

import javax.annotation.security.RolesAllowed;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.jws.WebService;

import org.jboss.logging.Logger;
import org.jboss.ws.api.annotation.WebContext;

import ru.argustelecom.box.env.activity.comment.CommentRepository;
import ru.argustelecom.box.env.billing.invoice.JobReportService;
import ru.argustelecom.box.env.billing.invoice.lifecycle.ChargeJobRoutingService;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobWrapper;
import ru.argustelecom.box.env.billing.invoice.model.JobDataType;
import ru.argustelecom.box.env.billing.invoice.model.Report;
import ru.argustelecom.box.integration.billing.MediationToBillingApi;
import ru.argustelecom.box.publang.billing.model.IChargeJob;
import ru.argustelecom.box.publang.billing.model.IFilter;
import ru.argustelecom.box.publang.billing.model.IReport;
import ru.argustelecom.box.publang.billing.model.ISummary;
import ru.argustelecom.box.publang.billing.model.IUnsuitable;
import ru.argustelecom.system.inf.exception.ws.WSBusinessException;
import ru.argustelecom.system.inf.exception.ws.WSSystemException;
import ru.argustelecom.system.inf.exception.ws.wrapper.WSWrappedFault;

//@formatter:off
@Stateless
@RolesAllowed({ "User" })
@WSWrappedFault
@WebContext(
		contextRoot = MEDIATION_TO_BILLING_CONTEXT_ROOT,
		urlPattern = MEDIATION_TO_BILLING_WS_URL_PATTERN
)
@WebService(
		targetNamespace = MEDIATION_TO_BILLING_WS_NSURI,
		serviceName = MEDIATION_TO_BILLING_WS_SERVICE_NAME,
		portName = MEDIATION_TO_BILLING_WS_PORT_NAME
)
//@formatter:on
public class MediationToBillingApiImpl implements MediationToBillingApi {

	private static final Logger logger = Logger.getLogger(MediationToBillingApiImpl.class);

	private static final String RECHARGE_PREFIX = "RC";

	@Inject
	private ChargeJobWrapper chargeJobWrapper;

	@Inject
	private ChargeJobRoutingService chargeJobRoutingSvc;

	@Inject
	private CommentRepository commentRp;

	@Inject
	private JobReportService jobReportSvc;

	@Override
	public void completeJob(IChargeJob chargeJob, IReport report) throws WSSystemException, WSBusinessException {
		logger.info("Пришел запрос о завершении задания тарификации");

		checkNotNull(chargeJob);
		checkNotNull(report);

		logIChargeJob(chargeJob);
		logIReport(report);

		String dataType = ofNullable(chargeJob.getDataType())
				.orElse(getDataTypeById(chargeJob.getMediationId()).name());

		chargeJob.setDataType(dataType);

		ChargeJob billChargeJob = chargeJobWrapper.unwrap(chargeJob);

		checkNotNull(report.getResult());

		if (billChargeJob.getDataType().equals(REGULAR)) {
			String commentHeader = jobReportSvc.createCommentHeader(PERFORMED_PRE_BILLING, SYNCHRONIZATION);
			String commentBody = jobReportSvc.createCommentBody(generateReport(report));

			commentRp.writeComment(billChargeJob, commentHeader, commentBody, jobReportSvc.getCommentator());
			return;
		}

		if (!report.getResult().equals(ERROR)) {
			chargeJobRoutingSvc.performRouting(billChargeJob, SYNCHRONIZE_RECHARGING, generateReport(report));
		} else {
			chargeJobRoutingSvc.performRouting(billChargeJob, ABORT_FROM_PRE_BILLING, generateReport(report));
		}

	}

	private JobDataType getDataTypeById(String mediationId) {
		if (mediationId.startsWith(RECHARGE_PREFIX)) {
			return SUITABLE;
		} else {
			return REGULAR;
		}
	}

	private Report generateReport(IReport iReport) {
		Report report = new Report();

		report.setResult(iReport.getResult().getName());

		ofNullable(iReport.getISummary()).ifPresent(iSummary -> {
			report.setTotalProcessed(iSummary.getTotalProcessed());
			report.setSuccess(iSummary.getSuccess());

			ofNullable(iSummary.getUnsuitable()).ifPresent(iUnsuitables -> {

				report.setUnsuitables(iUnsuitables.stream().filter(iUnsuitable -> iUnsuitable.getUnsuitableQuantity()
						!= null && iUnsuitable.getUnsuitableStage() != null)
						.collect(Collectors.toMap(IUnsuitable::getUnsuitableStage, IUnsuitable::getUnsuitableQuantity)));

			});
		});

		return report;
	}

	private void logIChargeJob(IChargeJob chargeJob) {
		logger.infov("> MediationId: {0}", chargeJob.getMediationId());
		logger.infov("> DataType: {0}", chargeJob.getDataType());
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

	private void logIReport(IReport report) {
		logger.infov("> iRechargeJob: {0}", report.getIRechargeJob());
		logger.infov("> Result: {0}", report.getResult());

		Optional<ISummary> summary = ofNullable(report.getISummary());

		summary.ifPresent(summaryInfo -> {
			logger.info("> Summary: ");
			logger.infov("> TotalProcessed: {0}", summaryInfo.getTotalProcessed());
			logger.infov("> Success: {0}", summaryInfo.getSuccess());
			logger.info("> Unsuitable: ");

			if(summaryInfo.getUnsuitable() != null) {
				summaryInfo.getUnsuitable().forEach(unsuitable -> {
					logger.infov("> UnsuitableStage: {0}", unsuitable.getUnsuitableStage());
					logger.infov("> UnsuitableQuantity: {0}", unsuitable.getUnsuitableQuantity());
				});
			}
		});
	}
}
