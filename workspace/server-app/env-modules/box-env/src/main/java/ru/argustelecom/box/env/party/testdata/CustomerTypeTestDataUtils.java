package ru.argustelecom.box.env.party.testdata;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.PartyCategory;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.CustomerType.CustomerTypeQuery;
import ru.argustelecom.box.env.party.model.PartyType;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;

public class CustomerTypeTestDataUtils implements Serializable {

	private static final long serialVersionUID = -3569434063290606520L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private PartyTypeTestDataUtils partyTypeTestDataUtils;

	@Inject
	private CustomerTypeRepository customerTypeRepository;

	public CustomerType createTestCustomerType(CustomerCategory customerCategory) {
		return customerTypeRepository.createCustomerType(
				"Тестовый тип клиента",
				customerCategory,
				findOrCreatePartyTypeByCustomerCategory(customerCategory),
				UUID.randomUUID().toString(),
				"Тип клиента, предназанчен для UI тестирования. Пожалуйста, не меняйте его keyword!"
		);
	}

	public CustomerType createTestCustomerType() {
		return createTestCustomerType(CustomerCategory.PERSON);
	}

	public CustomerType findOrCreateTestCustomerType(CustomerCategory customerCategory) {

		CustomerTypeQuery<CustomerType> query = new CustomerTypeQuery<>(CustomerType.class);
		List<CustomerType> customerTypes = query.and(
				query.category().equal(customerCategory)
		).getResultList(em);

		return getOrElse(customerTypes, () -> createTestCustomerType(customerCategory));
	}

    public CustomerType findOrCreateTestCustomerType() {
	    return findOrCreateTestCustomerType(CustomerCategory.PERSON);
    }

    private PartyType findOrCreatePartyTypeByCustomerCategory(CustomerCategory customerCategory) {
		if (customerCategory == CustomerCategory.PERSON) {
			return partyTypeTestDataUtils.findOrCreateTestPartyType(PartyCategory.PERSON);
		} else {
			return partyTypeTestDataUtils.findOrCreateTestPartyType(PartyCategory.COMPANY);
		}
	}
}
