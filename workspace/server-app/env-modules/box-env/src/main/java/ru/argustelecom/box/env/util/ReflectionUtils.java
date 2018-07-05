package ru.argustelecom.box.env.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * Перенести в инфраструктуру Argus
 */
public class ReflectionUtils {
	public static List<Field> getFields(Class<?> dataClass) {

		LinkedList<Field[]> fields = new LinkedList<>();
		Class<?> currentClass = dataClass;
		while (currentClass != null) {
			fields.add(currentClass.getDeclaredFields());
			currentClass = currentClass.getSuperclass();
			if (Objects.equals(Object.class, currentClass)) {
				currentClass = null;
			}
		}

		List<Field> result = new ArrayList<>();
		Field[] declaredFields = fields.pollLast();
		while (declaredFields != null) {
			for (Field field : declaredFields) {
				field.setAccessible(true);
				result.add(field);
			}
			declaredFields = fields.pollLast();
		}

		return result;
	}
}
