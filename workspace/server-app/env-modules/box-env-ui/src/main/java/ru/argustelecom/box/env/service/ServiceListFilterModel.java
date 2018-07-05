package ru.argustelecom.box.env.service;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.env.commodity.model.Service.ServiceQuery;
import static ru.argustelecom.box.env.service.ServiceListViewState.ServiceFilter.CONTRACT_NUMBER;
import static ru.argustelecom.box.env.service.ServiceListViewState.ServiceFilter.CUSTOMER;
import static ru.argustelecom.box.env.service.ServiceListViewState.ServiceFilter.CUSTOMER_TYPE;
import static ru.argustelecom.box.env.service.ServiceListViewState.ServiceFilter.ID;
import static ru.argustelecom.box.env.service.ServiceListViewState.ServiceFilter.PRODUCT_TYPE;
import static ru.argustelecom.box.env.service.ServiceListViewState.ServiceFilter.SERVICE_STATE;
import static ru.argustelecom.box.env.service.ServiceListViewState.ServiceFilter.SERVICE_TYPE;

import java.util.function.Supplier;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("serviceFilterFm")
@PresentationModel
public class ServiceListFilterModel extends BaseEQConvertibleDtoFilterModel<ServiceQuery> {

	@Inject
	private ServiceListViewState serviceListVs;

	@Override
	public void buildPredicates(ServiceQuery query) {
		serviceListVs.getFilterMap().forEach((key, value) -> {
			switch (key) {
			case ID:
				addPredicate(query.id().equal(value));
				break;
			case SERVICE_TYPE:
				addPredicate(query.serviceType().equal(cast(value)));
				break;
			case SERVICE_STATE:
				addPredicate(query.state().equal(value));
				break;
			case PRODUCT_TYPE:
				addPredicate(query.productType().equal(cast(value)));
				break;
			case CONTRACT_NUMBER:
				addPredicate(query.contractNumber().like(surroundWithWildcard(value)));
				break;
			case CUSTOMER_TYPE:
				addPredicate(query.customerType().equal(cast(value)));
				break;
			case CUSTOMER:
				addPredicate(query.customer().equal(cast(value)));
				break;
			default:
				break;
			}
		});

		if (serviceListVs.getPropertyFilters() != null) {
			addPredicate(serviceListVs.getPropertyFilters().toJpaPredicate(query.properties()));
		}
	}

	@Override
	public Supplier<ServiceQuery> entityQuerySupplier() {
		return () -> new ServiceQuery<>(Service.class);
	}

	private Identifiable cast(Object obj) {
		checkArgument(obj instanceof BusinessObjectDto);
		return ((BusinessObjectDto) obj).getIdentifiable();
	}

	private String surroundWithWildcard(Object obj) {
		return String.format("%%%s%%", obj);
	}

	private static final long serialVersionUID = -2378729950996630722L;
}
