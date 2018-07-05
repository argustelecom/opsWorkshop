package ru.argustelecom.box.env.billing.privilege;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum PrivilegeTypeRef {

	//@formatter:off
	TRUST_PERIOD	("Доверительный период", "Продлить период", "Продление доверительного периода"),
	TRIAL_PERIOD	("Пробный период", "Продлить пробный период", "Продление пробного периода"),
	DISCOUNT		("Скидка", "Изменить условия скидки", "Редактирование скидки");
	//@formatter:on

	private String name;
	private String editBtnTitle;
	private String editDlgHeader;

}