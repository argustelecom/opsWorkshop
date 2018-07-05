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
@FunctionalInterface
public interface LifecycleFactory<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	void buildLifecycle(LifecycleBuilder<S, O> lifecycle);

}
