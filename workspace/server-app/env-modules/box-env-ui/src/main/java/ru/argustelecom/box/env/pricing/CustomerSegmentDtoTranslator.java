package ru.argustelecom.box.env.pricing;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class CustomerSegmentDtoTranslator implements DefaultDtoTranslator<CustomerSegmentDto, CustomerSegment> {
	@Override
	public CustomerSegmentDto translate(CustomerSegment customerSegment) {
		return new CustomerSegmentDto(customerSegment.getId(), customerSegment.getObjectName());
	}
}
