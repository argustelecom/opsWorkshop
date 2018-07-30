package ru.argustelecom.ops.inf.hibernate.types;

import static com.google.common.collect.Lists.newArrayList;

import java.io.Serializable;
import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;

import ru.argustelecom.ops.inf.hibernate.utils.PostgresType;

public abstract class ArrayType<T> implements UserType {

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.ARRAY };
	}

	@Override
	public Class<List> returnedClass() {
		return List.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equals(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> nullSafeGet(ResultSet resultSet, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {
		if (resultSet.wasNull()) {
			return null;
		}

		Array array = resultSet.getArray(names[0]);
		if (array == null) {
			return newArrayList();
		}

		return newArrayList((T[]) array.getArray());
	}

	@Override
	public void nullSafeSet(PreparedStatement statement, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {
		if (value == null) {
			statement.setNull(index, Types.ARRAY);
		} else {
			Connection connection = statement.getConnection();
			Array array = connection.createArrayOf(getType().getValue(), ((List) value).toArray());
			statement.setArray(index, array);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ArrayList<T> deepCopy(Object value) throws HibernateException {
		if (value == null)
			return null;

		return newArrayList((List<T>) value);
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		return deepCopy(value);
	}

	@Override
	public List<T> assemble(Serializable cached, Object owner) throws HibernateException {
		return deepCopy(cached);
	}

	@Override
	public List<T> replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}

	public abstract PostgresType getType();
}
