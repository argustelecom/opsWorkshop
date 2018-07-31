package ru.argustelecom.ops.inf.component.dashboard;

import java.util.List;
import java.util.stream.Collectors;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import javax.faces.component.UIComponentBase;

import ru.argustelecom.ops.inf.component.dashboard.model.DashboardModel;

@FacesComponent(value = Dashboard.COMPONENT_TYPE)
//@formatter:off
@ResourceDependencies({ 
		@ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
		@ResourceDependency(library = "primefaces", name = "primefaces.js"),
		
		@ResourceDependency(name = "inf/styles/ops-components.css", target = "head")
})//@formatter:on
public class Dashboard extends UIComponentBase {

	public static final String COMPONENT_TYPE = "ru.argustelecom.ops.component.Dashboard";
	public static final String COMPONENT_FAMILY = "ru.argustelecom.ops.component";

	protected enum PropertyKeys {
		model, style, styleClass, responsive;
	}

	public Dashboard() {
		setRendererType(DashboardRenderer.RENDERER_TYPE);
	}

	@Override
	public String getFamily() {
		return COMPONENT_FAMILY;
	}

	public DashboardModel getModel() {
		return (DashboardModel) getStateHelper().eval(PropertyKeys.model, null);
	}

	public void setModel(DashboardModel model) {
		getStateHelper().put(PropertyKeys.model, model);
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

	public Boolean isResponsive() {
		return (Boolean) getStateHelper().eval(PropertyKeys.responsive, true);
	}

	public void setResponsive(Boolean responsive) {
		getStateHelper().put(PropertyKeys.responsive, responsive);
	}

	private List<DashboardItem> items;

	public List<DashboardItem> getItems() {
		if (items == null) {
			items = getChildren().stream().filter(c -> c instanceof DashboardItem).map(c -> (DashboardItem) c)
					.collect(Collectors.toList());
		}
		return items;
	}

	public void setItems(List<DashboardItem> items) {
		this.items = items;
	}

}
