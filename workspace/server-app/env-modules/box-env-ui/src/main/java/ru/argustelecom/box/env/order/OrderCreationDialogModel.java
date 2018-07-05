package ru.argustelecom.box.env.order;

import static ru.argustelecom.box.env.party.CustomerCategory.COMPANY;
import static ru.argustelecom.box.env.party.CustomerCategory.PERSON;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.address.LocationRepository;
import ru.argustelecom.box.env.address.LocationTypeRepository;
import ru.argustelecom.box.env.address.model.Building;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.model.Lodging;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.order.nls.OrderMessagesBundle;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.PartyRepository;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.model.role.PartyRoleRepository;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.login.ArgusPrincipal;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class OrderCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 5482335554987472400L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private ArgusPrincipal principal;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private OrderRepository orderRepository;

	@Inject
	private PartyRoleRepository partyRoleRepository;

	@Inject
	private PartyRepository partyRepository;

	@Inject
	private LocationRepository locationRepository;

	@Inject
	private LocationTypeRepository locationTypeRepository;

	@Inject
	private CustomerTypeRepository customerTypeRepository;

	private List<CustomerType> allCustomerTypes;
	private List<LocationType> lodgingTypes;

	@Getter
	@Setter
	private Customer customer;

	@Getter
	@Setter
	private CustomerCategory newCustomerCategory;

	@Getter
	@Setter
	private CustomerType newCustomerType;

	@Getter
	@Setter
	private String newFirstName;

	@Getter
	@Setter
	private String newSecondName;

	@Getter
	@Setter
	private String newLastName;

	@Getter
	@Setter
	private String newLegalName;

	@Getter
	@Setter
	private BusinessObjectDto<Building> newBuilding;

	@Getter
	@Setter
	private LocationType newLodgingType;

	@Getter
	@Setter
	private String newLodging;

	@Getter
	@Setter
	private String newConnectionAddressComment;

	public void onCreationDialogOpen() {
		RequestContext.getCurrentInstance().update("order_creation_form");
		RequestContext.getCurrentInstance().execute("PF('orderCreationDlgVar').show()");
	}

	public Order createOrder() {
		Location location = null;
		if (newBuilding != null && newLodgingType != null && newLodging != null)
			location = findOrCreateLodging();
		if (newBuilding != null && (newLodgingType == null || newLodging == null))
			location = newBuilding.getIdentifiable();
		Order newOrder = orderRepository.createOrder(em.find(Employee.class, principal.getWorkerId()),
				isNeedCreateCustomer() ? createCustomer() : customer, location, newConnectionAddressComment);
		cleanCreationParams();
		return newOrder;
	}

	public void cleanCreationParams() {
		customer = null;
		newCustomerCategory = null;
		newCustomerType = null;
		newFirstName = null;
		newSecondName = null;
		newLastName = null;
		newLegalName = null;
		newBuilding = null;
		newLodgingType = null;
		newLodging = null;
		newConnectionAddressComment = null;
	}

	public boolean isNeedCreateCustomer() {
		return customer == null;
	}

	public CustomerCategory[] getCustomerCategories() {
		return CustomerCategory.values();
	}

	public String getDialogHeader() {
		OrderMessagesBundle messages = LocaleUtils.getMessages(OrderMessagesBundle.class);
		if (customer != null) {
			return messages.creationFor() + " " + customer.getObjectName();
		}
		if (newCustomerCategory != null) {
			return newCustomerCategory.equals(COMPANY) ? messages.creationForCompany() : messages.creationForPerson();
		} else {
			return messages.creation();
		}
	}

	public List<CustomerType> getPossibleCustomerTypes() {
		if (allCustomerTypes == null)
			allCustomerTypes = customerTypeRepository.getAllCustomerTypes();
		return allCustomerTypes.stream().filter(type -> type.getCategory().equals(newCustomerCategory))
				.collect(Collectors.toList());
	}

	public List<LocationType> getLodgingTypes() {
		if (lodgingTypes == null)
			lodgingTypes = locationTypeRepository.findLodgingTypes();
		return lodgingTypes;
	}

	public boolean isOrderForIndividual() {
		return newCustomerCategory != null && newCustomerCategory.equals(PERSON);
	}

	public String onOrderCreated() {
		return outcomeConstructor.construct(OrderCardViewModel.VIEW_ID,
				IdentifiableOutcomeParam.of("order", createOrder()));
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private Customer createCustomer() {

		if (isOrderForIndividual())
			return partyRepository.createIndividual(null, newLastName, newFirstName, newSecondName, null, null,
					newCustomerType);
		else
			return partyRepository.createOrganization(newLegalName, null, null, newCustomerType);
	}

	private Lodging findOrCreateLodging() {
		return (newBuilding != null && newLodgingType != null && newLodging != null)
				? locationRepository.findOrCreateLodging(newBuilding.getIdentifiable(), newLodgingType, newLodging)
				: null;
	}

}