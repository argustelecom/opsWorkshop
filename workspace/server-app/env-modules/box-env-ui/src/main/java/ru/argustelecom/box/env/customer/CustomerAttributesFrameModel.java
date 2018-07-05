package ru.argustelecom.box.env.customer;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.party.CurrentPartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.model.role.Individual;
import ru.argustelecom.box.env.party.model.role.Organization;
import ru.argustelecom.box.env.party.model.role.PartyRoleRepository;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class CustomerAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 8972439212250130219L;

	private static final Logger log = Logger.getLogger(CustomerAttributesFrameModel.class);

	@Inject
	private PartyRoleRepository partyRoleRepository;

	@Inject
	private CurrentPartyRole currentPartyRole;

	private Customer customer;

	private List<Employee> possibleEmployees;
	private Employee newEmployee;

	@PostConstruct
	protected void postConstruct() {
		refresh();
	}

	public boolean isIndividual() {
		return customer != null && customer instanceof Individual;
	}

	public void toggleVipSign() {
		customer.setVip(!customer.isVip());
	}

	public List<Employee> getPossibleEmployees() {
		if (possibleEmployees == null)
			possibleEmployees = partyRoleRepository.getAllEmployees().stream()
					.filter(employee -> !((Organization) customer).getEmployees().contains(employee))
					.collect(Collectors.toList());
		return possibleEmployees;
	}

	public void addEmployee() {
		partyRoleRepository.addEmployee((Organization) customer, newEmployee);
		possibleEmployees.remove(newEmployee);
		cleanAdditionEmployeeParams();
	}

	public void removeEmployee(Employee employee) {
		partyRoleRepository.removeEmployee((Organization) customer, employee);
		// possibleEmployees.add(employee);
	}

	public void cleanAdditionEmployeeParams() {
		newEmployee = null;
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

	public Employee getNewEmployee() {
		return newEmployee;
	}

	public void setNewEmployee(Employee newEmployee) {
		this.newEmployee = newEmployee;
	}

}