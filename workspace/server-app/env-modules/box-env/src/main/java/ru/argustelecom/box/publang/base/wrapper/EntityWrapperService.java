package ru.argustelecom.box.publang.base.wrapper;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.String.format;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;

import org.jboss.logging.Logger;

import com.google.common.base.Strings;

import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.CDIHelper;

@DomainService
public class EntityWrapperService implements Serializable {

	private static final long serialVersionUID = 3070347167523158994L;
	private static final Logger log = Logger.getLogger(EntityWrapperService.class);

	public <I extends IEntity> I wrap(Class<I> wrappedClass, Identifiable entity) {
		checkRequiredArgument(wrappedClass, "wrappedClass");
		checkRequiredArgument(entity, "entity");

		I wrappingResult = null;
		if (isWrappingSupported(entity.getClass())) {
			IEntity wrappedEntity = doWrap(entity);
			if (!wrappedEntity.getClass().isAssignableFrom(wrappedClass)) {
				throw new SystemException(format("Can't cast '%s' to '%s'", wrappedEntity.getClass(), wrappedClass));
			}
			wrappingResult = wrappedClass.cast(wrappedEntity);
		}

		return wrappingResult;
	}

	public IEntity wrap(Identifiable entity) {
		checkRequiredArgument(entity, "entity");
		return isWrappingSupported(entity.getClass()) ? doWrap(entity) : null;
	}

	public <E extends Identifiable> E unwrap(Class<E> entityClass, IEntity wrappedEntity) {
		checkRequiredArgument(entityClass, "entityClass");
		checkRequiredArgument(wrappedEntity, "wrappedEntity");

		E unwrappingResult = null;
		if (isWrappingSupported(wrappedEntity.getClass())) {
			Identifiable entity = doUnwrap(wrappedEntity);
			if (!entity.getClass().isAssignableFrom(entityClass)) {
				throw new SystemException(format("Can't cast '%s' to '%s'", entity.getClass(), entityClass));
			}
			unwrappingResult = entityClass.cast(entity);
		}

		return unwrappingResult;
	}

	public Identifiable unwrap(IEntity wrappedEntity) {
		checkRequiredArgument(wrappedEntity, "wrappedEntity");
		return isWrappingSupported(wrappedEntity.getClass()) ? doUnwrap(wrappedEntity) : null;
	}

	private IEntity doWrap(Identifiable entity) {
		EntityWrapper wrapper = lookupWrapper(entity.getClass());
		return wrapper.wrap(entity);
	}

	private Identifiable doUnwrap(IEntity wrappedEntity) {
		EntityWrapper wrapper = lookupWrapper(wrappedEntity.getClass());
		return wrapper.unwrap(wrappedEntity);
	}

	private boolean isWrappingSupported(Class<?> clazz) {
		boolean wrappingSupported = clazz.isAnnotationPresent(EntityWrapperDef.class);
		if (!wrappingSupported) {
			log.warnv("Unsupported entity in the public language: {0}", clazz);
		}
		return wrappingSupported;
	}

	private EntityWrapper lookupWrapper(Class<?> clazz) {
		EntityWrapperDef definition = clazz.getAnnotation(EntityWrapperDef.class);
		checkState(definition != null, "@EntityWrapperDef is not present for type %s", clazz);

		String wrapperName = definition.name();
		checkState(!Strings.isNullOrEmpty(wrapperName), "Wrapper name is not defined in @EntityWrapperDef");

		EntityWrapper wrapper = CDIHelper.lookupCDIBean(EntityWrapper.class, wrapperName);
		checkState(wrapper != null, "Unable to lookup implementer for EntityWrapper with @Name '%s'", wrapperName);

		return wrapper;
	}
}