package ru.argustelecom.box.env.lifecycle.api.factory;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;

/**
 * TODO [документирование жизненного цикла]
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public interface LifecycleRouteBuilder<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	LifecycleRouteBuilder<S, O> controlledByUser(boolean value);

	LifecycleRouteBuilder<S, O> from(S startpoint);

	LifecycleEndpointBuilder<S, O> to(S endpoint);

	LifecycleBuilder<S, O> end();

}