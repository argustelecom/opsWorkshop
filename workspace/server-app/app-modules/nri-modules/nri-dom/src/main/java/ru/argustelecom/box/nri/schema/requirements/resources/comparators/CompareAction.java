package ru.argustelecom.box.nri.schema.requirements.resources.comparators;

import ru.argustelecom.box.inf.nls.LocaleUtils;

/**
 * Возможные варианты сравнений
 * Created by s.kolyada on 18.09.2017.
 */
public enum CompareAction {

	EQUALS("{CompareActionBundle:nri.schema.requirements.resources.comparators.equals}"),

	NOT_EQUALS("{CompareActionBundle:nri.schema.requirements.resources.comparators.not_equals}"),

	CONTAINS("{CompareActionBundle:nri.schema.requirements.resources.comparators.contains}"),

	CONTAINS_IN("{CompareActionBundle:nri.schema.requirements.resources.comparators.contains_in}"),

	MORE("{CompareActionBundle:nri.schema.requirements.resources.comparators.more}"),

	LESS("{CompareActionBundle:nri.schema.requirements.resources.comparators.less}");

	private String name;

	CompareAction(String name) {
		this.name = name;
	}

	public String getName(){
		return LocaleUtils.getLocalizedMessage(name,getClass());
	}

}