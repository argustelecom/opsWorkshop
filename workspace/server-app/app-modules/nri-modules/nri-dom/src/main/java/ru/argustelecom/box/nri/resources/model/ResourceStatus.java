package ru.argustelecom.box.nri.resources.model;


import ru.argustelecom.box.inf.nls.LocaleUtils;

/**
 * Статусы ресурсов
 * Created by s.kolyada on 18.09.2017.
 */
public enum ResourceStatus {

	ACTIVE("{ResourceStatusBundle:ru.argustelecom.box.nri.resources.status.active}"),

	DISABLED("{ResourceStatusBundle:ru.argustelecom.box.nri.resources.status.disable}"),

	RESERVED("{ResourceStatusBundle:ru.argustelecom.box.nri.resources.status.reserved}"),

	UNKNOWN("{ResourceStatusBundle:ru.argustelecom.box.nri.resources.status.unknown}");


	private String name;

	ResourceStatus(String name) {
		this.name = name;
	}

	public String getName(){
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	public static ResourceStatus defaultStatus() {
		return ResourceStatus.UNKNOWN;
	}
}
