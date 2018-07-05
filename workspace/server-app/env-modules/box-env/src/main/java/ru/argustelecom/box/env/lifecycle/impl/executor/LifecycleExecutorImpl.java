package ru.argustelecom.box.env.lifecycle.impl.executor;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId.FINALIZATION;
import static ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId.INITIALIZATION;
import static ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId.ROUTE_DEFINITION;
import static ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId.ROUTE_EXECUTION;
import static ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId.ROUTE_VALIDATION;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.concurrent.NotThreadSafe;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleEndpoint;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecycleExecutor;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;
import ru.argustelecom.box.env.lifecycle.impl.context.LifecycleExecutionCtxImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;
import ru.argustelecom.system.inf.utils.CDIHelper;
import ru.argustelecom.system.inf.validation.ValidationResult;

@NotThreadSafe
public class LifecycleExecutorImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleExecutor<S, O> {

	private static final Logger log = Logger.getLogger(LifecycleExecutorImpl.class);

	private LifecyclePhaseId currentPhaseId;

	private O businessObject;
	private S businessObjectState;
	private List<LifecyclePhaseListener<S, ? super O>> phaseListeners = new ArrayList<>();

	private Date executionDate;

	private LifecycleImpl<S, O> lifecycle;
	private LifecycleRouteImpl<S, O> route;
	private LifecycleEndpointImpl<S, O> endpoint;
	private LifecycleExecutionCtxImpl<S, O> executionContext;
	private ValidationResult<Object> validationResult;

	private boolean busy;
	private Map<LifecyclePhaseId, LifecyclePhase> phases = new EnumMap<>(LifecyclePhaseId.class);

	public LifecycleExecutorImpl(O businessObject, LifecycleImpl<S, O> lifecycle, LifecycleRouteImpl<S, O> route,
			Date executionDate) {
		
		this.businessObject = checkRequiredArgument(businessObject, "businessObject");
		this.businessObjectState = checkRequiredArgument(businessObject.getState(), "businessObjectState");
		this.lifecycle = checkRequiredArgument(lifecycle, "lifecycle");
		this.route = checkRequiredArgument(route, "route");
		this.executionDate = checkRequiredArgument(executionDate, "executionDate");
		
		checkArgument(this.route.canBeginIn(this.businessObjectState), "Route %s can't begin in state %s", this.route,
				this.businessObjectState);

		createPhaseImplementers();
	}

	// ****************************************************************************************************************
	// INTERNAL
	// ****************************************************************************************************************

	protected void setCurrentPhaseId(LifecyclePhaseId currentPhaseId) {
		this.currentPhaseId = currentPhaseId;
	}

	protected void forEachPhaseListener(Consumer<LifecyclePhaseListener<S, ? super O>> action) {
		phaseListeners.forEach(action);
	}

	protected LifecycleImpl<S, O> lifecycle() {
		return lifecycle;
	}

	protected LifecycleRouteImpl<S, O> route() {
		return route;
	}

	protected LifecycleEndpointImpl<S, O> endpoint() {
		return endpoint;
	}

	protected void updateEndpoint(LifecycleEndpointImpl<S, O> endpoint) {
		this.endpoint = endpoint;
	}

	protected LifecycleExecutionCtxImpl<S, O> executionContext() {
		return executionContext;
	}

	protected void updateExecutionContext(LifecycleExecutionCtxImpl<S, O> executionContext) {
		this.executionContext = executionContext;
	}

	protected void updateValidationResult(ValidationResult<Object> validationResult) {
		this.validationResult = validationResult;
	}

	protected LifecyclePhase getPhase(LifecyclePhaseId phaseId) {
		return phases.get(phaseId);
	}

	protected boolean executeBeforePhase(LifecyclePhaseId phaseId) {
		// Этот код никак не связан с потокобезопасностью. busy нужен для того, чтобы гарантировать, что из одной фазы
		// не попросят выполнения другой фазы, что может сломать логику выполнения жизненного цикла
		if (busy) {
			log.warn("The executor is currently performing an another lifecycle phase. Operation skiped");
			return false;
		}

		boolean result = false;
		busy = true;
		try {
			List<LifecyclePhase> executionPlan = createExecutionPlan(phaseId);
			if (executionPlan.isEmpty()) {
				log.warnv("Requested lifecycle phase has already been achieved [{0}<={1}]", phaseId,
						currentPhaseName());
			} else {
				log.debugv("Execution plan: {0}", executionPlan);
				for (LifecyclePhase phase : executionPlan) {
					phase.doPhase(this);
					setCurrentPhaseId(phase.getId());
				}
				result = true;
			}
		} finally {
			busy = false;
		}
		return result;
	}

