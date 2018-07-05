package ru.argustelecom.box.env.party.testdata;

import java.io.Serializable;
import java.util.UUID;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;

import ru.argustelecom.box.env.contact.ContactInfo;
import ru.argustelecom.box.env.contract.model.ContractCategory;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.PartyRepository;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Individual;

public class PartyTestDataUtils implements Serializable {

	private static final long serialVersionUID = -3540940381102062684L;

	@Inject
	private PartyRepository partyRepository;

	@Inject
	private CustomerTypeTestDataUtils customerTypeTestDataUtils;

	@Inject
	private OwnerTestDataUtils ownerTestDataUtils;

	@Inject
	private SupplierTestDataUtils supplierTestDataUtils;

	/**
	 * Безусловно создает нового тестового клиента Individual (см. {@link PartyRepository#createIndividual}) указанного
	 * типа с безумным именем, сгенерированным на основе UUID. Гарантирует, что либо будет создан новый клиент, либо
	 * будет брошено исключение. Никогда не вернет null
	 * 
	 * @param customerType
	 * @return
	 */
	public Customer createTestIndividualCustomer(CustomerType customerType) {
		// TODO Нужно придумать способ генерирования нормального имени для создаваемых человеков
		// FIXME [v.sysoev] убрал пробел при формировании имен, т.к. наш автокомплит не умеет корректно такие ситуации
		// обрабатывать
		String personalNumber = UUID.randomUUID().toString();
		String firstName = StringUtils.substring("Имя" + personalNumber, 0, 30);
		String secondName = StringUtils.substring("Отчество" + personalNumber, 0, 30);
		String lastName = StringUtils.substring("Фамилия" + personalNumber, 0, 30);

		return partyRepository.createIndividual("Синьор", lastName, firstName, secondName, "к.т.н.", new ContactInfo(),
				customerType);
	}

	public Customer createTestIndividualCustomerByTestCustomerType() {
		CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();
		return createTestIndividualCustomer(customerType);
	}

	public String getIndividualLastName(Customer customer) {

		if (customer instanceof Individual) {
			return ((Person) customer.getParty()).getName().lastName();
		}

		return "";
	}

	public PartyRole findOrCreateTestProviderForContract(ContractCategory contractCategory) {
		if (contractCategory == ContractCategory.BILATERAL) {
			return ownerTestDataUtils.findOrCreateTestOwner();
		} else {
			return supplierTestDataUtils.findOrCreateTestSupplier();
		}
	}
}
