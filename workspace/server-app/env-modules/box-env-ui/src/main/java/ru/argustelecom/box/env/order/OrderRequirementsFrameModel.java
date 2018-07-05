package ru.argustelecom.box.env.order;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class OrderRequirementsFrameModel implements Serializable {

	private static final long serialVersionUID = 570065721720220556L;

	private static final Logger log = Logger.getLogger(OrderRequirementsFrameModel.class);

	@Inject
	private CurrentOrder currentOrder;

	@Inject
	private OrderRepository orderRepository;

	private Order order;

	private List<CommodityType> selectedCommodityTypes = new ArrayList<>();

	@PostConstruct
	protected void postConstruct() {
		refresh();
	}

	public void addRequirements() {
		selectedCommodityTypes.forEach(order::addRequirement);
	}

	public void removeRequirement(CommodityType requirement) {
		order.removeRequirement(requirement);
	}

	public void cleanParams() {
		selectedCommodityTypes.clear();
	}

	public List<CommodityType> getPossibleCommodityTypes() {
		return orderRepository.getPossibleCommodityTypes(order);
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

	public List<CommodityType> getSelectedCommodityTypes() {
		return selectedCommodityTypes;
	}

	public void setSelectedCommodityTypes(List<CommodityType> selectedCommodityTypes) {
		this.selectedCommodityTypes = selectedCommodityTypes;
	}

}