package ru.argustelecom.box.env.lifecycle.api.executor;

import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleAction;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleEndpoint;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleValidator;
import ru.argustelecom.system.inf.validation.ValidationResult;

/**
 * Описывает фазы {@linkplain LifecycleExecutor исполнителя жизненного цикла}. Все фазы строго определены в исполнителе
 * и за каждым идентификатором находится реальный объект, который выполняет соответствующие действия. В процессе
 * выполнения перехода все фазы обрабатываются по порядку, в котором они определены в этом перечислении.
 */
public enum LifecyclePhaseId {

	/**
	 * Фаза инициализации. На этой фазе выполняется подготовительная работа по переводу объекта по его жизненному циклу:
	 * оповещение внешнего мира о том, что объект собрался изменять свое текущее состояние (квалификатор RoutedFrom)
	 */
	INITIALIZATION,

	/**
	 * Фаза построения конкртеного маршрута. На этой фазе выполняется определение конечной точки, в которую перейдет
	 * бизнес-объект. Результатом выполнения этой фазы является удовлетворяющая условиям перехода
	 * {@linkplain LifecycleEndpoint}, а также инстанцированный и сконфигурированный {@linkplain ExecutionCtx контекст
	 * выполнения}
	 */
	ROUTE_DEFINITION,

	/**
	 * Фаза валидации и проверки утверждений. На этой фазе последовательно вызываются все определенные для конечной
	 * точки {@linkplain LifecycleValidator валидаторы} и заполняется {@linkplain ValidationResult}
	 */
	ROUTE_VALIDATION,

	/**
	 * Фаза выполнения перехода. На этой фазе, при условии успешного прохождения валидации, последовательно вызываются
	 * определенные для конечной точки {@linkplain LifecycleAction бизнес-операции}, непосредственное изменение
	 * состояния бизнес-объекта, а также оповещение внешнего мира о том, что бизнес-объект перешел в новое состояние
	 * (квалификатор RoutedTo)
	 */
	ROUTE_EXECUTION,

	/**
	 * Фаза завершения перехода. На этой фазе сохраняется история о совершившемся переходе, а также оповещается внешний
	 * мир о том, что переход завершен (квалификатор RoutedCompleted)
	 */
	FINALIZATION;

	/**
	 * Возвращает true, если текущая фаза следует после указанной фазой и не равна ей
	 */
	public boolean greater(LifecyclePhaseId that) {
		return this.compareTo(that) > 0;
	}

	/**
	 * Возвращает true, если текущая фаза следует после указанной фазой или равна ей
	 */
	public boolean greaterOrEquals(LifecyclePhaseId that) {
		return this.compareTo(that) >= 0;
	}

	/**
	 * Возвращает true, если текущая фаза предшествует указанной фазе и не равна ей
	 */
	public boolean less(LifecyclePhaseId that) {
		return this.compareTo(that) < 0;
	}

	/**
	 * Возвращает true, если текущая фаза предшествует указанной фазе или равна ей
	 */
	public boolean lessOrEquals(LifecyclePhaseId that) {
		return this.compareTo(that) <= 0;
	}
}
