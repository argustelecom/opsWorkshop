package ru.argustelecom.box.env.lifecycle.api.definition;

import java.io.Serializable;
import java.util.Collection;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;

/**
 * Маршут - это совокупность переходов из определенных начальных состояний жизненного цикла в определенные конечные
 * состояния. Маршрут в общем случае (20% всех кейсов) может иметь несколько входов и несколько выходов. В частном
 * случае (80% всех кейсов) маршрут вырождается до простого перехода, в котором есть один вход и один безусловный выход.
 * Если маршрут имеет несколько входов (к примеру, 2) и несколько выходов (к примеру, 4), то за таким маршрутом
 * скрывается 2*4=8 фактических вариантов совершения перехода.
 * 
 * <p>
 * Все входы в маршрут (route startpoint) являются безусловными, нет никакой возможности запретить бизнес-объекту войти
 * в маршрут из какого-то определенного состояния, если это состояние поддерживается маршрутом как начальная точка.
 * 
 * <p>
 * Все выходы из маршрута ({@linkplain LifecycleEndpoint route endpoints}) являются условными -- для того, чтобы объект
 * покинул маршрут должно быть выполнено определенное условие. Если это условие не выполняется, то бизнес объект
 * переходит к следующему условному выходу и цикл проверки допустимости повторяется. При этом среди всех выходов в
 * обязательном порядке должен быть определен выход по умолчанию, в который объект пойдет, если ни одно из условий
 * других выходов так и не выполнилось. Таким образом, бизнес-объект всегда прийдет в какое-либо конечное состояние,
 * если он пойдет по какому-то маршруту.
 * 
 * <p>
 * Входы в маршут являются простым указанием факта, что маршрут может начинаться в этой точке. Выходы из маршрута
 * инкапсулируют правила валидации (проверка, что объект достиг своих бизнес-целей в своем текущем состоянии и может
 * быть переведен в другое конечное состояние) и бизнес-операции (обеспечивают консистентное представление объекта для
 * внешнего мира в его новом состоянии)
 * 
 * <p>
 * Существуют маршруты, которые не могут быть инициированы пользователем из специализированного UI. Такие маршруты
 * предусмотрены только для бизнес-логики, например, для планировщика или бизнес-процесса.
 * 
 * <p>
 * Каждый маршрут обязан обладать уникальным идентификатором в пределах всего жизненног цикла, а также человеко-понятным
 * названием, которое отображается в специализированном UI. Ключевое слово (идентификатор) маршрута и его
 * человеко-понятное наименование должно быть представлено глаголом и отвечать на вопрос "Что сделать", потому что
 * маршрут, фактически, является декларацией намерения совершить некоторое действие над бизнес-объектом, чтобы он смог
 * достигнуть нового состояния. Например, маршрут "Активировать" означает действие над бизнес-объектом, которое приведет
 * его в состояние "Активен".
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public interface LifecycleRoute<S extends LifecycleState<S>, O extends LifecycleObject<S>> {

	/**
	 * Возвращает уникальный идентификатор маршрута, не может быть null
	 */
	Serializable getKeyword();

	/**
	 * Возвращает человеко-понятное наименование маршрута
	 */
	String getName();

	/**
	 * Возвращает true, если маршрут может контролироваться пользователем из специализированного UI управления жизненным
	 * циклом
	 */
	boolean isControlledByUser();

	/**
	 * Возвращает перечень возможных начальных точек текущего маршрута
	 */
	Collection<S> getStartpoints();

	/**
	 * Возвращает перечень возможных конечных точек текущего маршрута
	 */
	Collection<LifecycleEndpoint<S>> getEndpoints();

}
