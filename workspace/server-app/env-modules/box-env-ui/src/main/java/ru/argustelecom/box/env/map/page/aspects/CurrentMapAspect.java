package ru.argustelecom.box.env.map.page.aspects;

import javax.inject.Inject;

import ru.argustelecom.system.inf.page.ObservablePresentationState;
import ru.argustelecom.system.inf.page.PresentationState;
import ru.argustelecom.system.inf.utils.CDIHelper;

/**
 * Текущий аспект страницы. 
 * 
 * @author s.golovanov
 * @see MapAspect аспект.
 */
@PresentationState
public class CurrentMapAspect extends ObservablePresentationState<String> {

	private static final long serialVersionUID = 1L;

	private String aspectBeanName;

	@Override
	public String getValue() {
		return aspectBeanName;
	}
	
	@SuppressWarnings("unchecked")
	public <T extends MapAspect> T getValueAsObject() {
		return aspectBeanName == null ? null : (T) CDIHelper.lookupCDIBean(MapAspect.class, aspectBeanName);
	}


	@Inject
	private CurrentMapAspectSettings currentMapAspectSettings;

	@Override
	protected void doSetValue(String value) {
		this.aspectBeanName = value;
		// при смене аспекта настройки предыдущего не должны оставаться текущими (TASK-78310)
		currentMapAspectSettings.setValue(null);
		//Для проверки что указанный beanName существует.
		getValueAsObject();
	}

}
