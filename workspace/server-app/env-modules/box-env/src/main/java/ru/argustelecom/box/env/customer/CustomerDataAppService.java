package ru.argustelecom.box.env.customer;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.contact.EmailContact;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class CustomerDataAppService implements Serializable {

	private static final long serialVersionUID = 1442409992675193406L;

	@PersistenceContext
	protected EntityManager em;

	public void editVipFlag(Long customerId, boolean vip) {
		checkArgument(customerId != null, "customerId is required");

		// TODO [Permission] Проверить права на редактирование карточки физ. клиента

		Customer customer = em.find(Customer.class, customerId);
		customer.setVip(vip);
	}

	public void changeMainEmail(Long customerId, Long emailId) {
		checkArgument(customerId != null, "customerId is required");

		Customer customer = em.find(Customer.class, customerId);

		if (emailId == null)
			customer.setMainEmail(null);
		else {
			EmailContact email = em.find(EmailContact.class, emailId);
			customer.setMainEmail(email);
		}

	}

}