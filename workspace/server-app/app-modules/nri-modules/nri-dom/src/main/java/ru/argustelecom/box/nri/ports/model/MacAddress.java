package ru.argustelecom.box.nri.ports.model;

import com.google.common.base.Verify;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.regex.Pattern;

/**
 * МАК-адрес
 */
public class MacAddress implements Serializable {

	private static final long serialVersionUID = -5902286098973295934L;

	private static final Pattern MAC_PATTERN = Pattern.compile("^([0-9A-Fa-f]{2}[:-]){5}([0-9A-Fa-f]{2})$");

	/**
	 * МАК-адрес
	 */
	@Getter
	private final String macAddress;

	/**
	 * Конструктор
	 * @param macAddress
	 */
	public MacAddress(String macAddress) {
		Verify.verify(StringUtils.isNotBlank(macAddress));
		if (!MAC_PATTERN.matcher(macAddress).matches()) {
			throw new IllegalStateException();
		}
		this.macAddress = macAddress;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof MacAddress)) return false;

		MacAddress that = (MacAddress) o;

		return macAddress.equals(that.macAddress);
	}

	@Override
	public int hashCode() {
		return macAddress.hashCode();
	}
}
