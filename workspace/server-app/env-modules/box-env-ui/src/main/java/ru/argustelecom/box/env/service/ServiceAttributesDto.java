package ru.argustelecom.box.env.service;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.product.model.AbstractProductType;

@Getter
@NoArgsConstructor
public class ServiceAttributesDto {

	private Long id;
	private BusinessObjectDto<ServiceType> serviceType;
	private ServiceState state;
	private BusinessObjectDto<AbstractContract<?>> contract;
	private BusinessObjectDto<AbstractProductType> product;

	@Builder
	public ServiceAttributesDto(Long id, BusinessObjectDto<ServiceType> serviceType, ServiceState state,
			BusinessObjectDto<AbstractContract<?>> contract, BusinessObjectDto<AbstractProductType> product) {
		this.id = id;
		this.serviceType = serviceType;
		this.state = state;
		this.contract = contract;
		this.product = product;
	}

}
