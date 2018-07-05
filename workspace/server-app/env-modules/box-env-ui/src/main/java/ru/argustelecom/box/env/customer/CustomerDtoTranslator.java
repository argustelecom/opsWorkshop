package ru.argustelecom.box.env.customer;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class CustomerDtoTranslator implements DefaultDtoTranslator<CustomerDto, Customer> {
	@Override
	public CustomerDto translate(Customer customer) {
		//@formatter:off
		return CustomerDto.builder()
				.id(customer.getId())
				.name(customer.getObjectName())
				.type(customer.getTypeInstance().getType().getObjectName())
				.build();
		//@formatter:on
	}
}