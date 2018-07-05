package ru.argustelecom.box.env.telephony.tariff;

import lombok.Getter;
import java.util.Collections;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.customer.CustomerTypeDto;
import ru.argustelecom.box.env.customer.CustomerTypeDtoTranslator;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.dto.BusinessObjectDtoTranslator;
import ru.argustelecom.box.env.party.CustomerAppService;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

import static ru.argustelecom.box.env.dto.DefaultDtoConverterUtils.translate;

@PresentationModel
@Named(value = "tariffListVm")
public class TariffListViewModel extends ViewModel {

	private static final long serialVersionUID = -8491798733734428741L;

	@Inject
	@Getter
	private TariffLazyDataModel lazyDm;

	@Inject
	private TariffListViewState tariffListVs;

	@Inject
	private CustomerAppService customerAs;

	@Inject
	private CustomerTypeRepository customerTypeRp;

	@Inject
	private BusinessObjectDtoTranslator businessObjectDtoTr;

	@Getter
	private List<CustomerTypeDto> customerTypes;

	@Inject
	private CustomerTypeDtoTranslator customerTypeDtoTr;

	@Override
	@PostConstruct
	public void postConstruct() {
		super.postConstruct();
		unitOfWork.makePermaLong();
		customerTypes = translate(customerTypeDtoTr, customerTypeRp.getAllCustomerTypes());
	}

	public List<? extends BusinessObjectDto<? extends Customer>> completeCustomer(String customerName) {
		if (tariffListVs.getCustomerType() != null)
			return businessObjectDtoTr.translate(customerAs.findCustomerBy(tariffListVs.getCustomerType().getId(), customerName));
		return Collections.emptyList();
	}
}