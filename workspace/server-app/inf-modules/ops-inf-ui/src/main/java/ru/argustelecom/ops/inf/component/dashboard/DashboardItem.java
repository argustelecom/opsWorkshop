package ru.argustelecom.ops.inf.component.dashboard;

import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponent;
import javax.faces.component.UIComponentBase;

@FacesComponent(value = DashboardItem.COMPONENT_TYPE)
public class DashboardItem extends UIComponentBase {

	public static final String COMPONENT_TYPE = "ru.argustelecom.ops.component.DashboardItem";
	public static final String COMPONENT_FAMILY = "ru.argustelecom.ops.component";

	protected enum PropertyKeys {
		style, styleClass, contentRendered;
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public String getStyle() {
		return (String) getStateHelper().eval(PropertyKeys.style, null);
	}

	public void setStyle(String style) {
		getStateHelper().put(PropertyKeys.style, style);
	}

	public String getStyleClass() {
		return (String) getStateHelper().eval(PropertyKeys.styleClass, null);
	}

	public void setStyleClass(String styleClass) {
		getStateHelper().put(PropertyKeys.styleClass, styleClass);
	}

	public Boolean isContentRendered() {
		return (Boolean) getStateHelper().eval(PropertyKeys.contentRendered, true);
	}

	public void setContentRendered(Boolean contentRendered) {
		getStateHelper().put(PropertyKeys.contentRendered, contentRendered);
	}

	public UIComponent getContentAlt() {
		return getFacet("contentAlt");
	}

	public UIComponent getContent() {
		return getFacet("content");
	}
}
