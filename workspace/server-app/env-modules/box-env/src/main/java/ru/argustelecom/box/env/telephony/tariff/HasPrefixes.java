package ru.argustelecom.box.env.telephony.tariff;

import static java.util.stream.Collectors.joining;

import java.util.List;

public interface HasPrefixes {
	String DEFAULT_PREFIX_DELIMITER = ",";

	List<Integer> getPrefixes();

	default String getPrefixesAsString() {
		return getPrefixes().stream().map(Object::toString).collect(joining(DEFAULT_PREFIX_DELIMITER));
	}
}
