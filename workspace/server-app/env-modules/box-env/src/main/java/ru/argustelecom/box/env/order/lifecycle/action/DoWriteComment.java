package ru.argustelecom.box.env.order.lifecycle.action;

import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.common.AbstractActionWriteComment;
import ru.argustelecom.box.env.order.lifecycle.OrderLifecycle;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.model.OrderState;

@LifecycleBean
public class DoWriteComment extends AbstractActionWriteComment<OrderState, Order> {

	@Override
	protected String getContent(ExecutionCtx<OrderState, ? extends Order> ctx) {
		return ctx.getVariable(OrderLifecycle.Variables.COMMENT);
	}

}
