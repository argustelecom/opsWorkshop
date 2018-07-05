package ru.argustelecom.box.inf.hibernate.types;

import ru.argustelecom.box.inf.hibernate.utils.PostgresType;

import static ru.argustelecom.box.inf.hibernate.utils.PostgresType.INTEGER;

public class IntArrayType extends ArrayType<Integer> {
	@Override
	public PostgresType getType() {
		return INTEGER;
	}
}
