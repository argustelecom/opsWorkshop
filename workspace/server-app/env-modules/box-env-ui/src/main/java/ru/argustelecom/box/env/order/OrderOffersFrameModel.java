package ru.argustelecom.box.env.order;

import static com.google.common.base.Preconditions.checkNotNull;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.commodity.model.CommodityType;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.pricing.ProductOfferingRepository;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.env.product.ProductTypeRepository;
import ru.argustelecom.box.env.product.model.AbstractProductType;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class OrderOffersFrameModel implements Serializable {

	private static final long serialVersionUID = -9050057769217531293L;

	private static final Logger log = Logger.getLogger(OrderOffersFrameModel.class);

	@Inject
	private OrderRepository orderRepository;

	@Inject
	private ProductTypeRepository productTypeRepository;

	@Inject
	private ProductOfferingRepository productOfferingRepo;

	@Inject
	private CurrentOrder currentOrder;

	private Order order;

	private List<CommodityType> selectedRequirements = new ArrayList<>();
	private List<ProductOffering> offers = new ArrayList<>();
	private List<ProductOffering> selectedOffers = new ArrayList<>();

	@PostConstruct
	protected void postConstruct() {
		refresh();
	}

	public void onSelectOfferDialogOpen() {
		RequestContext.getCurrentInstance().update("order_possible_offers_form");
		RequestContext.getCurrentInstance().execute("PF('orderSelectOffersDlgVar').show()");
	}

	public void toggleFilters(CommodityType requirement) {
		if (selectedRequirements.contains(requirement))
			selectedRequirements.remove(requirement);
		else
			selectedRequirements.add(requirement);
		applyFilters();
	}

	public void toggleOffers(ProductOffering offer) {
		if (selectedOffers.contains(offer))
			selectedOffers.remove(offer);
		else
			selectedOffers.add(offer);
	}

	public void addOffers() {
		selectedOffers.forEach(order::addOffer);
		cleanParams();
	}

	public void removeOffer(ProductOffering pricelistEntry) {
		order.removeOffer(pricelistEntry);
	}

	public void cleanParams() {
		selectedRequirements = new ArrayList<>();
		offers = new ArrayList<>();
		selectedOffers = new ArrayList<>();
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

	private void applyFilters() {
		offers = new ArrayList<>();
		List<AbstractProductType> possibleProductTypes = productTypeRepository
				.findProductTypeWithCommodityTypes(selectedRequirements);
		if (!possibleProductTypes.isEmpty())
			offers = productOfferingRepo.findProductOfferingsByProductTypes(possibleProductTypes).stream()
					.filter(pricelistEntry -> pricelistEntry.getPricelist().isSuitableForCustomer(order.getCustomer()))
					.collect(Collectors.toList());
		order.getUnmodifiableOffers().forEach(offers::remove);
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Order getOrder() {
		return order;
	}

	public List<ProductOffering> getOffers() {
		return offers;
	}

}