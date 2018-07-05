package ru.argustelecom.box.nri.schema.requirements.resources.comparators;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Компаратор для чисел
 * Created by s.kolyada on 06.10.2017.
 */
public class NumberToNumberComparator implements IParameterDataTypeComparator {

	/**
	 * Поддерживаемые типы сравнений
	 */
	private static final List<CompareAction> supportedComparations = new ArrayList<>();

	static {
		supportedComparations.add(CompareAction.NOT_EQUALS);
		supportedComparations.add(CompareAction.EQUALS);
		supportedComparations.add(CompareAction.MORE);
		supportedComparations.add(CompareAction.LESS);
	}

	@Override
	public List<CompareAction> supportedComparations() {
		return Collections.unmodifiableList(supportedComparations);
	}

	@Override
	public boolean compare(CompareAction action, String o1, String o2) {
		if (isAnyBlank(o1, o2)) {
			return false;
		}
		if (!supportedComparations.contains(action)) {
			throw new IllegalStateException("Unsuported comparation action " + action.name());
		}

		BigDecimal b1;
		BigDecimal b2;

		try {
			b1 = new BigDecimal(o1);
			b2 = new BigDecimal(o2);
		} catch (NumberFormatException nfe) {
			//игнорируем
			return false;
		}

		switch (action) {
			case MORE: return b1.compareTo(b2) > 0;
			case LESS: return b1.compareTo(b2) < 0;
			case NOT_EQUALS: return b1.compareTo(b2) != 0;
			default: return b1.compareTo(b2) == 0;
		}
	}
}