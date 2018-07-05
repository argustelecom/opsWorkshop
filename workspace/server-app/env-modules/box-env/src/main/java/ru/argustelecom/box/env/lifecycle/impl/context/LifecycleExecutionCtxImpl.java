package ru.argustelecom.box.env.lifecycle.impl.context;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleEndpoint;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleEndpointImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleRouteImpl;
import ru.argustelecom.box.env.lifecycle.impl.definition.LifecycleVariablesImpl.LifecycleVarValues;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;

public class LifecycleExecutionCtxImpl<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		extends LifecycleTestingCtxImpl<S, O> implements ExecutionCtx<S, O> {

	private LifecycleEndpointImpl<S, O> endpoint;
	private LifecycleVarValues valuesHolder;
	private boolean ignoreWarnings = false;
	private Map<Serializable, Object> dataContainer = new HashMap<>();

	public LifecycleExecutionCtxImpl(O businessObject, LifecycleRouteImpl<S, O> route,
			LifecycleEndpointImpl<S, O> endpoint, Date executionDate) {
		super(businessObject, route, executionDate);

		checkRequiredArgument(endpoint, "endpoint");
		checkArgument(route.hasEndpoint(endpoint), "Endpoint %s must belong to the route %s", endpoint, route);

		this.endpoint = endpoint;
		this.valuesHolder = endpoint.variables().createValuesHolder();
	}

	public LifecycleEndpointImpl<S, O> endpoint() {
		return endpoint;
	}

	public LifecycleVarValues valuesHolder() {
		return valuesHolder;
	}

	// ****************************************************************************************************************
	// PULBIC API
	// ****************************************************************************************************************

	@Override
	public LifecycleEndpoint<S> getEndpoint() {
		return endpoint();
	}

	@Override
	public TypeInstance<?> getValuesHolder() {
		return valuesHolder();
	}

	@Override
	public Object getVariable(Serializable keyword) {
		return valuesHolder().getPropertyValue(keyword.toString());
	}

	@Override
	public <V> V getVariable(Serializable keyword, Class<V> valueClass) {
		Object value = getVariable(keyword);
		if (value != null) {
			checkState(valueClass.isInstance(value), "Value '%s' is not instance of '%s'", value, valueClass.getName());
			return valueClass.cast(value);
		}
		return null;
	}

	@Override
	public <V, P extends TypeProperty<V>> V getVariable(P variableDef) {
		return valuesHolder().getPropertyValue(variableDef);
	}

	@Override
	public <V, P extends TypeProperty<V>> V getVariable(LifecycleVariable<P> var) {
		P variableDef = valuesHolder().getType().getProperty(var.type(), var.toString());
		return variableDef != null ? valuesHolder().getPropertyValue(variableDef) : null;
	}

	@Override
	public void setVariable(Serializable keyword, Object value) {
		valuesHolder().setPropertyValue(keyword.toString(), value);
	}

	@Override
	public <V, P extends TypeProperty<V>> void setVariable(P variableDef, V value) {
		valuesHolder().setPropertyValue(variableDef, value);
	}

	@Override
	public <V, P extends TypeProperty<V>> void setVariable(LifecycleVariable<P> var, V value) {
		P variableDef = valuesHolder().getType().getProperty(var.type(), var.toString());
		if (variableDef != null) {
			valuesHolder().setPropertyValue(variableDef, value);
		}
	}

	@Override
	public boolean hasData(Serializable keyword) {
		return dataContainer.containsKey(keyword);
	}

	@Override
	public <T> boolean hasData(Serializable keyword, Class<T> dataClass) {
		return getData(keyword, dataClass) != null;
	}

	@Override
	public Object getData(Serializable keyword) {
		return dataContainer.get(keyword);
	}

	@Override
	public <T> T getData(Serializable keyword, Class<T> dataClass) {
		Object data = getData(keyword);
		if (data != null && dataClass.isInstance(data)) {
			return dataClass.cast(data);
		}
		return null;
	}

	@Override
	public <T> void putData(Serializable keyword, T data) {
		if (data != null) {
			dataContainer.put(keyword, data);
		} else {
			dataContainer.remove(keyword);
		}
	}

	@Override
	public boolean isIgnoreWarnings() {
		return ignoreWarnings;
	}

	@Override
	public void suppressWarnings(boolean value) {
		ignoreWarnings = value;
	}

}
