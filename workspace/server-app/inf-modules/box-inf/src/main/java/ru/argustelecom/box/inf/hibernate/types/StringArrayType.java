package ru.argustelecom.box.inf.hibernate.types;

import static ru.argustelecom.box.inf.hibernate.utils.PostgresType.VARCHAR;

import ru.argustelecom.box.inf.hibernate.utils.PostgresType;

public class StringArrayType extends ArrayType<String> {
	@Override
	public PostgresType getType() {
		return VARCHAR;
	}
}
