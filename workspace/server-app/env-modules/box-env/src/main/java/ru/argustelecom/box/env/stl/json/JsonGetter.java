package ru.argustelecom.box.env.stl.json;

import com.fasterxml.jackson.databind.JsonNode;

@FunctionalInterface
interface JsonGetter<O> {
	O get(JsonNode node);
}