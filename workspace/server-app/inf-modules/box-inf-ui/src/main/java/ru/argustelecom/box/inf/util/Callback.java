package ru.argustelecom.box.inf.util;

/**
 * Интерфейс, который по факту является указателем на функцию. Типизирован, и метод принимает на вход объект такого типа
 *
 * @param <T> тип объекта, который будет передан в качестве параметра
 */
@FunctionalInterface
public interface Callback<T> {

	void execute(T instance);
}
