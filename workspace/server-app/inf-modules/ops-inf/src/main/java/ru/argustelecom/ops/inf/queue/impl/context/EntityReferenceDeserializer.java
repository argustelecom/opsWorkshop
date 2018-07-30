package ru.argustelecom.ops.inf.queue.impl.context;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

import ru.argustelecom.ops.inf.queue.api.context.EntityReference;

public class EntityReferenceDeserializer extends JsonDeserializer<EntityReference<?>> {

	@Override
	public EntityReference<?> deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {

		JsonToken t = jp.getCurrentToken();
		if (t == JsonToken.VALUE_STRING) {
			return new EntityReference<>(jp.getText());
		}
		if (t == JsonToken.VALUE_NULL) {
			return getNullValue(ctxt);
		}

		throw ctxt.mappingException(EntityReference.class, t);
	}

}