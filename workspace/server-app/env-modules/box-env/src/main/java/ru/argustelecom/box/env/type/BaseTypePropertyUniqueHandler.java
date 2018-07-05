package ru.argustelecom.box.env.type;

import static ru.argustelecom.box.env.type.event.qualifier.UniqueMode.DISABLE;
import static ru.argustelecom.box.env.type.event.qualifier.UniqueMode.ENABLE;

import java.util.List;

import javax.enterprise.event.Event;
import javax.inject.Inject;

import ru.argustelecom.box.env.type.event.DelegateUniqueEvent;
import ru.argustelecom.box.env.type.event.UniqueEvent;
import ru.argustelecom.box.env.type.event.qualifier.MakeUniqueDelegate;
import ru.argustelecom.box.env.type.model.TypeInstance;
import ru.argustelecom.box.env.type.model.TypeProperty;

/**
 * Базовый класс для обработчиков событий включения или выключения уникальность для свойства типа.<br/>
 * Также см. {@link ru.argustelecom.box.env.type.model.SupportUniqueProperty} и
 * {@link ru.argustelecom.box.env.type.model.TypeInstanceDescriptor}.
 */
public abstract class BaseTypePropertyUniqueHandler {

	@Inject
	@MakeUniqueDelegate(ENABLE)
	private Event<DelegateUniqueEvent> enableEvent;

	@Inject
	@MakeUniqueDelegate(DISABLE)
	private Event<DelegateUniqueEvent> disableEvent;

	protected void handleEnable(UniqueEvent event) {
		fire(enableEvent, event.getProperty());
	}

	protected void handleDisable(UniqueEvent event) {
		fire(disableEvent, event.getProperty());
	}

	protected abstract List<Class<? extends TypeInstance<?>>> getInstanceClasses();

	private void fire(Event<DelegateUniqueEvent> eventEmitter, TypeProperty<?> property) {
		getInstanceClasses()
				.forEach(instanceClass -> eventEmitter.fire(new DelegateUniqueEvent(instanceClass, property)));
	}
}
