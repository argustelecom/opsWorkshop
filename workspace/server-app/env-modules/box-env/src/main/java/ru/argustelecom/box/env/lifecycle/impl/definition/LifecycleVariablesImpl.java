package ru.argustelecom.box.env.lifecycle.impl.definition;

import static ru.argustelecom.box.env.type.model.TypeCreationalContext.creationalContext;

import java.io.Serializable;
import java.util.Collection;
import java.util.concurrent.atomic.AtomicLong;

import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariables;
import ru.argustelecom.box.env.type.model.Type;
import ru.argustelecom.box.env.type.model.TypeCreationalContext;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;

public class LifecycleVariablesImpl implements LifecycleVariables, Serializable {

	private static final long serialVersionUID = 9107605912053870178L;

	private AtomicLong propertyCounter;
	private AtomicLong valuesHolderCounter;
	private LifecycleVarDef definition;

	private transient TypeCreationalContext<LifecycleVarDef> creationalContext;

	public LifecycleVariablesImpl() {
		propertyCounter = new AtomicLong();
		valuesHolderCounter = new AtomicLong();
		creationalContext = creationalContext(LifecycleVarDef.class);
		definition = creationalContext.createType(1L);
	}

	public static class LifecycleVarDef extends Type {

		private static final long serialVersionUID = 1L;

		protected LifecycleVarDef(Long id) {
			super(id);
		}
	}

	public static class LifecycleVarValues extends TypeInstance<LifecycleVarDef> {

		private static final long serialVersionUID = 1L;

		protected LifecycleVarValues(Long id) {
			super(id);
		}
	}

	public <V, P extends TypeProperty<V>> P defineVariable(Class<P> variableClass, Serializable keyword) {
		Long propertyId = propertyCounter.incrementAndGet();
		return creationalContext.createProperty(definition, variableClass, keyword.toString(), propertyId);
	}

	public <V, P extends TypeProperty<V>> P defineVariable(LifecycleVariable<P> var) {
		Long propertyId = propertyCounter.incrementAndGet();
		return creationalContext.createProperty(definition, var.type(), var.toString(), propertyId);
	}

	public LifecycleVarValues createValuesHolder() {
		Long instanceId = valuesHolderCounter.incrementAndGet();
		return creationalContext.createInstance(definition, LifecycleVarValues.class, instanceId);
	}

	// ****************************************************************************************************************
	// PULBIC API
	// ****************************************************************************************************************

	@Override
	public boolean isEmpty() {
		return definition.isEmpty();
	}

	@Override
	public boolean isDefined(Serializable keyword) {
		return definition.hasProperty(keyword.toString());
	}

	@Override
	public <V, P extends TypeProperty<V>> boolean isDefined(LifecycleVariable<P> var) {
		return definition.hasProperty(var.toString());
	}

	@Override
	public Collection<TypeProperty<?>> getVariableDefs() {
		return definition.getProperties();
	}

	@Override
	public TypeProperty<?> getVariableDef(Serializable keyword) {
		return definition.getProperty(keyword.toString());
	}

	@Override
	public <P extends TypeProperty<?>> P getVariableDef(Class<P> variableClass, Serializable keyword) {
		return definition.getProperty(variableClass, keyword.toString());
	}

	@Override
	public <V, P extends TypeProperty<V>> P getVariableDef(LifecycleVariable<P> var) {
		return definition.getProperty(var.type(), var.toString());
	}

}