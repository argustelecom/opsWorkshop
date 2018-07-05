package ru.argustelecom.box.env.billing.invoice.chargejob;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ServiceChargeJobContextDtoTranslator implements DefaultDtoTranslator<ServiceChargeJobContextDto,Service> {

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public ServiceChargeJobContextDto translate(Service service) {
		return ServiceChargeJobContextDto.builder()
				.id(service.getId())
				.name(service.getObjectName())
				.contract(businessObjectDtoTr.translate(service.getSubject().getContract()))
				.type(businessObjectDtoTr.translate(service.getType()))
				.build();
	}
}
