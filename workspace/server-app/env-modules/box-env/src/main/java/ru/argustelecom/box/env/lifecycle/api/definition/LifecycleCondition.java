package ru.argustelecom.box.env.lifecycle.api.definition;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;

/**
 * Описывает условие перехода по одной из веток жизненного цикла. Вызывается фреймворком жизненного цикла в процессе
 * выполнения фазы {@linkplain ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId#ROUTE_DEFINITION
 * ROUTE_DEFINITION}
 * 
 * <p>
 * Этот интерфейс описывает условие, которое может быть реализовано как лямбда либо как анонимный (или конкретный)
 * класс. Отличительная особенность этого типа условий заключается в том, что оно (условие) выполняется не в Enterprise
 * контейнере, а как unmanaged код. Т.е. в нем не будут доступны Managed Beans, PersistenceContext, etc. через
 * стандартную инжекцию зависимостей или ресурсов. Для обращения к управляемым бинам из реализации этого интерфейса
 * необходимо использовать программный инжект через CDIHelper, либо лукап через JNDI, но это не будет считаться хорошей
 * практикой.
 * 
 * <p>
 * Для реализации условия перехода жизненного цикла как managed кода (транзакционного, ограниченного контекстом и с
 * работающей инжекцией) необходимо использовать
 * {@linkplain ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiCondition LifecycleCdiCondition}
 * 
 * <p>
 * Пример реализации LifecycleCondition:
 * 
 * <pre>
 * <code>
 * public class SampleCondition implements LifecycleCondition&lt;SampleState, Sample&gt; {
 *    
 *    {@literal @}Override
 *    public void test(TestingCtx&lt;SampleState, ? extends Sample&gt; ctx) {
 *       LocalDateTime activationTime = ChronoUtils.toLocalDateTime(ctx.getBusinessObject().getActivationTime());
 *       return activationTime.isAfter(LocalDateTime.now());
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
public interface LifecycleCondition<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	/**
	 * Выполняет проверку некоторого утверждения, описанного в реализации этого интерфейса. Вызывается только
	 * фреймворком жизненного цикла на определенной фазе выполнения, не может быть вызвана прикладным кодом.
	 * 
	 * <p>
	 * В качестве аргумента в этот метод передается контекст проверки текущего перехода, из которого реализация операции
	 * сможет извлечь полезные сведения о текущем бизнес-объекте и маршруте перехода в новое состояние и использовать
	 * эту информация непосредственно для выполнения проверки.
	 * 
	 * <p>
	 * В зависимости от результатов выполнения проверки, фреймворк жизненного цикла может принять (если true) или не
	 * принимать (если false) решение о переходе в указанную {@linkplain LifecycleEndpoint конечную точку} маршрута.
	 * 
	 * @param context
	 *            - {@linkplain TestingCtx контекст проверки} текущего перехода
	 */
	boolean test(TestingCtx<S, ? extends O> ctx);

}
