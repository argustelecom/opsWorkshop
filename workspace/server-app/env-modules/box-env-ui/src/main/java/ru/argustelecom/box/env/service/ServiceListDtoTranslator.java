package ru.argustelecom.box.env.service;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.inject.Inject;

import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class ServiceListDtoTranslator implements DefaultDtoTranslator<ServiceListDto, Service> {
	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Override
	public ServiceListDto translate(Service service) {
		checkNotNull(service);

		//@formatter:off
		return ServiceListDto.builder()
					.id(service.getId())
					.serviceType(businessObjectDtoTr.translate(service.getType()))
					.state(service.getState())
					.productType(businessObjectDtoTr.translate(service.getSubject().getProductOffering().getProductType()))
					.contract(businessObjectDtoTr.translate(service.getSubject().getContract()))
					.customer(businessObjectDtoTr.translate(service.getSubject().getContract().getCustomer()))
				.build();
		//@formatter:on
	}
}