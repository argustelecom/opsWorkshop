package ru.argustelecom.box.nri.schema.requirements.resources.comparators;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

/**
 * Интерфейс для компаратора типов данных
 * Created by s.kolyada on 06.10.2017.
 */
public interface IParameterDataTypeComparator {

	/**
	 * Получить список поддерэиваемых вариантов сравнения
	 * @return список
	 */
	List<CompareAction> supportedComparations();

	/**
	 * Сравнить 2 значение
	 * @param action вариант сравнения
	 * @param o1 значение 1
	 * @param o2 значение 2
	 * @return true если сравнение срабатывает иначе false
	 */
	boolean compare(CompareAction action, String o1, String o2);

	/**
	 * Проверяет если ли хоть одно пустое значение
	 * @param strings строки
	 * @return истина, если есть, иначе ложь
	 */
	default boolean isAnyBlank(String... strings) {
		for (String str : strings) {
			if (StringUtils.isBlank(str)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Проверяет, что все строки пстые
	 * @param strings строки
	 * @return истина. если все пустые, иначе ложь
	 */
	default boolean isAllBlank(String... strings) {
		for (String str : strings) {
			if (!StringUtils.isBlank(str)) {
				return false;
			}
		}
		return true;
	}
}
