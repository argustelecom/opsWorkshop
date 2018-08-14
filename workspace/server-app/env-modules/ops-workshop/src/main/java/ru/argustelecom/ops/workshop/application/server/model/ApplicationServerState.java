package ru.argustelecom.ops.workshop.application.server.model;

public enum ApplicationServerState {
	RUNNING("Включен"),
	TURNED_OFF("Выключен"),
	LOCKED_FOR_DEMO("Заблокирован для показа");

	private String desc;

	public String getDescription() {
		return desc;
	}

	ApplicationServerState(String desc) {
		this.desc = desc;
	}

	@Override
	public String toString() {
		return desc;
	}
}
