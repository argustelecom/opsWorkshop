package ru.argustelecom.box.env.filter;

import java.io.IOException;

import org.hibernate.proxy.HibernateProxy;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import ru.argustelecom.box.env.filter.model.FilterParam;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

public final class FilterParamMapper {

	private FilterParamMapper() {
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();
	private static final EntityConverter entityConverter = new EntityConverter();
	static {
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
	}

	public static String serialize(FilterParam filterParam) {
		try {
			return objectMapper.writeValueAsString(filterParam);
		} catch (JsonProcessingException e) {
			throw new SystemException("Unable to serialize filter", e);
		}
	}

	public static FilterParam deserialize(String filterParamAsString) {
		try {
			return objectMapper.readValue(filterParamAsString, FilterParam.class);
		} catch (IOException e) {
			throw new SystemException("Unable to deserialize filter", e);
		}
	}

	public static String serializeFilterParamValue(Object filterParamValue) {
		Object value = filterParamValue;
		if (filterParamValue instanceof HibernateProxy) {
			value = EntityManagerUtils.initializeAndUnproxy(filterParamValue);
		}

		if (value instanceof Identifiable) {
			return entityConverter.convertToString((Identifiable) value);
		} else {
			try {
				return objectMapper.writeValueAsString(value);
			} catch (JsonProcessingException e) {
				throw new SystemException("Unable to serialize filter value", e);
			}
		}
	}

	public static Object deserializeFilterParamValue(Class<?> valueClass, String valueAsString) {
		try {
			if (Identifiable.class.isAssignableFrom(valueClass)) {
				return entityConverter.convertToObject(valueAsString);
			} else {
				return objectMapper.readValue(valueAsString, valueClass);
			}
		} catch (Exception e) {
			throw new SystemException("Unable to deserialize filter value", e);
		}
	}

	public static boolean canSerializeFilterParamValue(String valueClassName) {
		try {
			return objectMapper.canSerialize(Class.forName(valueClassName));
		} catch (ClassNotFoundException e) {
			throw new SystemException("Unable to get filter value class", e);
		}
	}

}
