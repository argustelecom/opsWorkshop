package ru.argustelecom.box.env.customer;

import static ru.argustelecom.box.env.party.CustomerCategory.COMPANY;
import static ru.argustelecom.box.env.party.CustomerCategory.PERSON;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

import javax.inject.Inject;

import org.primefaces.context.RequestContext;

import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.CustomerTypeRepository;
import ru.argustelecom.box.env.party.PartyRepository;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.nls.CustomerMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.page.outcome.OutcomeConstructor;
import ru.argustelecom.box.inf.page.outcome.param.IdentifiableOutcomeParam;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class CustomerCreationDialogModel implements Serializable {

	private static final long serialVersionUID = -5390308860072437190L;

	@Inject
	private OutcomeConstructor outcomeConstructor;

	@Inject
	private PartyRepository partyRepository;

	@Inject
	private CustomerTypeRepository customerTypeRepository;

	private List<CustomerType> allCustomerTypes;

	private CustomerCategory newCustomerCategory;
	private CustomerType newCustomerType;
	private String newFirstName;
	private String newSecondName;
	private String newLastName;
	private String newLegalName;
	private String newBrandName;

	public void onCreationDialogOpen() {
		RequestContext.getCurrentInstance().execute("PF('customerCreationPanelVar').hide()");
		RequestContext.getCurrentInstance().update("customer_creation_form");
		RequestContext.getCurrentInstance().execute("PF('customerCreationDlgVar').show()");
	}

	public Customer createCustomer() {
		Customer newCustomer;
		if (isIndividualCreation())
			newCustomer = partyRepository.createIndividual(null, newLastName, newFirstName, newSecondName, null, null,
					newCustomerType);
		else
			newCustomer = partyRepository.createOrganization(newLegalName, newBrandName, null, newCustomerType);
		cleanCreationParams();
		return newCustomer;
	}

	public void cleanCreationParams() {
		newCustomerCategory = null;
		newCustomerType = null;
		newFirstName = null;
		newSecondName = null;
		newLastName = null;
		newLegalName = null;
		newBrandName = null;
	}

	public CustomerCategory[] getCustomerCategories() {
		return CustomerCategory.values();
	}

	public String getDialogHeader() {
		CustomerMessagesBundle messages = LocaleUtils.getMessages(CustomerMessagesBundle.class);
		if (newCustomerCategory != null) {
			return newCustomerCategory.equals(COMPANY) ? messages.companyCreation() : messages.personCreation();
		} else {
			return messages.customerCreation();
		}
	}

	public List<CustomerType> getPossibleCustomerTypes() {
		if (allCustomerTypes == null)
			allCustomerTypes = customerTypeRepository.getAllCustomerTypes();
		return allCustomerTypes.stream().filter(type -> type.getCategory().equals(newCustomerCategory))
				.collect(Collectors.toList());
	}

	public boolean isIndividualCreation() {
		return newCustomerCategory != null && newCustomerCategory.equals(PERSON);
	}

	public String onCustomerCreated() {
		return outcomeConstructor.construct(CustomerCardViewModel.VIEW_ID,
				IdentifiableOutcomeParam.of("customer", createCustomer()));
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public CustomerCategory getNewCustomerCategory() {
		return newCustomerCategory;
	}

	public void setNewCustomerCategory(CustomerCategory newCustomerCategory) {
		this.newCustomerCategory = newCustomerCategory;
	}

	public CustomerType getNewCustomerType() {
		return newCustomerType;
	}

	public void setNewCustomerType(CustomerType newCustomerType) {
		this.newCustomerType = newCustomerType;
	}

	public String getNewFirstName() {
		return newFirstName;
	}

	public void setNewFirstName(String newFirstName) {
		this.newFirstName = newFirstName;
	}

	public String getNewSecondName() {
		return newSecondName;
	}

	public void setNewSecondName(String newSecondName) {
		this.newSecondName = newSecondName;
	}

	public String getNewLastName() {
		return newLastName;
	}

	public void setNewLastName(String newLastName) {
		this.newLastName = newLastName;
	}

	public String getNewLegalName() {
		return newLegalName;
	}

	public void setNewLegalName(String newLegalName) {
		this.newLegalName = newLegalName;
	}

	public String getNewBrandName() {
		return newBrandName;
	}

	public void setNewBrandName(String newBrandName) {
		this.newBrandName = newBrandName;
	}

}