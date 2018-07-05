package ru.argustelecom.box.env.pricing.lifecycle.action;

import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.common.AbstractActionWriteComment;
import ru.argustelecom.box.env.pricing.lifecycle.AbstractPricelistLifecycle;
import ru.argustelecom.box.env.pricing.model.AbstractPricelist;
import ru.argustelecom.box.env.pricing.model.PricelistState;

@LifecycleBean
public class DoWriteComment extends AbstractActionWriteComment<PricelistState, AbstractPricelist> {

	@Override
	protected String getContent(ExecutionCtx<PricelistState, ? extends AbstractPricelist> ctx) {
		return ctx.getVariable(AbstractPricelistLifecycle.Variables.COMMENT);
	}
}
