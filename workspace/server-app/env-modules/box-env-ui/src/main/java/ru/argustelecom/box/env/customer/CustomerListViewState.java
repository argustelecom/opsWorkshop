package ru.argustelecom.box.env.customer;

import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.CONTACT_TYPE;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.CONTACT_VALUE;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.CONTRACT_ID;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.FIRST_NAME;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.LAST_NAME;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.ORGANIZATION_NAME;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.PERSONAL_ACCOUNT;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.SECOND_NAME;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.TAX_PAYER_ID;
import static ru.argustelecom.box.env.customer.CustomerListViewState.CustomerFilter.TYPE;
import static ru.argustelecom.box.env.party.CustomerCategory.PERSON;

import java.io.Serializable;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.type.model.TypePropertyFilterContainer;
import ru.argustelecom.box.env.validator.Email;
import ru.argustelecom.box.env.validator.Phone;
import ru.argustelecom.box.env.validator.Skype;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
@Getter
@Setter
public class CustomerListViewState extends FilterViewState implements Serializable {

	private static final long serialVersionUID = 3835834237800070336L;

	@FilterMapEntry(value = TYPE, translator = CustomerTypeDtoTranslator.class)
	private CustomerTypeDto type;

	@FilterMapEntry(ORGANIZATION_NAME)
	private String organizationName;

	@FilterMapEntry(FIRST_NAME)
	private String firstName;

	@FilterMapEntry(SECOND_NAME)
	private String secondName;

	@FilterMapEntry(LAST_NAME)
	private String lastName;

	@FilterMapEntry(CONTACT_TYPE)
	private ContactType contactType;

	@FilterMapEntry(CONTACT_VALUE)
	private String contactValue;

	@Phone
	private String phoneNumber;

	@Email
	private String emailAddress;

	@Skype
	private String skypeLogin;

	@FilterMapEntry(PERSONAL_ACCOUNT)
	private String personalAccount;

	@FilterMapEntry(TAX_PAYER_ID)
	private Long taxPayerId;

	@FilterMapEntry(CONTRACT_ID)
	private String contractId;

	private TypePropertyFilterContainer customerPropsFilter;
	private TypePropertyFilterContainer partyPropsFilter;

	public boolean isIndividual() {
		return type != null && type.getCustomerCategory().equals(PERSON);
	}

	public void onContactTypeChanged() {
		contactValue = null;
		phoneNumber = null;
		emailAddress = null;
		skypeLogin = null;
	}

	public String getPhoneNumber() {
		return contactValue;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
		contactValue = phoneNumber;
	}

	public String getEmailAddress() {
		return contactValue;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
		contactValue = emailAddress;
	}

	public String getSkypeLogin() {
		return contactValue;
	}

	public void setSkypeLogin(String skypeLogin) {
		this.skypeLogin = skypeLogin;
		contactValue = skypeLogin;
	}

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class CustomerFilter {
		public static final String TYPE = "TYPE";
		public static final String ORGANIZATION_NAME = "ORGANIZATION_NAME";
		public static final String FIRST_NAME = "FIRST_NAME";
		public static final String SECOND_NAME = "SECOND_NAME";
		public static final String LAST_NAME = "LAST_NAME";
		public static final String CONTACT_TYPE = "CONTACT_TYPE";
		public static final String CONTACT_VALUE = "CONTACT_VALUE";
		public static final String PERSONAL_ACCOUNT = "PERSONAL_ACCOUNT";
		public static final String TAX_PAYER_ID = "TAX_PAYER_ID";
		public static final String CONTRACT_ID = "CONTRACT_ID";
	}

}