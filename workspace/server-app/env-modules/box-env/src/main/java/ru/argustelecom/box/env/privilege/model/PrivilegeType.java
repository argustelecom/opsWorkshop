package ru.argustelecom.box.env.privilege.model;

import lombok.Getter;

@Getter
public enum PrivilegeType {

	TRUST_PERIOD("Доверительный период"),
	TRIAL_PERIOD("Пробный период");

	private String name;

	PrivilegeType(String name) {
		this.name = name;
	}
}
