package ru.argustelecom.box.env.service;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ServiceAttributesDtoTranslator {

	@Inject
	private BusinessObjectDtoTranslator translator;

	public ServiceAttributesDto translate(Service service) {
		//@formatter:off
		return ServiceAttributesDto.builder()
				.id(service.getId())
				.serviceType(translator.translate(service.getType()))
				.state(service.getState())
				.contract(translator.translate(service.getSubject().getContract()))
				.product(translator.translate(service.getSubject().getProductOffering().getProductType()))
			.build(); 
		//@formatter:on
	}

}
