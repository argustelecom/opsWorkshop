package ru.argustelecom.box.env.service;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode(of = { "id" }, callSuper = false)
public class ServiceListDto extends ConvertibleDto {
	private Long id;
	private BusinessObjectDto<ServiceType> serviceType;
	private ServiceState state;
	private BusinessObjectDto<AbstractProductType> productType;
	private BusinessObjectDto<AbstractContract<?>> contract;
	private BusinessObjectDto<Customer> customer;

	@Builder
	public ServiceListDto(Long id, BusinessObjectDto<ServiceType> serviceType, ServiceState state,
						  BusinessObjectDto<AbstractProductType> productType, BusinessObjectDto<AbstractContract<?>> contract,
						  BusinessObjectDto<Customer> customer) {
		this.id = id;
		this.serviceType = serviceType;
		this.state = state;
		this.productType = productType;
		this.contract = contract;
		this.customer = customer;
	}

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return ServiceListDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return Service.class;
	}
}
