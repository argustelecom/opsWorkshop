package ru.argustelecom.box.nri.schema.requirements.resources.comparators;

import org.apache.commons.collections.map.HashedMap;
import ru.argustelecom.box.nri.resources.spec.model.ParameterDataType;

import java.util.Collections;
import java.util.Map;

/**
 * Расширение описания типов. Служит для вынесения в отдельный класс кучи описаний связей между типами
 * Created by s.kolyada on 06.10.2017.
 */
public class ParameterDataTypeExtension {

	/**
	 * компараторы для String
	 */
	private static final Map<ParameterDataType, IParameterDataTypeComparator> stringComarators;

	/**
	 * Компараторы для чисел
	 */
	private static final Map<ParameterDataType, IParameterDataTypeComparator> numberComparators;

	/**
	 * Компараторы для bool
	 */
	private static final Map<ParameterDataType, IParameterDataTypeComparator> boolComparators;

	/**
	 * Заполнение описаний
	 */
	static {
		stringComarators = Collections.singletonMap(ParameterDataType.STRING, new StringToStringComparator());

		Map<ParameterDataType, IParameterDataTypeComparator> comparators = new HashedMap();
		comparators.put(ParameterDataType.INTEGER, new NumberToNumberComparator());
		comparators.put(ParameterDataType.FLOAT, new NumberToNumberComparator());
		numberComparators = Collections.unmodifiableMap(comparators);

		boolComparators = Collections.singletonMap(ParameterDataType.BOOLEAN, new BooleanComparator());
	}

	private ParameterDataTypeExtension() {
		throw new IllegalAccessError("Utility class");
	}

	/**
	 * Получить компараторы по типу данных переменной
	 * @param type тип
	 * @return карта со всеми возможными типами дляя сравнения и компараторвами для этих связей
	 */
	public static Map<ParameterDataType, IParameterDataTypeComparator> comapratorsFor(ParameterDataType type) {
		switch (type) {
			case STRING:
				return stringComarators;
			case INTEGER:
			case FLOAT:
				return numberComparators;
			case BOOLEAN:
				return boolComparators;
			default:
				return Collections.emptyMap();
		}
	}
}
