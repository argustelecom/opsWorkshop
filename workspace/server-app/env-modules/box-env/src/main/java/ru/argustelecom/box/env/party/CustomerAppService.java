package ru.argustelecom.box.env.party;

import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class CustomerAppService {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private CustomerRepository customerRepository;

	public List<? extends Customer> findCustomerBy(Long id, String customerName) {
		return customerRepository.findCustomerBy(em.getReference(CustomerType.class, id), customerName);
	}
}
