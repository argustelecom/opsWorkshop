package ru.argustelecom.ops.inf.hibernate.types;

import ru.argustelecom.ops.inf.hibernate.utils.PostgresType;

public class BigintArrayType extends ArrayType<Long> {
	@Override
	public PostgresType getType() {
		return PostgresType.BIGINT;
	}
}
