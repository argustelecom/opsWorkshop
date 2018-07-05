package ru.argustelecom.box.nri.ports;

import ru.argustelecom.box.nri.ports.model.MacAddress;

import javax.persistence.AttributeConverter;

/**
 * Конвертер МАК-адресов в строку и обратно для хранения в БД в виде строки
 */
public class MacAddressConverter implements AttributeConverter<MacAddress, String> {

	@Override
	public String convertToDatabaseColumn(MacAddress attribute) {
		return attribute == null ? null : attribute.getMacAddress();
	}

	@Override
	public MacAddress convertToEntityAttribute(String dbData) {
		return dbData == null ? null : new MacAddress(dbData);
	}
}
