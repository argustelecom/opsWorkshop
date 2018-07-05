package ru.argustelecom.box.env.saldo.imp.model;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class ClassProperty {

	Map<Integer, FieldProperty> orderNumFieldMap = new HashMap<>();

	public ClassProperty(Class<?> clazz) {
		Field[] fields = clazz.getDeclaredFields();

		for (Field field : fields) {
			try {
				Element element = field.getAnnotation(Element.class);
				orderNumFieldMap.put(element.orderNumber(), new FieldProperty(field, element));
			} catch (NullPointerException ignored) {
			}
		}
	}

	public FieldProperty find(Integer keyword) {
		return orderNumFieldMap.get(keyword);
	}

}