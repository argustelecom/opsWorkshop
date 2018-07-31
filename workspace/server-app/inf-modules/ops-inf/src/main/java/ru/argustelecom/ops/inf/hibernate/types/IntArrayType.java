package ru.argustelecom.ops.inf.hibernate.types;

import ru.argustelecom.ops.inf.hibernate.utils.PostgresType;

import static ru.argustelecom.ops.inf.hibernate.utils.PostgresType.INTEGER;

public class IntArrayType extends ArrayType<Integer> {
	@Override
	public PostgresType getType() {
		return INTEGER;
	}
}
