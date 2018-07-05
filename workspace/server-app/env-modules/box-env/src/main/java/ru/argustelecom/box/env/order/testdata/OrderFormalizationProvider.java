package ru.argustelecom.box.env.order.testdata;

import javax.inject.Inject;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.testdata.LocationTestDataUtils;
import ru.argustelecom.box.env.contact.ContactAppService;
import ru.argustelecom.box.env.contact.ContactCategory;
import ru.argustelecom.box.env.contact.ContactType;
import ru.argustelecom.box.env.contact.testdata.ContactTypeTestDataUtils;
import ru.argustelecom.box.env.order.model.Order;
import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.EmployeeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.PartyTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

/**
 * Предоставляет для тестов:
 * <li>Адрес (Location)
 * <li>Работника (Employee), ответственого за выполнение заявки.
 * <li>Клиента (Customer)
 * <li>Контакты (Contact) клиента
 * <li>Виды контактов (ContactType)
 * <li>Заявку (Order)
 * 
 * @author v.semchenko
 *
 */
public class OrderFormalizationProvider implements TestDataProvider {

	@Inject
	private CustomerTypeTestDataUtils customerTypeTestDataUtils;

	@Inject
	private LocationTestDataUtils locationTestDataUtils;

	@Inject
	private OrderTestDataUtils orderTestDataUtils;

	@Inject
	private EmployeeTestDataUtils employeeTestDataUtils;

	@Inject
	private PartyTestDataUtils partyTestDataUtils;

	@Inject
	private ContactAppService contactAppService;

	@Inject
	private ContactTypeTestDataUtils contactTypeTestDataUtils;

	public static final String ORDER_FORMALIZATION_PROP_NAME = "order.formalization.provider.order.formalization";
	private static final String TOKEN = "-127105";

	@Override
	public void provide(TestRunContext testRunContext) {
		CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();

		Customer customer = partyTestDataUtils.createTestIndividualCustomer(customerType);
		Location location = locationTestDataUtils.findOrCreateTestLocation();
		Employee assignee = employeeTestDataUtils.createTestEmployee();
		// создаем 4 типа контактов разной катигории
		ContactType customContactType = contactTypeTestDataUtils.findOrCreateContactType(
				"Тестовый тип специального контакта" + TOKEN, ContactCategory.CUSTOM, "Test-custom" + TOKEN);
		ContactType emailContactType = contactTypeTestDataUtils
				.findOrCreateContactType("Тестовый тип эл.адреса" + TOKEN, ContactCategory.EMAIL, "Test-email" + TOKEN);
		ContactType phoneContactType = contactTypeTestDataUtils.findOrCreateContactType(
				"Тестовый тип номера телефона" + TOKEN, ContactCategory.PHONE, "Test-phone" + TOKEN);
		ContactType skypeContactType = contactTypeTestDataUtils.findOrCreateContactType("Тестовый тип skype" + TOKEN,
				ContactCategory.SKYPE, "Test-skype" + TOKEN);
		// добавляем клиенту контакты
		contactAppService.addNewContact(customer.getParty().getId(), customContactType.getId(), "Тестовый контакт",
				"Тестовый контакт");
		contactAppService.addNewContact(customer.getParty().getId(), emailContactType.getId(), "test@test.st",
				"Тестовый контакт");
		contactAppService.addNewContact(customer.getParty().getId(), phoneContactType.getId(), "+78120000000",
				"Тестовый контакт");
		contactAppService.addNewContact(customer.getParty().getId(), skypeContactType.getId(), "testskype",
				"Тестовый контакт");

		Order order = orderTestDataUtils.findOrCreateTestOrder(assignee, customer, location);

		testRunContext.setBusinessPropertyWithMarshalling(ORDER_FORMALIZATION_PROP_NAME, order);
	}
}
