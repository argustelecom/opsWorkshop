package ru.argustelecom.box.env.lifecycle.api.cdi;

import static java.lang.annotation.ElementType.TYPE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.enterprise.context.RequestScoped;
import javax.enterprise.inject.Stereotype;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

/**
 * Стереотип, который задает контекст для {@linkplain LifecycleCdiAction операции}, {@linkplain LifecycleCdiCondition
 * условия} и {@linkplain LifecycleCdiValidator правила валидации} жизненного цикла. Должен быть <strong>явно</strong>
 * указан для класса, реализующего один из указанных ранее интерфейсов жизненного цикла. Кроме контекста обеспечивает
 * транзакционность бина, а также делая его именованным.
 * 
 * <p>
 * Необходимость явного указания обусловлена ограничениями CDI, при которых параметризуемые интерфейсы не сканируются и,
 * как следствие, этот стереотип игнорируется, если он указан на уровне базового интерфейса. В этом случае реализация
 * теряет контекст реквеста, свойства транзакционности и именованности. Любой код, который работает с Enterprise
 * операциями, условиями и правилами валидации жизненного цикла, будет явно выполнять проверку наличия этого стереотипа
 * для реализации.
 */
@Stereotype
@RequestScoped
@Transactional(TxType.REQUIRED)
@Documented
@Target({ TYPE })
@Retention(RUNTIME)
public @interface LifecycleBean {
}