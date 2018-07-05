package ru.argustelecom.box.nri.schema.requirements.resources.comparators;

import org.apache.commons.lang.BooleanUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Компаратор для boolean
 * Created by s.kolyada on 06.10.2017.
 */
public class BooleanComparator implements IParameterDataTypeComparator {

	/**
	 * Поддержэиваемые варианты сравнений
	 */
	private static final List<CompareAction> supportedComparations = new ArrayList<>();

	static {
		supportedComparations.add(CompareAction.NOT_EQUALS);
		supportedComparations.add(CompareAction.EQUALS);
	}

	@Override
	public List<CompareAction> supportedComparations() {
		return Collections.unmodifiableList(supportedComparations);
	}

	@Override
	public boolean compare(CompareAction action, String o1, String o2) {
		if (!supportedComparations.contains(action)) {
			throw new IllegalStateException("Unsupported compare action " + action.name());
		}

		Boolean b1 = BooleanUtils.toBooleanObject(o1);
		Boolean b2 = BooleanUtils.toBooleanObject(o2);

		if (b1 == null || b2 == null) {
			return !CompareAction.EQUALS.equals(action);
		}

		switch (action) {
			case NOT_EQUALS: return !b1.equals(b2);
			default: return b1.equals(b2);
		}
	}
}
