package ru.argustelecom.box.env.party.testdata;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.party.CustomerSegmentRepository;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;

public class CustomerSegmentTestDataUtils implements Serializable {
	
	@Inject
	private CustomerSegmentRepository customerSegmentRepository;

	private static final long serialVersionUID = 4735706759186541468L;
	
	/**
	 * Ищет первый попавшийся тип клиента по спецификации клиента. Если не нашел, создает, используя остальные параметры
	 * <p>
	 * 
	 * @param customerType
	 * @return
	 */
	public CustomerSegment findOrCreateTestCustomerSegment(CustomerType customerType) {
		List<CustomerSegment> customerSegments = customerSegmentRepository.findSegments(customerType);
		if (!customerSegments.isEmpty()) {
			return customerSegments.get(0);
		}
		return customerSegmentRepository.createSegment(customerType, "Тестовый сегмент", "Тестовое описание");
	}

}
