package ru.argustelecom.box.env.service;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.commodity.CommodityTypeAppService;
import ru.argustelecom.box.env.commodity.model.ServiceState;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.CustomerAppService;
import ru.argustelecom.box.env.party.CustomerTypeAppService;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.product.ProductTypeAppService;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "serviceListVm")
@PresentationModel
public class ServiceListViewModel extends ViewModel {

	@Inject
	private ServiceListViewState serviceListViewState;

	@Inject
	private CustomerAppService customerAppSrv;

	@Inject
	private CustomerTypeAppService customerTypeAppSrv;

	@Inject
	private CommodityTypeAppService serviceTypeAppSrv;

	@Inject
	private ProductTypeAppService productTypeAppSrv;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	@Inject
	private ServiceLazyDataModel lazyDm;

	@Getter
	private List<BusinessObjectDto<ServiceType>> serviceTypes;
	@Getter
	private ServiceState[] states;
	@Getter
	private List<BusinessObjectDto<CustomerType>> customerTypes;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		initFilterData();
	}

	private void initFilterData() {
		serviceTypes = businessObjectDtoTr.translate(serviceTypeAppSrv.findAllServiceTypes());
		customerTypes = businessObjectDtoTr.translate(customerTypeAppSrv.findAllCustomerTypes());
		serviceListViewState.setPropertyFilters(serviceTypeAppSrv.createServiceTypePropertyFilters());
		states = ServiceState.values();
	}

	public List<BusinessObjectDto<Customer>> completeCustomer(String customerName) {
		if (serviceListViewState.getCustomerType() != null) {
			return customerAppSrv.findCustomerBy(serviceListViewState.getCustomerType().getId(), customerName).stream()
					.map(customer -> businessObjectDtoTr.translate((Customer) customer)).collect(Collectors.toList());
		}
		return Collections.emptyList();
	}

	public List<BusinessObjectDto<AbstractProductType>> completeProductType(String name) {
		return businessObjectDtoTr.translate(productTypeAppSrv.findProductTypesBy(name));
	}

	private static final long serialVersionUID = 6364872975735597669L;
}
