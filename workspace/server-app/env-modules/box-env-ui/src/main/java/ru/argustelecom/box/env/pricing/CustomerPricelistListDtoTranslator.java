package ru.argustelecom.box.env.pricing;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class CustomerPricelistListDtoTranslator implements DefaultDtoTranslator<CustomerPricelistListDto, Customer> {
	@Override
	public CustomerPricelistListDto translate(Customer customer) {
		return new CustomerPricelistListDto(customer.getId(), customer.getObjectName());
	}
}
