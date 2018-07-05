package ru.argustelecom.box.nri.integration.viewmodel;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.nri.logicalresources.LogicalResourceDto;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;

/**
 * Холдер информации о нагруженных услугой ресурсах
 * Created by s.kolyada on 06.02.2018.
 */
@Getter
public class LogicalResourceDataHolder implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	private static final String IP_ADDR_URL = "views/nri/logicalresources/ipaddress/IpAddressView.xhtml?ipAddress=IPAddress-";

	private static final String PHONE_NUM_URL = "views/nri/logicalresources/PhoneNumberView.xhtml?phoneNumber=PhoneNumber-";

	/**
	 * Идентификатор
	 */
	private Long id;

	/**
	 * Имя ресурса
	 */
	private String resourceName;

	/**
	 * Тип ресурса
	 */
	private LogicalResourceType type;

	@Builder
	public LogicalResourceDataHolder(Long id, String resourceName, LogicalResourceType type) {
		this.id = id;
		this.resourceName = resourceName;
		this.type = type;
	}

	@Override
	public String getObjectName() {
		return resourceName;
	}

	/**
	 * СОбрать url на карточку ресурса
	 * @return путь к карточке ресурса
	 */
	public String buildResourceUrl() {
		switch (type) {
			case IP_ADDRESS: return IP_ADDR_URL + id;
			case PHONE_NUMBER: return PHONE_NUM_URL + id;
			default: return "";
		}
	}

	/**
	 * Преобразовать ДТО логического ресурса к холдеру
	 * @param logicalResourceDto логический ресурс
	 * @return холдер логического ресурса
	 */
	public static LogicalResourceDataHolder convert(LogicalResourceDto logicalResourceDto) {
		return LogicalResourceDataHolder.builder()
				.id(logicalResourceDto.getId())
				.resourceName(logicalResourceDto.getResourceName())
				.type(logicalResourceDto.getType())
				.build();
	}
}
