package ru.argustelecom.ops.inf.hibernate.types;

import static ru.argustelecom.ops.inf.hibernate.utils.PostgresType.VARCHAR;

import ru.argustelecom.ops.inf.hibernate.utils.PostgresType;

public class StringArrayType extends ArrayType<String> {
	@Override
	public PostgresType getType() {
		return VARCHAR;
	}
}
