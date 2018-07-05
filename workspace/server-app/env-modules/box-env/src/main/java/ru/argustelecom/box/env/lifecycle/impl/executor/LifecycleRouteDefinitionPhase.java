package ru.argustelecom.box.env.lifecycle.impl.executor;

import static com.google.common.base.Preconditions.checkState;

import java.util.Date;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.enterprise.context.RequestScoped;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.api.factory.LifecycleContextConfigurator;
import ru.argustelecom.box.env.lifecycle.impl.context.LifecycleExecutionCtxImpl;
import ru.argustelecom.box.env.lifecycle.impl.context.LifecycleTestingCtxImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;

@RequestScoped
class LifecycleRouteDefinitionPhase extends LifecyclePhase {

	private static final long serialVersionUID = 3958457340104814104L;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
	}

	@Override
	@PreDestroy
	protected void preDestroy() {
		super.preDestroy();
	}

	@Override
	public LifecyclePhaseId getId() {
		return LifecyclePhaseId.ROUTE_DEFINITION;
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void clean(LifecycleExecutorImpl<S, O> executor) {
		super.clean(executor);
		executor.updateEndpoint(null);
		executor.updateExecutionContext(null);
	}

	@Override
	protected <S extends LifecycleState<S>, O extends LifecycleObject<S>> void execute(
			LifecycleExecutorImpl<S, O> executor) {

		Date date = executor.getExecutionDate();
		O businessObject = executor.getBusinessObject();
		LifecycleImpl<S, O> lifecycle = executor.lifecycle();
		LifecycleRouteImpl<S, O> route = executor.route();
		LifecycleEndpointImpl<S, O> endpoint = findPreferredEndpoint(businessObject, route, executor);

		LifecycleExecutionCtxImpl<S, O> ctx = new LifecycleExecutionCtxImpl<>(businessObject, route, endpoint, date);
		for (LifecycleContextConfigurator<S, O> configurator : lifecycle.configurators()) {
			configurator.configure(ctx);
		}
		log.debugv("Execution context is created and configured for route {0}", route);

		executor.forEachPhaseListener(listener -> listener.afterRouteDefinition(ctx));
		executor.updateEndpoint(endpoint);
		executor.updateExecutionContext(ctx);
	}

	private <S extends LifecycleState<S>, O extends LifecycleObject<S>> LifecycleEndpointImpl<S, O> findPreferredEndpoint(
			O businessObject, LifecycleRouteImpl<S, O> route, LifecycleExecutorImpl<S, O> executor) {

		Date date = executor.getExecutionDate();
		final LifecycleTestingCtxImpl<S, O> ctx = new LifecycleTestingCtxImpl<>(businessObject, route, date);
		executor.forEachPhaseListener(listener -> listener.beforeRouteDefinition(ctx));

		for (LifecycleEndpointImpl<S, O> endpoint : route.endpoints()) {
			if (!endpoint.isDefault()) {
				checkState(endpoint.hasRoutingCondition());
				if (endpoint.routingCondition().test(ctx)) {
					log.debugv("Found conditional endpoint {0}", endpoint);
					return endpoint;
				}
			}
		}

		checkState(route.hasDefaultEndpoint(), "The default endpoint is not defined for route %s", route);
		log.debugv("Conditional endpoint is not defined, using the default {0}", route.getDefaultEndpoint());
		return route.getDefaultEndpoint();
	}
}
