package ru.argustelecom.box.env.contract;

import static ru.argustelecom.box.env.pricing.model.PricelistState.INFORCE;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.apache.commons.lang.StringUtils;
import org.primefaces.model.DualListModel;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.LocationTypeRepository;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.contract.model.AbstractContract;
import ru.argustelecom.box.env.contract.model.AbstractContractType;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.party.CustomerRepository;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.pricing.model.ProductOffering;
import ru.argustelecom.box.inf.util.Callback;

public abstract class AbstractContractCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -374188092688672647L;

	@Inject
	protected ContractEntryRepository contractEntryRepository;

	@Inject
	protected ContractTypeRepository contractTypeRepository;

	@Inject
	protected CustomerRepository customerRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationTypeRepository locationTypeRepository;

	private boolean redirectAfterCreation;
	private boolean selectTypeByCustomerType;
	private Callback<AbstractContract<?>> contractCallback;
	private Order order;

	@Getter
	@Setter
	private Customer newCustomer;

	@Setter
	private AbstractContractType newType;

	@Getter
	@Setter
	private String newNumber;

	@Getter
	@Setter
	private BusinessObjectDto<Building> newBuilding;

	@Getter
	@Setter
	private LocationType newLodgingType;

	@Getter
	@Setter
	private String newLodging;

	private DualListModel<ProductOffering> orderOffers = new DualListModel<>();
	private List<Building> buildings;
	private List<LocationType> lodgingTypes;

	public abstract void onCreationDialogOpen();

	protected abstract AbstractContract<?> create();

	public abstract List<? extends AbstractContractType> getTypes();

	public String onCreated() {
		AbstractContract<?> newContract = create();

		if (getOrder() != null) {
			getOrder().addContract(newContract);
			newContract.setOrder(getOrder());
			getOrderOffers().getTarget().forEach(entry -> contractEntryRepository
					.createProductOfferingEntry(newContract, entry, createLocation(), null));
		}

		if (getContractCallback() != null)
			getContractCallback().execute(newContract);

		cleanCreationParams();
		return outcome(newContract);
	}

	public String outcome(AbstractContract<?> contract) {
		return StringUtils.EMPTY;
	}

	public void cleanCreationParams() {
		newCustomer = null;
		newType = null;
		newNumber = null;
		orderOffers.getTarget().clear();
	}

	public Location createLocation() {
		return newLodgingType != null && newLodging != null
				? locationRepository.findOrCreateLodging(newBuilding.getIdentifiable(), newLodgingType, newLodging)
				: newBuilding.getIdentifiable();
	}

	public List<? extends Customer> completeCustomer(String customerName) {
		return customerRepository.findCustomerBy(getNewType().getCustomerType(), customerName);
	}

	public List<Building> getBuildings() {
		if (buildings == null)
			buildings = locationRepository.findAllBuildings();
		return buildings;
	}

	public List<LocationType> getLodgingTypes() {
		if (lodgingTypes == null)
			lodgingTypes = locationTypeRepository.findLodgingTypes();
		return lodgingTypes;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public boolean isRedirectAfterCreation() {
		return redirectAfterCreation;
	}

	public void setRedirectAfterCreation(boolean redirectAfterCreation) {
		this.redirectAfterCreation = redirectAfterCreation;
	}

	public boolean isSelectTypeByCustomerType() {
		return selectTypeByCustomerType;
	}

	public void setSelectTypeByCustomerType(boolean selectTypeByCustomerType) {
		this.selectTypeByCustomerType = selectTypeByCustomerType;
	}

	public Callback<AbstractContract<?>> getContractCallback() {
		return contractCallback;
	}

	public void setContractCallback(Callback<AbstractContract<?>> contractCallback) {
		this.contractCallback = contractCallback;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		if (order != null)
			orderOffers.setSource(order.getUnmodifiableOffers().stream()
					.filter(offer -> offer.getPricelist().getState().equals(INFORCE)).collect(Collectors.toList()));
		this.order = order;
	}

	public AbstractContractType getNewType() {
		if (newType == null && getTypes() != null && getTypes().size() == 1)
			newType = getTypes().get(0);
		return newType;
	}

	public DualListModel<ProductOffering> getOrderOffers() {
		return orderOffers;
	}

	public void setOrderOffers(DualListModel<ProductOffering> orderOffers) {
		this.orderOffers = orderOffers;
	}

}