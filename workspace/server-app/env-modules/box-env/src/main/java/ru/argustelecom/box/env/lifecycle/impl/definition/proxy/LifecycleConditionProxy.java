package ru.argustelecom.box.env.lifecycle.impl.definition.proxy;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiCondition;
import ru.argustelecom.box.env.lifecycle.api.context.TestingCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleCondition;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.utils.CDIHelper;

/**
 * Прокси для {@linkplain LifecycleCondition условия} жизненного цикла. Скрывает непосредственную реализацию условия,
 * делая единообразной проверку простых условий (чаще всего описываются при помощи лямбд) и условий, представленных в
 * виде Enterprise сервисов, реализованных как транзакционные Stateless CDI бины. Для получения и инициализации сервисов
 * использует {@linkplain CDIHelper}
 * <p>
 * При конструировании прокси условия проверяется, что транзакционный Stateless CDI бин аннотирован стереотипом
 * {@linkplain LifecycleBean}. Если это не так, то будет спровоцировано системное исключение.
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public abstract class LifecycleConditionProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleCondition<S, O> {

	private static final Logger log = Logger.getLogger(LifecycleConditionProxy.class);

	protected abstract LifecycleCondition<S, ? super O> getUnproxiedCondition();

	@Override
	public final boolean test(TestingCtx<S, ? extends O> context) {
		LifecycleCondition<S, ? super O> unproxiedCondition = getUnproxiedCondition();
		boolean testingResult = unproxiedCondition.test(context);

		log.debugv("Testing condition {0}: {1}", unproxiedCondition, testingResult);
		return testingResult;
	}

	//@formatter:off
	
	public static <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleConditionProxy<S, O> createSimple(LifecycleCondition<S, ? super O> condition) {
		return new SimpleConditionProxy<>(condition);
	}
	
	public static <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleConditionProxy<S, O> createCdi(Class<? extends LifecycleCdiCondition<S, ? super O>> conditionClass) {
		return new CdiConditionProxy<>(conditionClass);
	}
	
	//@formatter:on

	private static class SimpleConditionProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
			extends LifecycleConditionProxy<S, O> {

		private LifecycleCondition<S, ? super O> instance;

		SimpleConditionProxy(LifecycleCondition<S, ? super O> instance) {
			checkRequiredArgument(instance, "LifecycleCondition");
			this.instance = instance;
		}

		@Override
		protected LifecycleCondition<S, ? super O> getUnproxiedCondition() {
			return instance;
		}
	}

	private static class CdiConditionProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
			extends LifecycleConditionProxy<S, O> {

		private Class<? extends LifecycleCdiCondition<S, ? super O>> instanceClass;

		CdiConditionProxy(Class<? extends LifecycleCdiCondition<S, ? super O>> instanceClass) {
			checkRequiredArgument(instanceClass, "LifecycleCdiCondition class");
			checkArgument(instanceClass.isAnnotationPresent(LifecycleBean.class),
					"Unable to determine context. Service '%s' must be explicit annotated with stereotype @LifecycleBean",
					instanceClass);
			this.instanceClass = instanceClass;
		}

		@Override
		protected LifecycleCondition<S, ? super O> getUnproxiedCondition() {
			LifecycleCondition<S, ? super O> unproxiedCondition = CDIHelper.lookupCDIBean(instanceClass);
			if (unproxiedCondition == null) {
				throw new SystemException(String.format("Usatisfied dependency '%s'", instanceClass));
			}
			return unproxiedCondition;
		}
	}
}
