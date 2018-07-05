package ru.argustelecom.box.env.billing.invoice.lifecycle.action;

import org.apache.commons.lang.StringUtils;

import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.party.model.role.Employee;

@LifecycleBean
public class DoWriteEmptyComment extends WriteChargeJobComment {

	@Override
	protected String getContent(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		return StringUtils.EMPTY;
	}

	@Override
	protected Employee getAuthor(ExecutionCtx<ChargeJobState, ? extends ChargeJob> ctx) {
		return super.getAuthor(ctx);
	}
}
