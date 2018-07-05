package ru.argustelecom.box.env.lifecycle.impl.definition.proxy;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleBean;
import ru.argustelecom.box.env.lifecycle.api.cdi.LifecycleCdiAction;
import ru.argustelecom.box.env.lifecycle.api.context.ExecutionCtx;
import ru.argustelecom.box.env.lifecycle.api.definition.LifecycleAction;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.utils.CDIHelper;

/**
 * Прокси для {@linkplain LifecycleAction операции} жизненного цикла. Скрывает непосредственную реализацию операции,
 * делая единообразным вызов простых операций (чаще всего описываются при помощи лямбд) и Enterprise сервисов,
 * реализованных в виде транзакционных Stateless CDI бинов. Для получения и инициализации сервисов использует
 * {@linkplain CDIHelper}
 * <p>
 * При конструировании прокси операции проверяет, что транзакционный Stateless CDI бин аннотирован стереотипом
 * {@linkplain LifecycleBean}. Если это не так, то будет спровоцировано системное исключение.
 * 
 * @param <S>
 *            - тип состояния бизнес-объекта жизненного цикла
 * @param <O>
 *            - тип бизнес-объекта жизненного цикла
 */
public abstract class LifecycleActionProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
		implements LifecycleAction<S, O> {

	private static final Logger log = Logger.getLogger(LifecycleActionProxy.class);

	protected abstract LifecycleAction<S, ? super O> getUnproxiedAction();

	@Override
	public final void execute(ExecutionCtx<S, ? extends O> context) {
		LifecycleAction<S, ? super O> unproxiedAction = getUnproxiedAction();
		log.debugv("Executing action {0}", unproxiedAction);
		unproxiedAction.execute(context);
	}

	//@formatter:off
	
	public static <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleActionProxy<S, O> createSimple(LifecycleAction<S, ? super O> action) {
		return new SimpleActionProxy<>(action);
	}
	
	public static <S extends LifecycleState<S>, O extends LifecycleObject<S>> 
	LifecycleActionProxy<S, O> createCdi(Class<? extends LifecycleCdiAction<S, ? super O>> actionClass) {
		return new CdiActionProxy<>(actionClass);
	}
	
	//@formatter:on

	private static class SimpleActionProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
			extends LifecycleActionProxy<S, O> {

		private LifecycleAction<S, ? super O> instance;

		SimpleActionProxy(LifecycleAction<S, ? super O> instance) {
			checkRequiredArgument(instance, "LifecycleAction");
			this.instance = instance;
		}

		@Override
		protected LifecycleAction<S, ? super O> getUnproxiedAction() {
			return instance;
		}
	}

	private static class CdiActionProxy<S extends LifecycleState<S>, O extends LifecycleObject<S>>
			extends LifecycleActionProxy<S, O> {

		private Class<? extends LifecycleCdiAction<S, ? super O>> instanceClass;

		CdiActionProxy(Class<? extends LifecycleCdiAction<S, ? super O>> instanceClass) {
			checkRequiredArgument(instanceClass, "LifecycleCdiAction class");
			checkArgument(instanceClass.isAnnotationPresent(LifecycleBean.class),
					"Unable to determine context. Service '%s' must be explicit annotated with stereotype @LifecycleBean",
					instanceClass);
			this.instanceClass = instanceClass;
		}

		@Override
		protected LifecycleAction<S, ? super O> getUnproxiedAction() {
			LifecycleAction<S, ? super O> unproxiedAction = CDIHelper.lookupCDIBean(instanceClass);
			if (unproxiedAction == null) {
				throw new SystemException(String.format("Usatisfied dependency '%s'", instanceClass));
			}
			return unproxiedAction;
		}
	}
}
