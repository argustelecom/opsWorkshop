package ru.argustelecom.box.nri.schema.requirements.resources.comparators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Компаратор для 2 строк
 * Created by s.kolyada on 06.10.2017.
 */
public class StringToStringComparator implements IParameterDataTypeComparator {

	/**
	 * Поддержэиваемые варианты сравнений
	 */
	private static final List<CompareAction> supportedComparations = new ArrayList<>();

	static {
		supportedComparations.add(CompareAction.NOT_EQUALS);
		supportedComparations.add(CompareAction.EQUALS);
		supportedComparations.add(CompareAction.CONTAINS);
		supportedComparations.add(CompareAction.CONTAINS_IN);
	}

	@Override
	public List<CompareAction> supportedComparations() {
		return Collections.unmodifiableList(supportedComparations);
	}

	@Override
	public boolean compare(CompareAction action, String o1, String o2) {
		if (!supportedComparations.contains(action)) {
			throw new IllegalStateException("Unsuported comparation action " + action.name());
		}

		// если хоть одна строка пустая, то проверяем отличным от обычного способом
		if (isAnyBlank(o1, o2)) {
			switch (action) {
				// содержится и содержит - всегда ложь
				case CONTAINS:
				case CONTAINS_IN:
					return false;
				// равенствор и неравенство зависит от того, пусты ли оба или нет
				case NOT_EQUALS:
					return !isAllBlank(o1, o2);
				default:
					return isAllBlank(o1, o2);
			}
		}

		switch (action) {
			case CONTAINS:
				return o1.contains(o2);
			case CONTAINS_IN:
				return o2.contains(o1);
			case NOT_EQUALS:
				return !o1.equals(o2);
			default:
				return o1.equals(o2);
		}
	}
}