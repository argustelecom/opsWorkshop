package ru.argustelecom.box.env.service;

import static ru.argustelecom.box.env.service.ServiceLazyDataModel.ServiceSort;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.commodity.model.CommoditySpec_;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.model.Service.ServiceQuery;
import ru.argustelecom.box.env.commodity.model.Service_;
import ru.argustelecom.box.env.contract.model.AbstractContract_;
import ru.argustelecom.box.env.contract.model.ContractEntry_;
import ru.argustelecom.box.env.contract.model.ProductOfferingContractEntry_;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.pricing.model.ProductOffering_;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("serviceLazyDataModel")
@PresentationModel
public class ServiceLazyDataModel
		extends EQConvertibleDtoLazyDataModel<Service, ServiceListDto, ServiceQuery, ServiceSort> {

	@Inject
	private ServiceListFilterModel serviceListFilterModel;

	@Inject
	private ServiceListDtoTranslator serviceListDtoTr;

	@PostConstruct
	private void postConstruct() {
		initPathMap();
	}

	private void initPathMap() {
		addPath(ServiceSort.id, query -> query.root().get(Service_.id));
		addPath(ServiceSort.serviceType, query -> query.root().join(Service_.prototype).join(CommoditySpec_.type));
		addPath(ServiceSort.state, query -> query.root().get(Service_.state));
		addPath(ServiceSort.productType, query -> query.root().join(Service_.subject)
				.join(ProductOfferingContractEntry_.productOffering).join(ProductOffering_.productType));
		addPath(ServiceSort.contract, query -> query.root().join(Service_.subject).join(ContractEntry_.contract));
		addPath(ServiceSort.customer, query -> query.root().join(Service_.subject).join(ContractEntry_.contract)
				.join(AbstractContract_.customer));
	}

	@Override
	protected EQConvertibleDtoFilterModel<ServiceQuery> getFilterModel() {
		return serviceListFilterModel;
	}

	@Override
	protected Class<ServiceSort> getSortableEnum() {
		return ServiceSort.class;
	}

	@Override
	protected DefaultDtoTranslator<ServiceListDto, Service> getDtoTranslator() {
		return serviceListDtoTr;
	}

	public enum ServiceSort {
		id, serviceType, state, productType, contract, customer
	}

	private static final long serialVersionUID = -4382159475770305073L;
}
