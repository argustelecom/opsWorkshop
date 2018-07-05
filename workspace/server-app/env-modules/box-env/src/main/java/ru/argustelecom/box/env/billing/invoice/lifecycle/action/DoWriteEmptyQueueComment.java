package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import ru.argustelecom.box.env.billing.invoice.JobReportService;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.party.model.role.Employee;

@LifecycleBean
public class DoWriteEmptyQueueComment extends WriteChargeJobComment {

	@Inject
	private JobReportService jobReportSvc;

	@Override
	protected String getContent(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		return StringUtils.EMPTY;
	}

	@Override
	protected Employee getAuthor(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		return jobReportSvc.getCommentator();
	}
}
