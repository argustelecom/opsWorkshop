package ru.argustelecom.box.env.lifecycle.api.context;

import java.io.Serializable;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleEndpoint;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleVariable;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;

/**
 * TODO [документирование жизненного цикла]
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public interface ExecutionCtx<S extends LifecycleState<S>, O extends LifecycleObject<S>> extends TestingCtx<S, O> {

	LifecycleEndpoint<S> getEndpoint();

	TypeInstance<?> getValuesHolder();

	Object getVariable(Serializable keyword);

	<V> V getVariable(Serializable keyword, Class<V> valueClass);

	<V, P extends TypeProperty<V>> V getVariable(P variableDef);

	<V, P extends TypeProperty<V>> V getVariable(LifecycleVariable<P> var);

	void setVariable(Serializable keyword, Object value);

	<V, P extends TypeProperty<V>> void setVariable(P variableDef, V value);

	<V, P extends TypeProperty<V>> void setVariable(LifecycleVariable<P> var, V value);

	boolean hasData(Serializable keyword);

	<T> boolean hasData(Serializable keyword, Class<T> dataClass);

	Object getData(Serializable keyword);

	<T> T getData(Serializable keyword, Class<T> dataClass);

	<T> void putData(Serializable keyword, T data);

	boolean isIgnoreWarnings();

	default void suppressWarnings() {
		suppressWarnings(true);
	}

	void suppressWarnings(boolean value);

}
