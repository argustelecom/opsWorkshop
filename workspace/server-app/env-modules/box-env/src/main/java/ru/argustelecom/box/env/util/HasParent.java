package ru.argustelecom.box.env.util;

import ru.argustelecom.system.inf.exception.BusinessException;

public interface HasParent<T extends HasParent<T>> {

	T getParent();

	void changeParent(T newParent);

	/**
	 * Проверяет иерархию на цикличность ссылок
	 */
	default void checkCircularDependency(T parent) {
		while (parent != null) {
			if (parent.equals(this)) {
				throw new BusinessException();
			}
			parent = parent.getParent();
		}
	}

	/**
	 * Возвращает коренной элемент группы
	 */
	@SuppressWarnings("unchecked")
	default T findRoot() {
		T parent = (T) this;
		while (parent.getParent() != null) {
			parent = parent.getParent();
		}
		return parent;
	}
}