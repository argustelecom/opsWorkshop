package ru.argustelecom.box.env.lifecycle.api.definition;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;

/**
 * Описывает операцию жизненного цикла. Вызывается фреймворком жизненного цикла в процессе выполнения фазы
 * {@linkplain ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId#ROUTE_EXECUTION ROUTE_EXECUTION}
 * 
 * <p>
 * Этот интерфейс описывает операцию, которая может быть реализована как лямбда либо как анонимный (или конкретный)
 * класс. Отличительная особенность этого типа операций заключается в том, что она (операция) выполняется не в
 * Enterprise контейнере, а как unmanaged код. Т.е. в ней не будут доступны Managed Beans, PersistenceContext, etc.
 * через стандартную инжекцию зависимостей или ресурсов. Для обращения к управляемым бинам из реализации этого
 * интерфейса необходимо использовать программный инжект через CDIHelper, либо лукап через JNDI, но это не будет
 * считаться хорошей практикой.
 * 
 * <p>
 * Для реализации операции жизненного цикла как managed кода (транзакционного, ограниченного контекстом и с работающей
 * инжекцией) необходимо использовать {@linkplain ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction
 * LifecycleCdiAction}
 * 
 * <p>
 * Пример реализации LifecycleAction:
 * 
 * <pre>
 * <code>
 * public class SampleAction implements LifecycleAction&lt;SampleState, Sample&gt; {
 *    
 *    {@literal @}Override
 *    public void execute(ExecutionCtx&lt;SampleState, ? extends Sample&gt; ctx) {
 *       Sample businessObject = ctx.getBusinessObject();  
 *       if (businessObject.hasSomeState()) {
 *           businessObject.doSomeAction();
 *           businessObject.setSomeDateProperty(new Date());
 *       }
 *    }
 *    
 * }
 * </code>
 * </pre>
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
@FunctionalInterface
public interface LifecycleAction<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	/**
	 * Выполняет конкретную бизнес-операцию жизненного цикла, описанную в реализации этого интерфейса. Вызывается только
	 * фреймворком жизненного цикла на определенной фазе выполнения, не может быть вызвана прикладным кодом.
	 * 
	 * <p>
	 * В качестве аргумента в этот метод передается контекст выполнения, из которого реализация операции сможет извлечь
	 * полезные сведения о текущем бизнес-объекте, маршруте перехода в новое состояние, переменных жизненного цикла и
	 * т.д.
	 * 
	 * @param context
	 *            - {@linkplain ExecutionCtx контекст выполнения} текущего перехода
	 */
	void execute(ExecutionCtx<S, ? extends O> ctx);

}
