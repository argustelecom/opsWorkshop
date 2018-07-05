package ru.argustelecom.box.env.lifecycle.impl.context;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Date;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;

public class LifecycleTestingCtxImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements TestingCtx<S, O> {

	private O businessObject;
	private LifecycleRouteImpl<S, O> route;
	private Date executionDate;

	public LifecycleTestingCtxImpl(O businessObject, LifecycleRouteImpl<S, O> route, Date executionDate) {
		this.businessObject = checkRequiredArgument(businessObject, "businessObject");
		this.route = checkRequiredArgument(route, "route");
		this.executionDate = executionDate;
	}

	public LifecycleRouteImpl<S, O> route() {
		return route;
	}

	@Override
	public O getBusinessObject() {
		return businessObject;
	}

	@Override
	public LifecycleRoute<S, O> getRoute() {
		return route();
	}

	@Override
	public Date getExecutionDate() {
		return executionDate;
	}

}
