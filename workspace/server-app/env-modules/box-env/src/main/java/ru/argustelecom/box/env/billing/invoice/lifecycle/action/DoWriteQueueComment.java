package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import javax.inject.Inject;


import ru.argustelecom.box.env.billing.invoice.JobReportService;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.model.Report;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.party.model.role.Employee;

import static ru.argustelecom.box.env.billing.invoice.lifecycle.ChargeJobRoutingService.REPORT_KEYWORD;

@LifecycleBean
public class DoWriteQueueComment extends WriteChargeJobComment {

	@Inject
	private JobReportService jobReportSvc;

	@Override
	protected String getContent(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {

		if (ctx.getData(REPORT_KEYWORD) instanceof Report) {
			Report report = (Report) ctx.getData(REPORT_KEYWORD);
			return jobReportSvc.createCommentBody(report);
		}

		return super.getContent(ctx);
	}

	@Override
	protected Employee getAuthor(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		return jobReportSvc.getCommentator();
	}
}
