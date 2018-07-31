package ru.argustelecom.ops.inf.queue.impl.context;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import ru.argustelecom.ops.inf.queue.api.context.EntityReference;

public class EntityReferenceSerializer<T extends EntityReference<?>> extends JsonSerializer<T> {
	@Override
	public void serialize(T value, JsonGenerator jgen, SerializerProvider provider)
			throws IOException, JsonGenerationException {
		if (value != null) {
			jgen.writeString(value.identity());
		} else {
			jgen.writeNull();
		}
	}
}