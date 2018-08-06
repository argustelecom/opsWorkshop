package ru.argustelecom.ops.inf.queue.impl.context;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.IOException;

import com.fasterxml.jackson.core.Version;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.google.common.base.Strings;

import ru.argustelecom.ops.inf.nls.LocaleUtils;
import ru.argustelecom.ops.inf.queue.api.context.Context;
import ru.argustelecom.ops.inf.queue.api.context.EntityReference;
import ru.argustelecom.system.inf.exception.SystemException;

public final class ContextMapper {

	private ContextMapper() {
	}

	private static final ObjectMapper objectMapper = new ObjectMapper();
	static {
		objectMapper.configure(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT, true);
		objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

		SimpleModule entityReferenceModule = new SimpleModule("EntityReference", new Version(1, 0, 0, "RC", null, null));
		entityReferenceModule.addSerializer(EntityReference.class, new EntityReferenceSerializer<>());
		entityReferenceModule.addDeserializer(EntityReference.class, new EntityReferenceDeserializer());
		objectMapper.registerModule(entityReferenceModule);
	}

	public static ObjectMapper get() {
		return objectMapper;
	}

	public static void update(Context context, String marshalledContext) {
		checkArgument(context != null);
		checkArgument(!isNullOrEmpty(marshalledContext));
		try {

			objectMapper.readerForUpdating(context).readValue(marshalledContext);

		} catch (IOException cause) {
			throw LocaleUtils.exception(SystemException.class, cause,
					"Unable to update event context of class {0} with marshalled value {1}",
					context.getClass().getSimpleName(), marshalledContext);
		}
	}

	public static String marshall(Context context) {
		checkArgument(context != null);
		try {

			return objectMapper.writeValueAsString(context);

		} catch (IOException cause) {
			throw LocaleUtils.exception(SystemException.class, cause, "Unable to marshall event context of class {0}",
					context.getClass().getSimpleName());
		}
	}

	public static boolean isValid(String marshalledContext) {
		return !Strings.isNullOrEmpty(marshalledContext) && !EMPTY_CONTEXT.equals(marshalledContext);
	}

	public static final String EMPTY_CONTEXT = "{}";
}
