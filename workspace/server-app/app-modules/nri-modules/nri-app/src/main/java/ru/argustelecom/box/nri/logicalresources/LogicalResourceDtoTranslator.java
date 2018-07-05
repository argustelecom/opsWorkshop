package ru.argustelecom.box.nri.logicalresources;

import ru.argustelecom.box.env.dto2.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.box.nri.logicalresources.model.LogicalResource;

/**
 * Транслятор ДТО логического ресурса
 * Created by s.kolyada on 06.02.2018.
 */
@DtoTranslator
public class LogicalResourceDtoTranslator implements DefaultDtoTranslator<LogicalResourceDto, LogicalResource> {

	@Override
	public LogicalResourceDto translate(LogicalResource businessObject) {
		if (businessObject == null) {
			return null;
		}

		return LogicalResourceDto.builder()
				.id(businessObject.getId())
				.resourceName(businessObject.getObjectName())
				.type(businessObject.getType())
				.build();
	}
}
