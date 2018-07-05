package ru.argustelecom.box.env.lifecycle;

import java.io.Serializable;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.LifecycleRegistry;
import ru.argustelecom.box.env.lifecycle.api.LifecycleState;
import ru.argustelecom.box.env.lifecycle.api.definition.Lifecycle;

@Named
@RequestScoped
public class BehaviorUtils {

	@Inject
	private LifecycleRegistry registry;

	public <S extends LifecycleState<S>, O extends LifecycleObject<S>> boolean isForbidden(O businessObject,
			Serializable behavior) {
		Lifecycle<S, O> lifecycle = registry.getLifecycle(businessObject);
		if (lifecycle != null) {
			return lifecycle.isForbidden(businessObject.getState(), behavior);
		}
		return false;
	}

}