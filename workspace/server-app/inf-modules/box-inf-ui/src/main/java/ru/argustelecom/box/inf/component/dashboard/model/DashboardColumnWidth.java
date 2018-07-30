package ru.argustelecom.box.inf.component.dashboard.model;

public enum DashboardColumnWidth {

	width_1u, width_2u, width_3u, width_4u, width_5u, width_6u, width_7u, width_8u, width_9u, width_10u;

	public String getStyleClass() {
		return name().replace('_', '-');
	}
}
