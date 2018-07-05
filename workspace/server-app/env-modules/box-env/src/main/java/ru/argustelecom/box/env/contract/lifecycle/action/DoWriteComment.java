package ru.argustelecom.box.env.contract.lifecycle.action;

import ru.argustelecom.box.env.contract.lifecycle.AbstractContractLifecycle;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.ContractState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.common.AbstractActionWriteComment;

@LifecycleBean
public class DoWriteComment extends AbstractActionWriteComment<ContractState, AbstractContract<?>> {

	@Override
	protected String getContent(ExecutionCtx<ContractState, ? extends AbstractContract<?>> ctx) {
		return ctx.getVariable(AbstractContractLifecycle.Variables.COMMENT);
	}

}
