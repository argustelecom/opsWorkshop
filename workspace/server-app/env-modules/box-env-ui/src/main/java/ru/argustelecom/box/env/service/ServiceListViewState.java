package ru.argustelecom.box.env.service;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.product.model.ProductType;
import ru.argustelecom.box.env.type.model.TypePropertyFilterContainer;
import ru.argustelecom.system.inf.page.PresentationState;

@Getter
@Setter
@Named("serviceListVs")
@PresentationState
public class ServiceListViewState extends FilterViewState {

	@FilterMapEntry(ServiceFilter.ID)
	private Long id;

	// TODO FIXME когда будет рефакторинг BusinessObjectDto
	@FilterMapEntry(value = ServiceFilter.SERVICE_TYPE, isBusinessObjectDto = true)
	private BusinessObjectDto<ServiceType> serviceType;

	@FilterMapEntry(ServiceFilter.SERVICE_STATE)
	private ServiceState state;

	@FilterMapEntry(value = ServiceFilter.PRODUCT_TYPE, isBusinessObjectDto = true)
	private BusinessObjectDto<ProductType> productType;

	@FilterMapEntry(value = ServiceFilter.CONTRACT_NUMBER)
	private String contractNumber;

	@FilterMapEntry(value = ServiceFilter.CUSTOMER_TYPE, isBusinessObjectDto = true)
	private BusinessObjectDto<CustomerType> customerType;

	@FilterMapEntry(value = ServiceFilter.CUSTOMER, isBusinessObjectDto = true)
	private BusinessObjectDto<Customer> customer;

	private TypePropertyFilterContainer propertyFilters;

	@Override
	public void clearParamsJSF() {
		super.clearParamsJSF();
		propertyFilters.clearValues();
	}

	static class ServiceFilter {
		public static final String ID = "ID";
		public static final String SERVICE_TYPE = "SERVICE_TYPE";
		public static final String SERVICE_STATE = "SERVICE_STATE";
		public static final String PRODUCT_TYPE = "PRODUCT_TYPE";
		public static final String CONTRACT_NUMBER = "CONTRACT_NUMBER";
		public static final String CUSTOMER_TYPE = "CUSTOMER_TYPE";
		public static final String CUSTOMER = "CUSTOMER";
	}

	private static final long serialVersionUID = -4842718022459184797L;
}
