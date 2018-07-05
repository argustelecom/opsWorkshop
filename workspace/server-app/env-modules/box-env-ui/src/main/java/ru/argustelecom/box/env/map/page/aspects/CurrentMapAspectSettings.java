package ru.argustelecom.box.env.map.page.aspects;

import ru.argustelecom.system.inf.page.ObservablePresentationState;
import ru.argustelecom.system.inf.page.PresentationState;

/**
 * Произвольные настройки аспекта. Конкретные аспекты знают их состав и суть, здесь они просто непрозрачный блоб.
 * <p>
 * Класс дает две вещи: букмаркабельность и событие изменения настроек аспекта. Настройки должны применяться аспектами
 * через этот класс.
 * 
 * @author s.golovanov
 */
@PresentationState
public class CurrentMapAspectSettings extends ObservablePresentationState<Object> {

	private static final long serialVersionUID = 1L;

	private Object value;

	@Override
	public Object getValue() {
		return value;
	}

	@Override
	protected void doSetValue(Object value) {
		this.value = value;
	}

}
