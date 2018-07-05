package ru.argustelecom.box.env.lifecycle.api.definition;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.system.inf.validation.ValidationResult;

/**
 * Описывает правило валидации жизненного цикла. Вызывается фреймворком жизненного цикла в процессе выполнения фазы
 * {@linkplain ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseId#ROUTE_VALIDATION ROUTE_VALIDATION}
 * 
 * <p>
 * Этот интерфейс описывает правило валидации, которое может быть реализовано как лямбда либо как анонимный (или
 * конкретный) класс. Отличительная особенность этого типа правил валидации заключается в том, что оно (правило
 * валидации) выполняется не в Enterprise контейнере, а как unmanaged код. Т.е. в нем не будут доступны Managed Beans,
 * PersistenceContext, etc. через стандартную инжекцию зависимостей или ресурсов. Для обращения к управляемым бинам из
 * реализации этого интерфейса необходимо использовать программный инжект через CDIHelper, либо лукап через JNDI, но это
 * не будет считаться хорошей практикой.
 * 
 * <p>
 * Для реализации правила валидации жизненного цикла как managed кода (транзакционного, ограниченного контекстом и с
 * работающей инжекцией) необходимо использовать
 * {@linkplain ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator LifecycleCdiValidator}
 * 
 * <p>
 * Реализация валидатора должна выполнить проверку некоторых утверждений, описанных бизнес-правилами и требованиями. В
 * зависимости от этой проверки прикладной код должен сформировать результат валидации, заполнив его соответсвующими
 * событиями, которые могут быть одним из трех видов:
 * <ul>
 * <li><b>INFO</b> - простое сообщение пользователю о результате выполнения валидации. Ни на что не влияет и носит
 * сугубо информационный характер.
 * <li><b>WARNING</b> - предупреждение пользователя о наличии потенциальных проблем. При появлении предупреждений
 * дальнейшее выполнение жизненного цикла может быть заблокировано, в случае если пользователь не примет явного решения
 * игнорировать эти предупреждения. Для ознакомления с предупреждениями и принятия решения об их игнорировании
 * предусмотрен специализированный UI. Если же жизненный цикл выполняется программно, т.е. без взаимодействия с
 * пользователем, то погасить предупреждения возможно через
 * {@link ru.argustelecom.box.env.lifecycle.api.executor.LifecyclePhaseListener LifecyclePhaseListener}
 * <li><b>ERROR</b> - сигнализирует о том, что бизнес-объект находится в неконсистентном состоянии и дальнейшее
 * выполнение жизненного цикла невозможно до тех пор, пока объект не станет консистентным. В качестве примера подобного
 * рода события фазы валидации можно представить попытку активации договора без содержания договора, или активацию
 * прайс-листа без продуктовых предложений. В этом случае необходимо сначала привести бизнес-объект в консистентное
 * состояние, например, создать одно или несколько продуктовых предложений и повторно инициировать активацию
 * прайс-листа.
 * </ul>
 * 
 * <p>
 * Пример реализации LifecycleValidator:
 * 
 * <pre>
 * <code>
 * public class SampleValidator implements LifecycleValidator&lt;SampleState, Sample&gt; {
 *    
 *    {@literal @}Override
 *    public void validate(ExecutionCtx&lt;SampleState, ? extends Sample&gt; ctx, ValidationResult&lt;?&gt; result) {
 *       Sample businessObject = ctx.getBusinessObject();
 *       if (businessObject instanceof NamedObject) {
 *           result.infof(businessObject, "Объекту присвоено имя '%s'", businessObject);
 *            
 *           //также доступно result.info(...)  -- без форматирования с готовой строкой
 *           //также доступно result.infov(...) -- с форматированием через MessageFormat.format
 *       }
 *       if (businessObject.getId() % 2L != 0) {
 *           result.warnv(businessObject, "Идентификатор не кратен двум, возможны проблемы. ID = {0}", businessObject.getId());
 *           
 *           //аналогично result.warn(...)
 *           //аналогично result.warnf(...) -- с форматированием через String.format
 *       }
 *       if (!businessObject.hasSomeExpectedState()) {
 *           result.error(businessObject, "Состояние объекта неудовлетворительно!");
 *           
 *           //аналогично result.errorf(...);
 *           //аналогично result.errorv(...);
 *       }
 *    } 
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
public interface LifecycleValidator<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	/**
	 * Выполняет конкретное бизнес-правило валидации жизненного цикла, описанное в реализации этого интерфейса.
	 * Вызывается только фреймворком жизненного цикла на определенной фазе выполнения, не может быть вызвано прикладным
	 * кодом.
	 * 
	 * <p>
	 * В качестве аргумента в этот метод передается контекст выполнения, из которого реализация операции сможет извлечь
	 * полезные сведения о текущем бизнес-объекте, маршруте перехода в новое состояние, переменных жизненного цикла и
	 * т.д.
	 * 
	 * @param context
	 *            - {@linkplain ExecutionCtx контекст выполнения} текущего перехода
	 * @param validationResult
	 *            - результат валидации
	 */
	void validate(ExecutionCtx<S, ? extends O> ctx, ValidationResult<Object> result);

}
