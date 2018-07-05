package ru.argustelecom.box.env.components;

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;
import java.util.Map;

import javax.faces.component.NamingContainer;
import javax.faces.component.UIInput;
import javax.faces.component.UINamingContainer;

/**
 * FIXME Необходимо пристрелить этот костыль после решения основного таска
 * <p>
 * TASK-89357 Итерации по компонентам с полным хранением состояния
 */
public abstract class AbstractCompositeInput extends UIInput implements NamingContainer {

	public AbstractCompositeInput() {
		setRendererType(null);
	}

	@Override
	public String getFamily() {
		return UINamingContainer.COMPONENT_FAMILY;
	}

	protected <T> void setPrivateState(Serializable key, T state) {
		if (state != null) {
			getStateHelper().put(key, getClientId(), state);
		} else {
			Map<?, ?> stateHolder = (Map<?, ?>) getStateHelper().eval(key);
			if (stateHolder != null) {
				stateHolder.remove(getClientId());
				if (stateHolder.isEmpty()) {
					getStateHelper().remove(key);
				}
			}
		}
	}

	protected <T> T getPrivateState(Serializable key, Class<T> stateClass) {
		return getPrivateState(key, stateClass, null);
	}

	protected <T> T getPrivateState(Serializable key, Class<T> stateClass, T defaultValue) {
		Object state = getStateHelper().eval(key);
		if (state != null) {
			if (stateClass.isInstance(state)) {
				return stateClass.cast(state);
			}
			checkState(state instanceof Map);
			Map<?, ?> stateHolder = (Map<?, ?>) state;
			if (!stateHolder.isEmpty()) {
				Object privateState = stateHolder.get(getClientId());
				if (privateState != null) {
					checkState(stateClass.isInstance(privateState));
					return stateClass.cast(privateState);
				}
			}
		}
		return defaultValue;
	}
}
