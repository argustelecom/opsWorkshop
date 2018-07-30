package ru.argustelecom.ops.inf.hibernate.types;

import static com.google.common.base.Preconditions.checkState;

import java.io.IOException;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SessionImplementor;
import org.hibernate.usertype.UserType;
import org.postgresql.util.PGobject;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.base.Objects;

public class JsonbType implements UserType {

	protected static final ObjectMapper objectMapper = new ObjectMapper();

	static {
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	@Override
	public int[] sqlTypes() {
		return new int[] { Types.JAVA_OBJECT };
	}

	@Override
	public Class<JsonNode> returnedClass() {
		return JsonNode.class;
	}

	@Override
	public boolean equals(Object x, Object y) throws HibernateException {
		return Objects.equal(x, y);
	}

	@Override
	public int hashCode(Object x) throws HibernateException {
		return x.hashCode();
	}

	@Override
	public Object nullSafeGet(ResultSet rs, String[] names, SessionImplementor session, Object owner)
			throws HibernateException, SQLException {

		JsonNode jsonNode = null;
		PGobject pgObject = null;
		if (names != null && names.length != 0 && (pgObject = (PGobject) rs.getObject(names[0])) != null) {
			try {
				jsonNode = objectMapper.readTree(pgObject.getValue());
			} catch (IOException e) {
				throw new HibernateException(e);
			}
		}
		return jsonNode;
	}

	@Override
	public void nullSafeSet(PreparedStatement st, Object value, int index, SessionImplementor session)
			throws HibernateException, SQLException {

		if (value == null) {
			st.setNull(index, Types.NULL);
			return;
		}

		String jsonString = null;
		try {
			jsonString = objectMapper.writeValueAsString(value);
		} catch (IOException e) {
			throw new HibernateException(e);
		}

		PGobject pgObject = new PGobject();
		pgObject.setType("jsonb");
		pgObject.setValue(jsonString);
		st.setObject(index, pgObject);
	}

	@Override
	public Object deepCopy(Object value) throws HibernateException {
		if (value == null)
			return null;

		JsonNode jsonNode = null;
		try {
			jsonNode = objectMapper.readTree(objectMapper.writeValueAsBytes(value));
		} catch (IOException e) {
			throw new HibernateException(e);
		}
		return jsonNode;
	}

	@Override
	public boolean isMutable() {
		return true;
	}

	@Override
	public Serializable disassemble(Object value) throws HibernateException {
		try {
			return objectMapper.writeValueAsString(value);
		} catch (IOException e) {
			throw new HibernateException(e);
		}
	}

	@Override
	public Object assemble(Serializable cached, Object owner) throws HibernateException {
		checkState(cached instanceof String);
		try {
			return objectMapper.readTree((String) cached);
		} catch (IOException e) {
			throw new HibernateException(e);
		}
	}

	@Override
	public Object replace(Object original, Object target, Object owner) throws HibernateException {
		return deepCopy(original);
	}

}
