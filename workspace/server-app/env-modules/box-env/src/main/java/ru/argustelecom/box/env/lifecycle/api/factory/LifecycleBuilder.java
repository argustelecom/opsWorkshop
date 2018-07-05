package ru.argustelecom.box.env.lifecycle.api.factory;

import java.io.Serializable;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.system.inf.modelbase.NamedObject;

/**
 * TODO [документирование жизненного цикла]
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public interface LifecycleBuilder<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	LifecycleBuilder<S, O> keyword(Serializable keyword);
	
	LifecycleBuilder<S, O> name(String name);
	
	LifecycleBuilder<S, O> forbid(S state, Serializable... behaviorKey);
	
	LifecycleBuilder<S, O> configure(LifecycleContextConfigurator<S, O> configurator);
	
	LifecycleRouteBuilder<S, O> route(Serializable routeKeyword, String routeName);
	
	<R extends Serializable & NamedObject> LifecycleRouteBuilder<S, O> route(R routeNamedKeyword);

}