package ru.argustelecom.ops.env.stl.json;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@AllArgsConstructor(access = AccessLevel.PROTECTED)
public final class JsonEntityArrayAccessor {

	public <E extends Identifiable> List<E> get(ObjectNode valuesRoot, String fieldName, Class<E> entityClass,
			EntityConverter entityConverter) {

		JsonNode node = valuesRoot.get(fieldName);
		if (node == null || node.isNull()) {
			return Collections.emptyList();
		}

		checkState(node.isArray());
		ArrayNode valueNode = (ArrayNode) node;

		List<E> result = new ArrayList<>();
		Iterator<JsonNode> it = valueNode.elements();
		while (it.hasNext()) {
			JsonNode item = it.next();
			checkState(item.isTextual());
			result.add(entityConverter.convertToObject(entityClass, item.asText()));
		}
		return result;
	}

	public <E extends Identifiable> void set(ObjectNode valuesRoot, String fieldName, List<E> fieldValue,
			EntityConverter entityConverter) {

		ArrayNode valueNode = valuesRoot.putArray(fieldName);
		if (fieldValue != null) {
			fieldValue.stream().map(entityConverter::convertToString).forEach(valueNode::add);
		}
	}

}
