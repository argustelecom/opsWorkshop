package ru.argustelecom.box.env.stl.json;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class JsonAccessor<O> {

	private final JsonGetter<O> getter;
	private final JsonSetter<O> setter;

	public O get(ObjectNode valuesRoot, String fieldName) {
		return get(valuesRoot, fieldName, null);
	}

	public O get(ObjectNode valuesRoot, String fieldName, O defaultValue) {
		JsonNode valueNode = valuesRoot.get(fieldName);
		if (valueNode == null || valueNode.isNull()) {
			return defaultValue;
		}
		return getter.get(valueNode);
	}

	public void set(ObjectNode valuesRoot, String fieldName, O fieldValue) {
		if (fieldValue == null) {
			valuesRoot.putNull(fieldName);
		} else {
			setter.set(valuesRoot, fieldName, fieldValue);
		}
	}
}
