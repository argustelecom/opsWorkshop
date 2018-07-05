package ru.argustelecom.box.nri.ports;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jboss.logging.Logger;
import ru.argustelecom.box.nri.ports.model.PortType;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Конвертер списка типов портов в одну строку и обратно
 */
@Converter
public class PortTypeEnumListConverter implements AttributeConverter<Set<PortType>, String> {

	private static final Logger log = Logger.getLogger(PortTypeEnumListConverter.class);

	@Override
	public String convertToDatabaseColumn(Set<PortType> attribute) {
		if (CollectionUtils.isEmpty(attribute)) {
			return "";
		}

		String result = StringUtils.join(attribute,",");

		if (result.length() > 256) {
			log.warn("String representation of List<Enum> is too long. Some elements are skipped!");
			result = result.substring(0,255).substring(0, result.lastIndexOf(","));
		}

		return result;
	}

	@Override
	public Set<PortType> convertToEntityAttribute(String dbData) {
		if (StringUtils.isBlank(dbData)) {
			// тк используется для инициализации сущности, то возвращаем реальный лист, а не заглушку
			// с пустым листом, как могли бы, тк элемент может модифицироваться после инициализации
			return new HashSet<>();
		}
		String[] portTypes = StringUtils.split(dbData, ",");
		if (portTypes.length < 1) {
			log.warn("String representation of List<Enum> was not valid: " + dbData);
			return new HashSet<>();
		}
		return Arrays.asList(portTypes)
				.stream()
				.map(pt -> PortType.valueOf(pt))
				.collect(Collectors.toSet());
	}
}
