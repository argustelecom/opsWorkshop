package ru.argustelecom.box.env.lifecycle.api.factory;

import ru.argustelecom.box.env.type.model.TypeProperty;

/**
 * TODO [документирование жизненного цикла]
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
@FunctionalInterface
public interface LifecycleVariableConfigurator<P extends TypeProperty<?>> {

	void configure(P variableDef);

}