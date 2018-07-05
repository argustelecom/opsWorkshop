package ru.argustelecom.box.nri.logicalresources;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.dto2.ConvertibleDto;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResourceType;
import ru.argustelecom.system.inf.modelbase.NamedObject;

import java.io.Serializable;

/**
 * ДТО логического ресурса
 * Created by s.kolyada on 06.02.2018.
 */
@Getter
@EqualsAndHashCode(of = "id", callSuper = false)
public class LogicalResourceDto extends ConvertibleDto implements Serializable, NamedObject {

	private static final long serialVersionUID = 1L;

	/**
	 * Идентификтор
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
	public LogicalResourceDto(Long id, String resourceName, LogicalResourceType type) {
		this.id = id;
		this.resourceName = resourceName;
		this.type = type;
	}

	@Override
	public Class<LogicalResourceDtoTranslator> getTranslatorClass() {
		return LogicalResourceDtoTranslator.class;
	}

	@Override
	public Class<LogicalResource> getEntityClass() {
		return LogicalResource.class;
	}

	@Override
	public String getObjectName() {
		return resourceName;
	}
}
