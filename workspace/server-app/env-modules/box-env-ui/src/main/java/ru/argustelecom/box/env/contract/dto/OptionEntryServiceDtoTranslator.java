package ru.argustelecom.box.env.contract.dto;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class OptionEntryServiceDtoTranslator implements DefaultDtoTranslator<OptionEntryServiceDto, Service> {

	@Override
	public OptionEntryServiceDto translate(Service service) {
		return new OptionEntryServiceDto(service.getId(), service.getObjectName(),
				service.getSubject().getContract().getDocumentNumber());
	}

}
