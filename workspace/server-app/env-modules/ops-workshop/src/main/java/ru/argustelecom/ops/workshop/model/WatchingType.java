package ru.argustelecom.ops.workshop.model;

public enum WatchingType {
	ALWAYS("Всегда"),
	MYTEAM("Когда участвует команда"),
	NEWER("Никогда");

	private String desc;

	WatchingType(String desc) {
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
