package ru.argustelecom.ops.workshop.model;

public enum VersionStatus {
	ACTIVE("Активна"),
	FIXED("Зафиксирована"),
	RELEASED("Выпущена"),
	ARCHIEVED("Архив");

	private String desc;

	VersionStatus(String desc) {
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
