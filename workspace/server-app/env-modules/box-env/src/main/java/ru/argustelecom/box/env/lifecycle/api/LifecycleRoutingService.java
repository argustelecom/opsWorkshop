package ru.argustelecom.box.env.lifecycle.api;

import java.io.Serializable;

import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleRoute;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecycleExecutor;
import ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener;

/**
 * Сервис, используемый прикладным кодом для программного управления состоянием бизнес-объекта. Позовляет выполнить
 * прямой переход по указанному маршруту жизненного цикла из текущего состояния.
 * 
 * <p>
 * Пример использования:
 * 
 * <pre>
 * <code>
 * {@literal @}DomainService
 * public class SomeBusinessLogicsService {
 * 
 *    {@literal @}Inject
 *    private LifecycleRoutingService routings;
 *    
 *    public void someBusinessMethod(Sample businessObject) {
 *        ...
 *        checkState(businessObject.getState() != SampleState.CLOSED);
 *        
 *        routings.performRouting(businessObject, SampleLifecycle.Routes.CLOSE);
 *        
 *        checkState(businessObject.getState() == SampleState.CLOSED);
 *        ...
 *    }
 * }
 * </code>
 * </pre>
 */
public interface LifecycleRoutingService extends Serializable {
	//@formatter:off
	
	/**
	 * Выполняет перемещение бизнес-объекта в новое состояние по указанному маршруту. Если маршрут не может начинаться 
	 * в текущем состоянии бизнес-объекта, то будет брошено соответствующее исключение.
	 * 
	 * @param businessObject - бизнес-объект, состояние которого нужно изменить
	 * @param route - маршрут, по которому нужно перейти в новое состояние
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 */
	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	void performRouting(O businessObject, LifecycleRoute<S, O> route);

	/**
	 * Выполняет перемещение бизнес-объекта в новое состояние по указанному маршруту. Дополнительно указывается слушатель
	 * фаз исполнителя жизненного цикла, для того, чтобы была возможность среагировать на то или иное событие. Например, 
	 * заглушить предупреждения валидации либо по завершению перехода (или на определенной фазе) выполнить какие-либо 
	 * произвольные действия.
	 * 
	 * <p>
	 * Если маршрут не может начинаться в текущем состоянии бизнес-объекта, то будет брошено соответствующее исключение.
	 * 
	 * @param businessObject - бизнес-объект, состояние которого нужно изменить
	 * @param route - маршрут, по которому нужно перейти в новое состояние
	 * @param phaseListener - слушатель фаз выполнения жизненного цикла
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * @param <L> - тип слушателя фаз жизненного цикла
	 */
	<S extends LifecycleState<S>, O extends LifecycleObject<S>, L extends LifecyclePhaseListener<S, ? super O>> 
	void performRouting(O businessObject, LifecycleRoute<S, O> route, L phaseListener);

	/**
	 * Выполняет определение маршрута и перемещение бизнес-объекта в новое состояние по этому маршруту. Если маршрут не 
	 * может начинаться в текущем состоянии бизнес-объекта, то будет брошено соответствующее исключение.
	 * 
	 * @param businessObject - бизнес-объект, состояние которого нужно изменить
	 * @param routeKeyword - уникальный идентификатор маршрута, по которому нужно перейти в новое состояние
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 */
	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	void performRouting(O businessObject, Serializable routeKeyword);

	/**
	 * Выполняет определение маршрута и перемещение бизнес-объекта в новое состояние по этому маршруту. Дополнительно 
	 * указывается слушатель фаз исполнителя жизненного цикла, для того, чтобы была возможность среагировать на то или 
	 * иное событие. Например, заглушить предупреждения валидации либо по завершению перехода (или на определенной фазе) 
	 * выполнить какие-либо произвольные действия.
	 * 
	 * <p>
	 * Если маршрут не может начинаться в текущем состоянии бизнес-объекта, то будет брошено соответствующее исключение.
	 * 
	 * @param businessObject - бизнес-объект, состояние которого нужно изменить
	 * @param routeKeyword - уникальный идентификатор маршрута, по которому нужно перейти в новое состояние
	 * @param phaseListener - слушатель фаз выполнения жизненного цикла
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * @param <L> - тип слушателя фаз жизненного цикла
	 */
	<S extends LifecycleState<S>, O extends LifecycleObject<S>, L extends LifecyclePhaseListener<S, ? super O>> 
	void performRouting(O businessObject, Serializable routeKeyword, L phaseListener);
	
