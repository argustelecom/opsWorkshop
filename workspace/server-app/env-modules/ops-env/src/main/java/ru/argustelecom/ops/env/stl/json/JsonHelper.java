package ru.argustelecom.ops.env.stl.json;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public final class JsonHelper {

	private JsonHelper() {
		// Instantiation is not available
	}

	// @formatter:off
	
	/**
	 * Извлекает и сохраняет логическое значение в JsonObject
	 */
	public static final JsonAccessor<Boolean> BOOLEAN = new JsonAccessor<>(
		node -> {
			checkState(node.isBoolean());
			return node.asBoolean();
		},
		ObjectNode::put
	);

	/**
	 * Извлекает и сохраняет целочисленные значения в JsonObject. 
	 */
	public static final JsonAccessor<Long> LONG = new JsonAccessor<>(
		node -> {
			checkState(node.isInt() || node.isLong());
			return node.asLong();
		}, 
		(node, name, value) -> {
			// Из-за бага в jackson приходится явно сторить int, если значение попадает в интервал возможных для int, 
			// т.к. jackson не сохраняет тип при сериализации/десериализации и ориентрируется только на значение. 
			// Иначе, получаем, что десериализованное значение не equals сериализованному
			if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
				node.put(name, value.intValue());
			} else {
				node.put(name, value.longValue());
			}
		}
	); 
	
	/**
	 * Извлекает и сохраняет вещественные значения в JsonObject. 
	 */
	public static final JsonAccessor<Double> DOUBLE = new JsonAccessor<>(
		node -> {
			checkState(node.isDouble());
			return node.asDouble();
		},
		ObjectNode::put
	);
	
	/**
	 * Извлекает и сохраняет строковые значения в JsonObject. 
	 */
	public static final JsonAccessor<String> STRING = new JsonAccessor<>(
		node -> {
			checkState(node.isTextual());
			return node.asText();
		},
		ObjectNode::put
	);

	/**
	 * Извлекает и сохраняет значения типа "Массив строк" в JsonObject
	 */
	public static final JsonAccessor<List<String>> STRING_ARRAY = new JsonAccessor<>(
		node -> {
			checkState(node.isArray());
			ArrayNode valueRoot = (ArrayNode) node;

			List<String> result = new ArrayList<>();
			Iterator<JsonNode> it = valueRoot.elements();
			while (it.hasNext()) {
				JsonNode item = it.next();
				checkState(item.isTextual());
				result.add(item.asText());
			}
			return result;
		},
		(node, name, value) -> {
			ArrayNode valueRoot = node.putArray(name);
			value.forEach(valueRoot::add);
		}
	);

	/**
	 * Извлекает и сохраняет значения датовремени в JsonObject. Сохраняет как инстант, что позволяет выполнять 
	 * над датовременем операции сравнения на уровне хранилища данных без каста к датовремени 
	 */
	public static final JsonAccessor<Date> DATE = new JsonAccessor<>(
		node -> {
			checkState(node.isInt() || node.isLong());
			return new Date(node.asLong());
		}, 
		(node, name, value) -> node.put(name, value.getTime())
	);
	
	/**
	 * Извлекает и сохраняет ссылку на персистентную сущность в JsonObject.
	 */
	public static final JsonEntityAccessor ENTITY = new JsonEntityAccessor();

	/**
	 * Извлекает и сохраняет коллекцию ссылок на персистентную сущность в JsonObject.
	 */
	public static final JsonEntityArrayAccessor ENTITY_ARRAY = new JsonEntityArrayAccessor();
	
	// @formatter:on
}
