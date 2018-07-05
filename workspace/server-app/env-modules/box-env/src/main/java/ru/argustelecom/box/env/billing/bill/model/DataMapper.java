package ru.argustelecom.box.env.billing.bill.model;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.IOException;
import java.util.Collection;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.SystemException;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class DataMapper {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	public static ObjectMapper get() {
		return objectMapper;
	}

	static {
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
	}

	public static String marshal(Object obj) {
		checkArgument(obj != null);

		try {
			return objectMapper.writeValueAsString(obj);
		} catch (JsonProcessingException cause) {
			throw throwException(cause, "Unable to marshal object");
		}
	}

	public static <T> T unmarshal(String json, Class<T> clazz) {
		checkArgument(json != null);
		checkArgument(clazz != null);

		try {
			return objectMapper.readValue(json, clazz);
		} catch (IOException cause) {
			throw throwException(cause, "Unable to unmarshal json: \n {0}", json);
		}
	}

	public static <T> T unmarshalList(String json, Class<?> clazz) {
		checkArgument(json != null);
		checkArgument(clazz != null);

		try {
			return objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, clazz));
		} catch (IOException cause) {
			throw throwException(cause, "Unable to unmarshal json: \n {0}", json);
		}
	}

	private static SystemException throwException(IOException cause, String format, Object... objects) {
		return LocaleUtils.exception(SystemException.class, cause, format, objects);
	}

}
