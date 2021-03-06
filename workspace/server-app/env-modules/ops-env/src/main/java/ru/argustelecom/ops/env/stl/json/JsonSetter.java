package ru.argustelecom.ops.env.stl.json;

import com.fasterxml.jackson.databind.node.ObjectNode;

@FunctionalInterface
interface JsonSetter<O> {
	void set(ObjectNode node, String fieldName, O fieldValue);
}
