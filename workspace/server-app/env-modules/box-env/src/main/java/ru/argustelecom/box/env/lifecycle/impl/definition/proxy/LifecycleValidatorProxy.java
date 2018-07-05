package ru.argustelecom.box.env.lifecycle.impl.definition.proxy;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiValidator;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleValidator;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.utils.CDIHelper;
import ru.argustelecom.system.inf.validation.ValidationResult;

/**
 * Прокси для {@linkplain LifecycleValidator правила валидации} жизненного цикла. Скрывает непосредственную реализацию
 * правила валидации, делая единообразным вызов простых правил (чаще всего описываются при помощи лямбд) и Enterprise
 * сервисов, реализованных в виде транзакционных Stateless CDI бинов. Для получения и инициализации сервисов использует
 * {@linkplain CDIHelper}
 * <p>
 * При конструировании прокси правила валидации проверяет, что транзакционный Stateless CDI бин аннотирован стереотипом
 * {@linkplain LifecycleBean}. Если это не так, то будет спровоцировано системное исключение.
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public abstract class LifecycleValidatorProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleValidator<S, O> {

	private static final Logger log = Logger.getLogger(LifecycleValidatorProxy.class);

	protected abstract LifecycleValidator<S, ? super O> getUnproxiedValidator();

	@Override
	public final void validate(ExecutionCtx<S, ? extends O> ctx, ValidationResult<Object> result) {
		LifecycleValidator<S, ? super O> unproxiedValidator = getUnproxiedValidator();
		log.debugv("Validating {0}", unproxiedValidator);

		ValidationResult<Object> localResult = ValidationResult.success();
		unproxiedValidator.validate(ctx, localResult);

		if (!localResult.isEmpty()) {
			log.debug(localResult);
			result.add(localResult);
		}
	}

	//@formatter:off
	
	public static <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleValidatorProxy<S, O> createSimple(LifecycleValidator<S, ? super O> validator) {
		return new SimpleValidatorProxy<>(validator);
	}
	
	public static <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleValidatorProxy<S, O> createCdi(Class<? extends LifecycleCdiValidator<S, ? super O>> validatorClass) {
		return new CdiValidatorProxy<>(validatorClass);
	}
	
	//@formatter:on

	private static class SimpleValidatorProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
			extends LifecycleValidatorProxy<S, O> {

		private LifecycleValidator<S, ? super O> instance;

		SimpleValidatorProxy(LifecycleValidator<S, ? super O> instance) {
			checkRequiredArgument(instance, "LifecycleValidator");
			this.instance = instance;
		}

		@Override
		protected LifecycleValidator<S, ? super O> getUnproxiedValidator() {
			return instance;
		}
	}

	private static class CdiValidatorProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
			extends LifecycleValidatorProxy<S, O> {

		private Class<? extends LifecycleCdiValidator<S, ? super O>> instanceClass;

		CdiValidatorProxy(Class<? extends LifecycleCdiValidator<S, ? super O>> instanceClass) {
			checkRequiredArgument(instanceClass, "LifecycleCdiValidator class");
			checkArgument(instanceClass.isAnnotationPresent(LifecycleBean.class),
					"Unable to determine context. Service '%s' must be explicit annotated with stereotype @LifecycleBean",
					instanceClass);
			this.instanceClass = instanceClass;
		}

		@Override
		protected LifecycleValidator<S, ? super O> getUnproxiedValidator() {
			LifecycleValidator<S, ? super O> unproxiedValidator = CDIHelper.lookupCDIBean(instanceClass);
			if (unproxiedValidator == null) {
				throw new SystemException(String.format("Usatisfied dependency '%s'", instanceClass));
			}
			return unproxiedValidator;
		}
	}
}
