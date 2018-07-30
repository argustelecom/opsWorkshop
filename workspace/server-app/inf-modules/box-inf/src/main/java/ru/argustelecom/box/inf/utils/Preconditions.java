package ru.argustelecom.box.inf.utils;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.Collection;
import java.util.Objects;

import com.google.common.base.Strings;

public final class Preconditions {

	private Preconditions() {
	}

	public static <T> T checkRequiredArgument(T argument, String argumentName) {
		boolean specified = argument instanceof String ? !Strings.isNullOrEmpty((String) argument) : argument != null;
		checkArgument(specified, "%s is required", argumentName);
		return argument;
	}

	/**
	 * Проверяет, что элементы коллекции не равны null
	 */
	public static <T, C extends Collection<T>> C checkCollectionState(C collection, String argumentName) {
		boolean hasNulls = checkRequiredArgument(collection, argumentName).stream().anyMatch(Objects::isNull);
		checkArgument(!hasNulls, "%s has null entries", argumentName);
		return collection;
	}
}
