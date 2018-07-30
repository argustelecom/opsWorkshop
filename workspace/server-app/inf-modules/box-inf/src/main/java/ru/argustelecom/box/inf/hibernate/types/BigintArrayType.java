package ru.argustelecom.box.inf.hibernate.types;

import ru.argustelecom.box.inf.hibernate.utils.PostgresType;

public class BigintArrayType extends ArrayType<Long> {
	@Override
	public PostgresType getType() {
		return PostgresType.BIGINT;
	}
}