	/**
	 * Выполняет определение ближайшего прямого маршрута по текущему состоянию указанного бизнес-объекта и указанному 
	 * конечному состоянию, и выполняет переход по этому маршруту. Если объект не может перейти в указанное состояние 
	 * из своего текущего состояния (например, маршрута такого нет вообще, или он не прямой), то будет брошено 
	 * соответствующее исключение.
	 * 
	 * <p>
	 * Если указан флаг strictRouting, то будет выполнена проверка, что объект находится строго в состоянии nextState, а 
	 * не в каком либо соседнем, достижимом из этого же маршрута но с определенным условием, которое внезапно выполнилось
	 * 
	 * @param businessObject - бизнес-объект, состояние которого нужно изменить
	 * @param nextState - следующее прямое достижимое состояние бизнес-объекта, в которое нужно перейти
	 * @param strictRouting - true если строгий переход. 
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 */
	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	void performRouting(O businessObject, S nextState, boolean strictRouting);
	
	/**
	 * Выполняет определение ближайшего прямого маршрута по текущему состоянию указанного бизнес-объекта и указанному 
	 * конечному состоянию, и выполняет переход по этому маршруту. Дополнительно указывается слушатель фаз исполнителя 
	 * жизненного цикла, для того, чтобы была возможность среагировать на то или иное событие. Например, заглушить 
	 * предупреждения валидации либо по завершению перехода (или на определенной фазе) выполнить какие-либо произвольные 
	 * действия.
	 * 
	 * <p>
	 * Если указанный объект не может перейти в указанное состояние из своего текущего состояния (например, маршрута 
	 * такого нет вообще, или он не прямой), то будет брошено соответствующее исключение.
	 * 
	 * <p>
	 * Если указан флаг strictRouting, то будет выполнена проверка, что объект находится строго в состоянии nextState, а 
	 * не в каком либо соседнем, достижимом из этого же маршрута но с определенным условием, которое внезапно выполнилось
	 * 
	 * @param businessObject - бизнес-объект, состояние которого нужно изменить
	 * @param nextState - следующее прямое достижимое состояние бизнес-объекта, в которое нужно перейти
	 * @param phaseListener - слушатель фаз выполнения жизненного цикла
	 * 
	 * @param <S> - тип состояния бизнес-объекта жизненного цикла
	 * @param <O> - тип бизнес-объекта жизненного цикла
	 * @param <L> - тип слушателя фаз жизненного цикла
	 */
	<S extends LifecycleState<S>, O extends LifecycleObject<S>, L extends LifecyclePhaseListener<S, ? super O>> 
	void performRouting(O businessObject, S nextState, boolean strictRouting, L phaseListener);
	
	/**
	 * Создает экземпляр исполнителя жизненного цикла для указанного бизнес-объекта по указанному маршруту. Исполнитель 
	 * далее может использоваться прикладным или сервисным кодом для непосредственного выполнения жизненного цикла в 
	 * ручном режиме.
	 * 
	 * @param businessObject - бизнес-объект, состояние которого нужно изменить
	 * @param route - маршрут, по которому нужно перейти в новое состояние
	 * 
	 * @return {@linkplain LifecycleExecutor} 
	 */
	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleExecutor<S, O> createExecutor(O businessObject, LifecycleRoute<S, O> route);
	
	/**
	 * Выполняет поиск маршрута по его уникальному идентификатору и создает экземпляр исполнителя жизненного цикла 
	 * для указанного бизнес-объекта по этому маршруту. Исполнитель далее может использоваться прикладным или сервисным 
	 * кодом для непосредственного выполнения жизненного цикла в ручном режиме.
	 * 
	 * @param businessObject - бизнес-объект, состояние которого нужно изменить
	 * @param route - маршрут, по которому нужно перейти в новое состояние
	 * 
	 * @return {@linkplain LifecycleExecutor} 
	 */
	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleExecutor<S, O> createExecutor(O businessObject, Serializable routeKeyword);
	
	<S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleExecutor<S, O> createExecutor(O businessObject, S nextState);

	//@formatter:on
}
