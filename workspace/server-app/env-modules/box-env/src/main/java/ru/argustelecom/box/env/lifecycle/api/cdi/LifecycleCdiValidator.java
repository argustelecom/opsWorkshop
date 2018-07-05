package ru.argustelecom.box.env.lifecycle.api.cdi;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleValidator;

/**
 * Маркерный интерфейс {@linkplain LifecycleValidator правила валидации} жизненного цикла. Реализация интерфейса
 * ожидается в виде транзакционного Stateless CDI бина, который требует окружения (другие сервисы, контекст персистенции
 * и т.д.) управляемого Enterprise контейнером.
 * 
 * <p>
 * Класс-реализация данного интерфейса должен в обязательном порядке обладать cтреотипом {@linkplain LifecycleBean} для
 * обозначения дефолтного контекста, имени бина и привязки к текущей транзакции. В случае отсутствия этого стереотипа,
 * во время построения/выполнения жизненного цикла будет выброшено системное исключение
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public interface LifecycleCdiValidator<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		extends LifecycleValidator<S, O> {
}
