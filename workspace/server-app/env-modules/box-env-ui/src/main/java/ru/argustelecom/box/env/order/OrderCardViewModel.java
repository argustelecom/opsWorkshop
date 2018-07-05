package ru.argustelecom.box.env.order;

import static com.google.common.base.Preconditions.checkNotNull;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;

import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.party.CurrentPartyRole;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class OrderCardViewModel extends ViewModel {

	private static final long serialVersionUID = 2511447703674576163L;

	private static final Logger log = Logger.getLogger(OrderCardViewModel.class);

	public static final String VIEW_ID = "/views/env/order/OrderCardView.xhtml";

	@Inject
	private CurrentPartyRole currentPartyRole;

	@Inject
	private CurrentOrder currentOrder;

	private Order order;

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		unitOfWork.makePermaLong();
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		checkNotNull(currentOrder.getValue(), "currentOrder required");
		if (currentOrder.changed(order)) {
			order = currentOrder.getValue();
			currentPartyRole.setValue(order.getCustomer());
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