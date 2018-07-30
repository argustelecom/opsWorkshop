package ru.argustelecom.box.env.party;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.validation.constraints.NotNull;

import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.CustomerType.CustomerTypeQuery;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.type.TypeFactory;
import ru.argustelecom.box.env.type.model.TypePropertyFilterContainer;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;

@Repository
public class CustomerTypeRepository implements Serializable {

	private static final long serialVersionUID = -8248219787526651978L;

	private static final String ALL_CUSTOMER_TYPES = "CustomerTypeRepository.getAllCustomerTypes";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TypeFactory typeFactory;

	public CustomerType createCustomerType(@NotNull String name, @NotNull CustomerCategory category,
			@NotNull PartyType partyType, String keyword, String description) {
		CustomerType newCustomerType = typeFactory.createType(CustomerType.class);
		newCustomerType.setName(name);
		newCustomerType.setCategory(category);
		newCustomerType.setPartyType(partyType);
		newCustomerType.setKeyword(keyword);
		newCustomerType.setDescription(description);
		em.persist(newCustomerType);
		return newCustomerType;
	}

	@NamedQuery(name = ALL_CUSTOMER_TYPES, query = "from CustomerType")
	public List<CustomerType> getAllCustomerTypes() {
		return new CustomerTypeQuery<>(CustomerType.class).getResultList(em);
	}

	public TypePropertyFilterContainer createCustomerTypePropertyFilters() {
		return typeFactory.createFilterContainer(getAllCustomerTypes());
	}

}