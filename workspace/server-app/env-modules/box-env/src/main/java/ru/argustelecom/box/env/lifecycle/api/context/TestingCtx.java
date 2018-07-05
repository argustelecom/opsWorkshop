package ru.argustelecom.box.env.lifecycle.api.context;

import java.util.Date;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;

/**
 * 
 *
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public interface TestingCtx<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	O getBusinessObject();

	LifecycleRoute<S, O> getRoute();
	
	Date getExecutionDate();

}