	protected List<LifecyclePhase> createExecutionPlan(LifecyclePhaseId finalPhaseId) {
		if (currentPhaseId != null && currentPhaseId.greaterOrEquals(finalPhaseId)) {
			return Collections.emptyList();
		}

		List<LifecyclePhase> executionPlan = new ArrayList<>();
		Iterator<LifecyclePhaseId> phaseIdsIterator = phases.keySet().iterator();
		while (phaseIdsIterator.hasNext()) {
			LifecyclePhaseId phaseId = phaseIdsIterator.next();
			if (phaseId.greater(finalPhaseId)) {
				break;
			}
			boolean skipPhase = currentPhaseId != null && currentPhaseId.greaterOrEquals(phaseId);
			if (!skipPhase) {
				executionPlan.add(getPhase(phaseId));
			}
		}

		return executionPlan;
	}

	protected void createPhaseImplementers() {
		phases.put(INITIALIZATION, createPhase(LifecycleInitializationPhase.class));
		phases.put(ROUTE_DEFINITION, createPhase(LifecycleRouteDefinitionPhase.class));
		phases.put(ROUTE_VALIDATION, createPhase(LifecycleRouteValidationPhase.class));
		phases.put(ROUTE_EXECUTION, createPhase(LifecycleRouteExecutionPhase.class));
		phases.put(FINALIZATION, createPhase(LifecycleFinalizationPhase.class));
	}

	protected <T extends LifecyclePhase> T createPhase(Class<T> phaseClass) {
		return CDIHelper.lookupCDIBean(phaseClass);
	}

	private String currentPhaseName() {
		return currentPhaseId != null ? currentPhaseId.name() : "<INITIAL>";
	}

	// ****************************************************************************************************************
	// PULBIC API
	// ****************************************************************************************************************

	@Override
	public LifecyclePhaseId getCurrentPhaseId() {
		return currentPhaseId;
	}

	@Override
	public O getBusinessObject() {
		return businessObject;
	}

	@Override
	public S getBusinessObjectState() {
		return businessObjectState;
	}

	@Override
	public void addPhaseListener(LifecyclePhaseListener<S, ? super O> phaseListener) {
		checkRequiredArgument(phaseListener, "phaseListener");
		this.phaseListeners.add(phaseListener);
	}

	@Override
	public void removePhaseListener(LifecyclePhaseListener<S, ? super O> phaseListener) {
		checkRequiredArgument(phaseListener, "phaseListener");
		this.phaseListeners.remove(phaseListener);
	}

	public Date getExecutionDate() {
		return executionDate;
	}

	@Override
	public Lifecycle<S, O> getLifecycle() {
		return lifecycle();
	}

	@Override
	public LifecycleRoute<S, O> getRoute() {
		return route();
	}

	@Override
	public LifecycleEndpoint<S> getEndpoint() {
		return endpoint();
	}

	@Override
	public ExecutionCtx<S, O> getExecutionContext() {
		return executionContext();
	}

	@Override
	public ValidationResult<Object> getValidationResult() {
		return validationResult;
	}

	@Override
	public boolean initializeRouting() {
		return executeBeforePhase(INITIALIZATION);
	}

	@Override
	public boolean determineRouteEndpoint() {
		return executeBeforePhase(ROUTE_DEFINITION);
	}

	@Override
	public boolean validateRoute() {
		return executeBeforePhase(ROUTE_VALIDATION);
	}

	@Override
	public boolean executeRoute() {
		return executeBeforePhase(ROUTE_EXECUTION);
	}

	@Override
	public boolean finalizeRouting() {
		return executeBeforePhase(FINALIZATION);
	}

	@Override
	public boolean canBackToPreviousPhase() {
		return currentPhaseId != null && currentPhaseId.less(ROUTE_EXECUTION);
	}

	@Override
	public boolean backToPreviousPhase() {
		if (!canBackToPreviousPhase()) {
			log.warnv("Could not back to previous phase. Current: {0}", currentPhaseName());
			return false;
		}

		LifecyclePhase currentPhase = getPhase(currentPhaseId);
		currentPhase.clean(this);

		List<LifecyclePhaseId> phaseList = Arrays.asList(LifecyclePhaseId.values());
		ListIterator<LifecyclePhaseId> phaseIt = phaseList.listIterator(phaseList.indexOf(currentPhaseId));
		currentPhaseId = phaseIt.hasPrevious() ? phaseIt.previous() : null;
		log.debugv("Executor is coming back to phase {0}", currentPhaseName());

		return true;
	}

}
