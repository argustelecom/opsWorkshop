package ru.argustelecom.box.env.lifecycle.impl.event;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.lang.annotation.Annotation;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.event.RoutedFrom;
import ru.argustelecom.box.env.lifecycle.api.event.RoutedTo;
import ru.argustelecom.box.env.lifecycle.api.event.RoutingCompleted;
import ru.argustelecom.box.publang.base.model.IEntity;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperService;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.utils.CDIHelper;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

@RequestScoped
public class LifecycleNotificator {

	private static final Logger log = Logger.getLogger(LifecycleNotificator.class);

	@Inject
	private EntityWrapperService wrapper;

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void fireRoutedFromEvent(O businessObject) {
		RoutedFrom qualifier = new RoutedFromLiteral(businessObject.getState().getEventQualifier());
		fireRoutingEvent(businessObject, qualifier);
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void fireRoutedToEvent(O businessObject) {
		RoutedTo qualifier = new RoutedToLiteral(businessObject.getState().getEventQualifier());
		fireRoutingEvent(businessObject, qualifier);
	}

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> void fireRoutingCompletedEvent(O businessObject,
			S oldState) {
		RoutingCompleted qualifier = new RoutingCompletedLiteral(oldState.getEventQualifier(),
				businessObject.getState().getEventQualifier());
		fireRoutingEvent(businessObject, qualifier);
	}

	<S extends LifecycleState<S>, O extends LifecycleObject<S>> void fireRoutingEvent(O businessObject,
			Annotation qualifier) {
		try {
			IEntity wrappedEntity = wrapper.wrap(initializeAndUnproxy(businessObject));
			if (wrappedEntity != null) {
				log.debugv("Firing notification event {0} with qualifier {1}", wrappedEntity, qualifier);
				CDIHelper.fireStrictEvent(wrappedEntity, qualifier);
			} else {
				log.warnv("Lifecycle notification is not supported for entity {0}", businessObject.getClass());
			}
		} catch (Exception e) {
			throw new SystemException(e);
		}
	}
}
