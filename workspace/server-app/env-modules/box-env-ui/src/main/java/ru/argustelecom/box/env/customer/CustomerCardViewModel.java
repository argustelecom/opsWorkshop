package ru.argustelecom.box.env.customer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectDto;
import ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectDtoTranslator;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.contract.ContractRepository;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.Contract;
import ru.argustelecom.box.env.order.OrderRepository;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.party.CurrentPartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class CustomerCardViewModel extends ViewModel {

	private static final long serialVersionUID = -6831267722750390954L;

	private static final Logger log = Logger.getLogger(CustomerCardViewModel.class);

	public static final String VIEW_ID = "/views/env/customer/CustomerCardView.xhtml";

	@Inject
	private PrivilegeSubjectDtoTranslator privilegeSubjectDtoTranslator;

	@Inject
	private ContractRepository contractRepository;

	@Inject
	private SubscriptionRepository subscriptionRepository;

	@Inject
	private OrderRepository orderRepository;

	@Inject
	private CurrentPartyRole currentPartyRole;

	private Customer customer;

	private List<Contract> contracts;
	private List<Order> orders;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	public List<Contract> getContracts() {
		if (contracts == null) {
			contracts = contractRepository.findContracts(customer);
		}
		return contracts;
	}

	public Callback<AbstractContract> getContractCallback() {
		return ((contract) -> contracts.add((Contract) contract));
	}

	public List<Order> getOrders() {
		if (orders == null) {
			orders = orderRepository.findOrders(customer);
		}
		return orders;
	}

	public PrivilegeSubjectDto getPrivilegeSubject() {
		return privilegeSubjectDtoTranslator.translate(customer);
	}

	public boolean isVipCustomer() {
		return customer.isVip();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		checkNotNull(currentPartyRole.getValue(), "currentPartyRole required");
		if (currentPartyRole.changed(customer)) {
			customer = (Customer) currentPartyRole.getValue();
			log.debugv("postConstruct. customer_id={0}", customer.getId());
		}
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Customer getCustomer() {
		return customer;
	}

}