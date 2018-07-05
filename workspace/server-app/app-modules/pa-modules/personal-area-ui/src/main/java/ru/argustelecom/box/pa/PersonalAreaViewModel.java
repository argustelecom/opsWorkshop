package ru.argustelecom.box.pa;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.Logger;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.BillReportAppService;
import ru.argustelecom.box.env.billing.bill.BillReportException;
import ru.argustelecom.box.env.billing.bill.BillRepository;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.login.PersonalAreaLoginRepository;
import ru.argustelecom.box.env.login.model.PersonalAreaLogin;
import ru.argustelecom.box.env.party.model.Person;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Individual;
import ru.argustelecom.box.env.party.model.role.Organization;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.SystemException;
import ru.argustelecom.system.inf.page.ViewModel;

@Named(value = "personalAreaVM")
public class PersonalAreaViewModel extends ViewModel {

	private static final long serialVersionUID = -9105909415948929634L;

	private static final Logger log = Logger.getLogger(PersonalAreaViewModel.class);

	private static final String INDIVIDUAL_WELCOME_TEMPLATE = "Здравствуйте, %s!";
	private static final String ORGANIZATION_WELCOME_TEMPLATE = "Добро пожаловать в личный кабинет %s!";

	@Inject
	private BillReportAppService billReportAs;

	@Inject
	private BillRepository billRepository;

	@Inject
	private PersonalAreaLoginRepository loginRepo;

	private Customer customer;

	private String welcomeText;
	private List<PersonalAccount> accounts;
	private PersonalAccount selectedAccount;
	private Map<Location, List<Subscription>> subscriptionMap = new HashMap<>();

	@Override
	@PostConstruct
	protected void postConstruct() {
		super.postConstruct();
		refresh();
		initAccounts();
		initSelectedAccount();
		unitOfWork.makePermaLong();
	}

	public void toggleAccount(PersonalAccount account) {
		selectedAccount = account;
		initSubscriptionMap();
	}

	public boolean showAccountList() {
		return customer.getActivePersonalAccounts().size() > 1;
	}

	public String getWelcomeText() {
		ResourceBundle personalAreaBundle = LocaleUtils.getBundle("PersonalAreaBundle", getClass());

		if (customer instanceof Individual) {
			Person person = (Person) customer.getParty();
			welcomeText = personalAreaBundle.getString("box.pa.greeting.person") + person.getName().officialAppeal();
		}
		if (customer instanceof Organization)
			welcomeText = personalAreaBundle.getString("box.pa.greeting.company") + customer.getObjectName();
		return welcomeText;
	}

	public StreamedContent export() throws SQLException, IOException {
		Bill bill = billRepository.findLastBillByCustomer(customer);
		if (bill == null) {
			Notification.warn("Экспорт невозможен", "Не найдено ни одного счета");
			return null;
		}
		try {
			InputStream billIs = billReportAs.generateReport(bill.getId());
			String fileName = String.format("%s_%s.pdf", bill.getDocumentNumber(), bill.getDocumentDate());
			return new DefaultStreamedContent(billIs, "application/pdf", fileName);
		} catch (BillReportException e) {
			throw new SystemException("Ошибка генерации счета ", e);
		}
	}

	public boolean canExportBill() {
		return initializeAndUnproxy(selectedAccount.getCustomer()) instanceof Individual;
	}

	// *****************************************************************************************************************
	// Private methods
	// *****************************************************************************************************************

	private void refresh() {
		if (customer == null) {
			PersonalAreaLogin currentLogin = loginRepo.currentLogin();
			checkState(currentLogin != null);
			checkState(currentLogin.getCustomer() != null);

			customer = currentLogin.getCustomer();
			log.debugv("postConstruct. customer_id={0}", customer.getId());
		}
	}

	private void initAccounts() {
		accounts = customer.getActivePersonalAccounts();
		Collections.sort(accounts,
				(PersonalAccount a1, PersonalAccount a2) -> a1.getNumber().compareTo(a2.getNumber()));
	}

	private void initSelectedAccount() {
		if (!customer.getActivePersonalAccounts().isEmpty()) {
			selectedAccount = customer.getActivePersonalAccounts().get(0);
			initSubscriptionMap();
		}
	}

	private void initSubscriptionMap() {
		subscriptionMap.clear();
		for (Subscription subscription : selectedAccount.getActiveSubscriptions()) {
			for (Location location : subscription.getLocations()) {
				if (subscriptionMap.containsKey(location))
					subscriptionMap.get(location).add(subscription);
				else
					subscriptionMap.put(location, Lists.newArrayList(subscription));
			}
		}
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public Customer getCustomer() {
		return customer;
	}

	public List<PersonalAccount> getAccounts() {
		return accounts;
	}

	public PersonalAccount getSelectedAccount() {
		return selectedAccount;
	}

	public void setSelectedAccount(PersonalAccount selectedAccount) {
		this.selectedAccount = selectedAccount;
	}

	public Map<Location, List<Subscription>> getSubscriptionMap() {
		return subscriptionMap;
	}

}