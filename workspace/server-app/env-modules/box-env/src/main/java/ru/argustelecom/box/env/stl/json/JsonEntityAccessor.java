package ru.argustelecom.box.env.stl.json;

import static com.google.common.base.Strings.isNullOrEmpty;

import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class JsonEntityAccessor {

	public <E extends Identifiable> E get(ObjectNode valuesRoot, String fieldName, Class<E> entityClass,
			EntityConverter entityConverter) {

		String entityRef = JsonHelper.STRING.get(valuesRoot, fieldName);
		return !isNullOrEmpty(entityRef) ? entityConverter.convertToObject(entityClass, entityRef) : null;
	}

	public <E extends Identifiable> void set(ObjectNode valuesRoot, String fieldName, E fieldValue,
			EntityConverter entityConverter) {

		if (fieldValue == null) {
			valuesRoot.putNull(fieldName);
		} else {
			String entityRef = entityConverter.convertToString(fieldValue);
			JsonHelper.STRING.set(valuesRoot, fieldName, entityRef);
		}
	}
}
