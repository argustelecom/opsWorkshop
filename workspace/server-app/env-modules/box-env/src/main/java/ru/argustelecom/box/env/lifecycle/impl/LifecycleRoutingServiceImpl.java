package ru.argustelecom.box.env.lifecycle.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRegistry;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRoutingService;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecycleExecutor;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;
import ru.argustelecom.box.env.lifecycle.impl.executor.LifecycleExecutorImpl;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.modelbase.NamedObject;

@DomainService
public class LifecycleRoutingServiceImpl implements LifecycleRoutingService {

	//@formatter:off
	private static final long serialVersionUID = 9143929687636241961L;
	private static final Logger log = Logger.getLogger(LifecycleRoutingServiceImpl.class);

	@Inject
	private LifecycleRegistry registry;
	
	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	void performRouting(O businessObject, LifecycleRoute<S, O> route) {
		performRouting(businessObject, route, null);
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>, L extends LifecyclePhaseListener<S, ? super O>> 
	void performRouting(O businessObject, LifecycleRoute<S, O> route, L phaseListener) {
		LifecycleExecutor<S, O> executor = createExecutor(businessObject, route);
		if (phaseListener != null) {
			executor.addPhaseListener(phaseListener);
		}
		// это последовательно выполнит все фазы жизненного цикла до завершающей
		// см. LifecycleExecutor#finalizeRouting()
		executor.finalizeRouting();
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	void performRouting(O businessObject, Serializable routeKeyword) {
		performRouting(businessObject, routeKeyword, null);
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>, L extends LifecyclePhaseListener<S, ? super O>> 
	void performRouting(O businessObject, Serializable routeKeyword, L phaseListener) {
		LifecycleExecutor<S, O> executor = createExecutor(businessObject, routeKeyword);
		if (phaseListener != null) {
			executor.addPhaseListener(phaseListener);
		}
		// это последовательно выполнит все фазы жизненного цикла до завершающей
		// см. LifecycleExecutor#finalizeRouting()
		executor.finalizeRouting();
	}
	
	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	void performRouting(O businessObject, S nextState, boolean strictRouting) {
		performRouting(businessObject, nextState, strictRouting, null);
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>, L extends LifecyclePhaseListener<S, ? super O>> 
	void performRouting(O businessObject, S nextState, boolean strictRouting, L phaseListener) {
		checkRequiredArgument(businessObject, "businessObject");
		if (businessObject.inState(nextState)) {
			log.warnv("The business object {0} is already in the state {1}", businessObject, nextState);
			return;
		}
		
		LifecycleExecutor<S, O> executor = createExecutor(businessObject, nextState);
		if (phaseListener != null) {
			executor.addPhaseListener(phaseListener);
		}
		// это последовательно выполнит все фазы жизненного цикла до завершающей
		// см. LifecycleExecutor#finalizeRouting()
		executor.finalizeRouting();
		
		if (strictRouting && !businessObject.inState(nextState)) {
			throw new BusinessException(String.format(
				"В соответствии с текущим состоянием '%s' изменение статуса на '%s' невозможно",
				businessObject instanceof NamedObject ? ((NamedObject) businessObject).getObjectName() : businessObject.toString(),
				nextState.getName()		
			));
		}
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleExecutor<S, O> createExecutor(O businessObject, LifecycleRoute<S, O> route) {
		checkRequiredArgument(businessObject, "businessObject");
		checkRequiredArgument(route, "route");
		
		LifecycleImpl<S, O> lifecycleImpl = unwrapLifecycle(businessObject);
		LifecycleRouteImpl<S, O> routeImpl = unwrapRoute(route);
		
		return doCreateExecutor(businessObject, lifecycleImpl, routeImpl);
	}

	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleExecutor<S, O> createExecutor(O businessObject, Serializable routeKeyword) {
		checkRequiredArgument(businessObject, "businessObject");
		checkRequiredArgument(routeKeyword, "routeKeyword");
		
		LifecycleImpl<S, O> lifecycleImpl = unwrapLifecycle(businessObject);
		LifecycleRouteImpl<S, O> routeImpl = lifecycleImpl.findRoute(businessObject.getState(), routeKeyword);
		
		return doCreateExecutor(businessObject, lifecycleImpl, routeImpl);
	}
	
	@Override
	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> LifecycleExecutor<S, O> createExecutor(
			O businessObject, S nextState) {
		checkRequiredArgument(businessObject, "businessObject");
		checkRequiredArgument(nextState, "nextState");
		checkArgument(!businessObject.inState(nextState), "BusinessObject '%s' is already in state '%s'",
				businessObject, nextState);
		
		LifecycleImpl<S, O> lifecycleImpl = unwrapLifecycle(businessObject);
		LifecycleRouteImpl<S, O> routeImpl = lifecycleImpl.findRoute(businessObject.getState(), nextState);
		
		return doCreateExecutor(businessObject, lifecycleImpl, routeImpl);
	}

	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleExecutorImpl<S, O> doCreateExecutor(O businessObject, LifecycleImpl<S, O> lifecycle, LifecycleRouteImpl<S, O> route) {
		return new LifecycleExecutorImpl<>(businessObject, lifecycle, route, new Date());
	}

	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleImpl<S, O> unwrapLifecycle(O businessObject) {
		Lifecycle<S, O> wrappedLifecycle = registry.getLifecycle(businessObject);
		
		checkState(wrappedLifecycle != null);
		checkState(
			wrappedLifecycle instanceof LifecycleImpl, 
			"Unexpected Lifecycle implementation %s", wrappedLifecycle.getClass()
		);
		
		return (LifecycleImpl<S, O>) wrappedLifecycle;
	}

	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleRouteImpl<S, O> unwrapRoute(LifecycleRoute<S, O> route) {
		boolean knownImplementation = route instanceof LifecycleRouteImpl;
		checkArgument(knownImplementation, "Unexpected Lifecycle Route implementation %s", route.getClass());
		return (LifecycleRouteImpl<S, O>) route;
	}
}
