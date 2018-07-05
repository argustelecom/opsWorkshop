package ru.argustelecom.box.nri.ports.model;

import com.google.common.base.Verify;
import lombok.Getter;
import ru.argustelecom.box.inf.nls.LocaleUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Тип порта
 */
public enum PortType {

	ETHERNET_PORT("{PortsBundle:box.nri.ports.port.type.ethernet}", Arrays.asList(TransmissionMedium.COPPER), AccessTechnology.FTTx ),

	XDSL_PORT("{PortsBundle:box.nri.ports.port.type.xdsl}", Arrays.asList(TransmissionMedium.COPPER), AccessTechnology.xDSL),

	PASSIVE_PORT("{PortsBundle:box.nri.ports.port.type.passive}", Arrays.asList(TransmissionMedium.COPPER, TransmissionMedium.OPTIC),
			AccessTechnology.FTTx, AccessTechnology.xPON, AccessTechnology.xDSL),

	OPTIC_TRANSCEIVER("{PortsBundle:box.nri.ports.port.type.opttrancsiever}", Arrays.asList(TransmissionMedium.OPTIC),
			AccessTechnology.FTTx, AccessTechnology.xPON),

	PON_PORT("{PortsBundle:box.nri.ports.port.type.pon}", Arrays.asList(TransmissionMedium.OPTIC), AccessTechnology.xPON),

	COMBO_PORT("{PortsBundle:box.nri.ports.port.type.combo}", Arrays.asList(TransmissionMedium.COPPER, TransmissionMedium.OPTIC, TransmissionMedium.NOT_CHOSEN), AccessTechnology.FTTx, AccessTechnology.xPON),

	OPTIC_SPLITTER("{PortsBundle:box.nri.ports.port.type.optsplitter}", Arrays.asList(TransmissionMedium.OPTIC), AccessTechnology.xPON);

	/**
	 * Поддерживаемые портом технологии доступа
	 */
	@Getter
	private final List<AccessTechnology> supportedAccessTechnologies;

	/**
	 * Поддерживаемые среды передачи
	 */
	@Getter
	private final List<TransmissionMedium> supportedTransmissionMediums;

	/**
	 * Название
	 */
	@Getter
	private String name;

	/**
	 * Конструктор с технологиями доступа
	 *
	 * @param supportedAccessTechnologies поддерживаемые технологии доступа
	 */
	PortType(String name, List<TransmissionMedium> supportedTransmissionMedium, AccessTechnology... supportedAccessTechnologies) {
		Verify.verify(supportedAccessTechnologies.length > 0);
		this.name = name;
		List<AccessTechnology> technologies;
		if (supportedAccessTechnologies.length == 1) {
			technologies = Collections.singletonList(supportedAccessTechnologies[0]);
		} else {
			technologies = Arrays.asList(supportedAccessTechnologies);
		}
		this.supportedTransmissionMediums = Collections.unmodifiableList(supportedTransmissionMedium);
		this.supportedAccessTechnologies = Collections.unmodifiableList(technologies);
	}

	public String getName() {
		return LocaleUtils.getLocalizedMessage(name, getClass());
	}
}
