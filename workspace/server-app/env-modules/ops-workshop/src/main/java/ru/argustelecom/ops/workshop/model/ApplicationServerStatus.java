package ru.argustelecom.ops.workshop.model;

public enum ApplicationServerStatus {
	RUNNING("Включен"),
	SHUTDOWN("Выключен"),
	USED("Заблокирован для показа");

	private String desc;

	ApplicationServerStatus(String desc) {
		this.desc = desc;
	}

	public String getDesc() {
		return desc;
	}

	@Override
	public String toString() {
		return desc;
	}
}