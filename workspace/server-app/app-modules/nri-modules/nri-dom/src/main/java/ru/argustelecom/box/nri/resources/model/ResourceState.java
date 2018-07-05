package ru.argustelecom.box.nri.resources.model;

import ru.argustelecom.box.inf.nls.LocaleUtils;

/**
 * Состояние ресурса
 */
public enum ResourceState {

	FREE("{ResourceStateBundle:ru.argustelecom.box.nri.resources.state.free}"),

	BOOKED("{ResourceStateBundle:ru.argustelecom.box.nri.resources.state.booked}"),

	LOADED("{ResourceStateBundle:ru.argustelecom.box.nri.resources.state.loaded}"),;


	private String name;

	ResourceState(String name) {
		this.name = name;
	}

	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	/**
	 * статус по умолчанию
	 * @return
	 */
	public static ResourceState defaultState() {
		return ResourceState.FREE;
	}
}
