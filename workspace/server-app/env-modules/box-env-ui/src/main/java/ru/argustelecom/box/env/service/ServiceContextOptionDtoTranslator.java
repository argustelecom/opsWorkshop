package ru.argustelecom.box.env.service;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@DtoTranslator
public class ServiceContextOptionDtoTranslator implements DefaultDtoTranslator<ServiceContextOptionDto, Service> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public ServiceContextOptionDto translate(Service service) {
		//@formatter:off
		return ServiceContextOptionDto.builder()
				.id(service.getId())
				.contractId(service.getSubject().getContract().getId())
				.entryId(service.getSubject().getId())
				.customerId(service.getSubject().getContract().getCustomer().getId())
				.specId(EntityManagerUtils.initializeAndUnproxy(service.getPrototype()).getId())
				.optionTypes(businessObjectDtoTr.translate(service.getType().getOptionTypes()))
				.customerTypeId(service.getSubject().getContract().getCustomer().getTypeInstance().getType().getId())
				.build();
		//@formatter:on
	}
}