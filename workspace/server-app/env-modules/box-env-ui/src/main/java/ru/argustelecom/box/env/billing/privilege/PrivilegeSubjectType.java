package ru.argustelecom.box.env.billing.privilege;

import lombok.Getter;

@Getter
public enum PrivilegeSubjectType {

	SUBSCRIPTION("Подписка"), PERSONAL_ACCOUNT("Лицевой счет"), CUSTOMER("Клиент");

	private String name;

	PrivilegeSubjectType(String name) {
		this.name = name;
	}
}