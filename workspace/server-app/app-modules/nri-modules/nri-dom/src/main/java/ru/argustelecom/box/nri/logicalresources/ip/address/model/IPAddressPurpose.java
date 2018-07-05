package ru.argustelecom.box.nri.logicalresources.ip.address.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.util.List;

import static java.util.Arrays.asList;

/**
 * Назначение IP-адреса
 * Created by s.kolyada on 16.01.2018.
 */
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public enum IPAddressPurpose implements NamedObject {

	//@formatter:off
	SERVICE("{IpAddressPurposeBundle:box.nri.logical.ip.purpose.service}"),
	CONFIGURATION("{IpAddressPurposeBundle:box.nri.logical.ip.purpose.cofiguration}"),
	RESERVED("{IpAddressPurposeBundle:box.nri.logical.ip.purpose.reserved}"),
	NOT_SPECIFIED("{IpAddressPurposeBundle:box.nri.logical.ip.purpose.unspecified}");
	//@formatter:on

	private String name;

	/**
	 * Получить список значений в листе
	 *
	 * @return лист значений
	 */
	public static List<IPAddressPurpose> listOfValues() {
		return asList(values());
	}

	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}

	public String getObjectName() {
		return getName();
	}
}
