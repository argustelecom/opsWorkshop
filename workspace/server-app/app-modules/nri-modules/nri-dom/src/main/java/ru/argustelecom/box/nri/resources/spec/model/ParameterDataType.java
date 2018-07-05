package ru.argustelecom.box.nri.resources.spec.model;

import lombok.Getter;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.IParameterDataTypeComparator;
import ru.argustelecom.box.nri.schema.requirements.resources.comparators.ParameterDataTypeExtension;

import java.util.Map;
import java.util.regex.Pattern;

/**
 * Тип данных параметра ресурса
 * Created by s.kolyada on 18.09.2017.
 */
public enum ParameterDataType {

	/**
	 * Любой текст, не может быть пустым
	 */
	STRING(".*[\\wа-яА-Я]+.*"),

	/**
	 * Целое число
	 */
	INTEGER("-?\\d+"),

	/**
	 * Число с дробной частью
	 */
	FLOAT("-?\\d+(\\.\\d+)?"),

	/**
	 * Истина/Ложь
	 */
	BOOLEAN("(?Ui)^(true|false)");

	/**
	 * Регулярное выражение для валидации
	 */
	@Getter
	private String regex;

	/**
	 * паттерн для валидации
	 */
	private Pattern pattern;

	/**
	 * Конструктор с регулярным выражением
	 * @param defaultRegex регулярное выражение
	 */
	ParameterDataType(String defaultRegex) {
		this.regex = defaultRegex;
		this.pattern = Pattern.compile(defaultRegex);
	}

	/**
	 * Валидация значение по типу данных
	 * @param value значение
	 * @return истина если оно валидно, иначе ложь
	 */
	public boolean validate(String value) {
		return pattern.matcher(value).matches();
	}

	/**
	 * Получить доступные для данного типа компараторы
	 * @return компараторы
	 */
	public Map<ParameterDataType, IParameterDataTypeComparator> comaprators() {
		return ParameterDataTypeExtension.comapratorsFor(this);
	}

	/**
	 * Получить компаратор для текущего типа с самим собой
	 * @return компаратор, если существует, иначе null
	 */
	public IParameterDataTypeComparator comparator() {
		return ParameterDataTypeExtension.comapratorsFor(this).get(this);
	}

}
