package ru.argustelecom.box.env.numerationpattern.metamodel;

@FunctionalInterface
public interface NumerationPatternVariableCallback<T, V> {
	V get(T instance);
}
