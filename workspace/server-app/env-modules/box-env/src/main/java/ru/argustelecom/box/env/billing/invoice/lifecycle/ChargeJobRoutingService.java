package ru.argustelecom.box.env.billing.invoice.lifecycle;

import lombok.Setter;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.DONE;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.PERFORMED_BILLING;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobState.SYNCHRONIZED;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.REGULAR;

import java.io.Serializable;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.model.Report;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.inf.queue.api.QueueStat;
import ru.argustelecom.box.inf.queue.api.QueueStat.Statistic;
import ru.argustelecom.box.inf.service.DomainService;

@DomainService
public class ChargeJobRoutingService implements Serializable {

	private static final long serialVersionUID = -2739525875498859634L;

	public static final String REPORT_KEYWORD = "MEDIATION_REPORT";

	@Inject
	private LifecycleRoutingService routingSrv;

	@Inject
	private QueueStat queueStat;

	public void synchronize(ChargeJob job) {
		checkNotNull(job);

		ChargeJobState nextState = job.getDataType() == REGULAR ? SYNCHRONIZED : PERFORMED_BILLING;
		routingSrv.performRouting(job, nextState, true);
	}

	public void tryClose(ChargeJob job) {
		checkNotNull(job);
		checkState(job.getDataType() != REGULAR);

		// Задача уже выполняется в биллинге, синхронизировать не надо. Проверим, что с ней нет связанных
		// активных задач, есть только выполненные и проваленные
		Statistic stat = queueStat.gatherByGroup(job.getId().toString());
		if (stat.isCompleted()) {
			routingSrv.performRouting(job, DONE, true);
		}
	}

	public void performRouting(ChargeJob chargeJob, RechargingChargeJobLifecycle.Route route, Report report) {
		ChargeJobAttachReport chargeJobAttachReport = new ChargeJobAttachReport(report);

		routingSrv.performRouting(chargeJob, route, chargeJobAttachReport);
	}

	private static class ChargeJobAttachReport extends LifecyclePhaseListener<ChargeJobState, ChargeJob> {

		@Setter
		private Report report;

		public ChargeJobAttachReport(Report report) {
			this.report = report;
		}

		@Override
		public void beforeRouteValidation(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
			super.beforeRouteValidation(ctx);
			ctx.putData(REPORT_KEYWORD, report);
		}

		private static final Logger log = Logger.getLogger(ChargeJobAttachReport.class);
	}
}
