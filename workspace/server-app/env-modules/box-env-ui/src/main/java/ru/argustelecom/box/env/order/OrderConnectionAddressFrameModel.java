package ru.argustelecom.box.env.order;

import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.util.Objects;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.nls.OrderMessagesBundle;
import ru.argustelecom.box.env.techservice.coverage.CoverageRepository;
import ru.argustelecom.box.env.techservice.coverage.model.Coverage;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "orderConnectionAddressFM")
@PresentationModel
public class OrderConnectionAddressFrameModel implements Serializable {

	private static final long serialVersionUID = 5357601003525762451L;
	public static final String EMPTY_COVERAGE_COLOR = "red";

	@Inject
	private CoverageRepository cr;

	private Order order;
	private Location connectionAddress;
	private Coverage coverage;

	private OrderMessagesBundle orderMb;

	@PostConstruct
	protected void postConstruct() {
		orderMb = LocaleUtils.getMessages(OrderMessagesBundle.class);
	}

	public void preRender(Order order) {
		if (!Objects.equals(this.order, order)) {
			refresh(order);
		}
	}

	public boolean haveCoverage() {
		return coverage != null;
	}

	public String addressTitle() {
		return haveCoverage() ? coverage.getState().getObjectName() : orderMb.notCovered();
	}

	public String addressColor() {
		return haveCoverage() ? coverage.getState().getColor() : EMPTY_COVERAGE_COLOR;
	}

	public Callback<Order> getChangeConnectionAddressCallback() {
		return (this::refresh);
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void initCoverage() {
		Building building = null;

		if (connectionAddress instanceof Building) {
			building = (Building) connectionAddress;
		}
		if (connectionAddress instanceof Lodging) {
			building = (Building) initializeAndUnproxy(connectionAddress.getParent());
		}

		if (building != null) {
			coverage = cr.find(building);
		}
	}

	public void refresh(Order order) {
		this.order = order;
		this.connectionAddress = initializeAndUnproxy(order.getConnectionAddress());
		initCoverage();
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Order getOrder() {
		return order;
	}

	public Location getConnectionAddress() {
		return connectionAddress;
	}

	public Coverage getCoverage() {
		return coverage;
	}

}