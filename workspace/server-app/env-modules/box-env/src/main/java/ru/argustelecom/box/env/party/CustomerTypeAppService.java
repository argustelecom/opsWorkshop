package ru.argustelecom.box.env.party;

import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.inf.service.ApplicationService;

import javax.inject.Inject;
import java.util.List;

@ApplicationService
public class CustomerTypeAppService {
	@Inject
	private CustomerTypeRepository customerTypeRp;

	public List<CustomerType> findAllCustomerTypes() {
		return customerTypeRp.getAllCustomerTypes();
	}
}
