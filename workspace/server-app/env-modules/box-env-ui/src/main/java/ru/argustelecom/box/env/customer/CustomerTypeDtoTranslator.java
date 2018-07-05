package ru.argustelecom.box.env.customer;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class CustomerTypeDtoTranslator implements DefaultDtoTranslator<CustomerTypeDto, CustomerType> {
	@Override
	public CustomerTypeDto translate(CustomerType customerType) {
		return new CustomerTypeDto(customerType.getId(), customerType.getObjectName(), customerType.getCategory());
	}
}
