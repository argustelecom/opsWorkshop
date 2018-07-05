package ru.argustelecom.box.env.order;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.model.OrderPriority;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.model.role.PartyRoleRepository;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class OrderAttributesFrameModel implements Serializable {

	private static final long serialVersionUID = 6037055201561593523L;

	private static final Logger log = Logger.getLogger(OrderAttributesFrameModel.class);

	@Inject
	private PartyRoleRepository partyRoleRepository;

	@Inject
	private CurrentOrder currentOrder;

	private Order order;

	@PostConstruct
	protected void postConstruct() {
		refresh();
	}

	public OrderPriority[] getPriorities() {
		return OrderPriority.values();
	}

	public List<Employee> getEmployees() {
		List<Employee> employees = partyRoleRepository.getAllEmployees();
		employees.remove(order.getAssignee());
		return employees;
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		checkNotNull(currentOrder.getValue(), "currentOrder required");
		if (currentOrder.changed(order)) {
			order = currentOrder.getValue();
			log.debugv("postConstruct. order_id={0}", order.getId());
		}
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Order getOrder() {
		return order;
	}
}