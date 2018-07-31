package ru.argustelecom.ops.inf.hibernate.utils;

import lombok.Getter;

public enum PostgresType {
	INTEGER("integer"), BIGINT("bigint"), VARCHAR("varchar");

	@Getter
	private String value;

	PostgresType(String value) {
		this.value = value;
	}
}
