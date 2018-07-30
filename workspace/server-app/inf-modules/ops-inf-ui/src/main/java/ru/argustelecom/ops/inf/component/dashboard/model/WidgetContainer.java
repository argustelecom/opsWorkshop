package ru.argustelecom.ops.inf.component.dashboard.model;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class WidgetContainer {

	private String widgetId;
	private String height;
	private DashboardColumnWidth width = DashboardColumnWidth.width_10u;
	private boolean enabled = true;

	public String getWidgetId() {
		return widgetId;
	}

	public void setWidgetId(String widgetId) {
		this.widgetId = widgetId;
	}

	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public DashboardColumnWidth getWidth() {
		return width;
	}

	public void setWidth(DashboardColumnWidth width) {
		this.width = checkNotNull(width);
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
}
