package ru.argustelecom.box.env.customer;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.contact.ContactCategory.EMAIL;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.billing.bill.PrefTableRepository;
import ru.argustelecom.box.env.contact.Contact;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.contact.ContactTypeRepository;
import ru.argustelecom.box.env.contact.EmailContact;
import ru.argustelecom.box.env.login.PersonalAreaLoginRepository;
import ru.argustelecom.box.env.login.model.PersonalAreaLogin;
import ru.argustelecom.box.env.message.MessageService;
import ru.argustelecom.box.env.message.mail.MailService;
import ru.argustelecom.box.env.message.mail.SendingMailException;
import ru.argustelecom.box.env.message.model.MessageTemplate;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.nls.CustomerMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "personalAreaFM")
@PresentationModel
public class PersonalAreaFrameModel implements Serializable {

	private static final long serialVersionUID = -9078305917307005539L;

	private static final String PERSONAL_AREA_URL_KEYWORD = "PersonalAreaURL";

	private static final String BRAND_NAME_KEYWORD = "BrandName";

	@Inject
	private PersonalAreaLoginRepository loginRepository;

	@Inject
	private MailService mailService;

	@Inject
	private MessageService messageService;

	@Inject
	private ContactTypeRepository ctr;

	@Inject
	private PrefTableRepository prefTableRepository;

	private Customer customer;

	private PersonalAreaLogin login;

	private String newLogin;
	private String newPassword;

	private List<ContactType> addressTypes;
	private ContactType selectedAddressType;
	private EmailContact selectedAddress;
	private String additionalInfo;

	private String emailValue;

	private CustomerMessagesBundle customerMb;

	@PostConstruct
	protected void postConstruct() {
		customerMb = LocaleUtils.getMessages(CustomerMessagesBundle.class);
	}

	public void preRender(Customer customer) {
		if (!Objects.equals(this.customer, customer)) {
			this.customer = customer;
		}
		this.login = loginRepository.findLogin(customer);
	}

	public void create() {
		checkNotNull(newLogin);
		checkNotNull(newPassword);
		checkArgument(login == null);

		loginRepository.createLogin(customer, newLogin, newPassword);
		clearCreationParams();
	}

	public void remove() {
		checkNotNull(login);

		loginRepository.removeLogin(login);
	}

	public void cancelCreate() {
		clearCreationParams();
	}

	public void send() {
		checkNotNull(login);
		checkNotNull(selectedAddress);

		try {
			String message = messageService.createMessage(MessageTemplate.PA_ACCOUNT_DATA_TEMPLATE_ID,
					createDataModel());
			String senderName = prefTableRepository.getSenderName();
			mailService.sendMail(selectedAddress.getValue().toString(), customerMb.personalAreaLoginData(), senderName,
					message, null);
		} catch (SendingMailException e) {
			throw new BusinessException("Exception during sending e-mail: ", e);
		}

		clearSendingParams();
	}

	public void cancelSend() {
		clearSendingParams();
	}

	public List<ContactType> getAddressTypes() {
		if (addressTypes == null) {
			addressTypes = ctr.findContactTypes(EMAIL);
		}
		return addressTypes;
	}

	public List<Contact<?>> getAddresses() {
		if (selectedAddressType != null) {
			return customer.getParty().getContactInfo().getContacts().stream()
					.filter(contact -> contact.getType().equals(selectedAddressType)).collect(Collectors.toList());
		}
		return customer.getParty().getContactInfo().getContacts().stream()
				.filter(contact -> addressTypes.contains(contact.getType())).collect(Collectors.toList());
	}

	public boolean haveAccount() {
		return login != null;
	}

	private void clearCreationParams() {
		newLogin = null;
		newPassword = null;
	}

	private void clearSendingParams() {
		selectedAddressType = null;
		selectedAddress = null;
		additionalInfo = null;
	}

	private Map<String, Object> createDataModel() {
		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put("party", customer.getParty());
		dataModel.put("personalAreaLogin", login);
		dataModel.put("additionalInfo", additionalInfo != null ? additionalInfo : "");
		return dataModel;
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Customer getCustomer() {
		return customer;
	}

	public PersonalAreaLogin getLogin() {
		return login;
	}

	public String getNewLogin() {
		return newLogin;
	}

	public void setNewLogin(String newLogin) {
		this.newLogin = newLogin;
	}

	public String getNewPassword() {
		return newPassword;
	}

	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}

	public ContactType getSelectedAddressType() {
		return selectedAddressType;
	}

	public void setSelectedAddressType(ContactType selectedAddressType) {
		this.selectedAddressType = selectedAddressType;
	}

	public EmailContact getSelectedAddress() {
		return selectedAddress;
	}

	public void setSelectedAddress(EmailContact selectedAddress) {
		this.selectedAddress = selectedAddress;
	}

	public String getAdditionalInfo() {
		return additionalInfo;
	}

	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}

	public String getEmailValue() {
		return emailValue;
	}

	public void setEmailValue(String emailValue) {
		this.emailValue = emailValue;
	}

}