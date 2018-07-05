package ru.argustelecom.box.env.lifecycle.api.factory;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

/**
 * TODO [документирование жизненного цикла]
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
@FunctionalInterface
public interface LifecycleContextConfigurator<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	void configure(ExecutionCtx<S, ? extends O> context);

}
