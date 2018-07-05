package ru.argustelecom.box.env.login.testdata;

import java.util.Locale;
import java.util.UUID;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.login.LoginService;
import ru.argustelecom.box.env.login.model.Login;
import ru.argustelecom.box.env.party.model.Party;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.testdata.EmployeeTestDataUtils;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.system.inf.chrono.TZ;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

/**
 * Предоставляет новый логин и работника с ролями
 * 
 * @see Login
 * @see Employee
 * @see Role
 * @author v.astapkovich (TASK-88556)
 */
public class BoxLoginProvider implements TestDataProvider {

	public static final String CREATED_EMPLOYEE_PROP_NAME = "login.provider.employee";
	public static final String EMPLOYEE_NAME = "login.provider.employee.name";

	@Inject
	private EmployeeTestDataUtils employeeTestDataUtils;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private LoginService loginService;

	@PersistenceContext
	private EntityManager em;

	@Override
	public void provide(TestRunContext testRunContext) {
		Employee employee = employeeTestDataUtils.createTestEmployee();
		addRoleForEmployee(employee);

		String loginPassword = UUID.randomUUID().toString().substring(0, 16);
		Login login = createLoginForEmployee(employee, loginPassword);

		// Сохраняем в контекст
		testRunContext.setBusinessPropertyWithMarshalling(CREATED_LOGIN_PROP_NAME, login.getLoginName());
		testRunContext.setBusinessPropertyWithMarshalling(CREATED_PASS_PROP_NAME, loginPassword);

		testRunContext.addFieldsToMarshalling(PartyRole.class, "party");
		testRunContext.setBusinessPropertyWithMarshalling(CREATED_EMPLOYEE_PROP_NAME, employee);
		testRunContext.setBusinessPropertyWithMarshalling(EMPLOYEE_NAME, employee.getObjectName());
	}

	/**
	 * Создать для Employee Учётную Запись с автогенерируемыми логином и паролем.
	 * 
	 * @param employee
	 *            Employee, для которого требуется создание УЗ
	 */
	private Login createLoginForEmployee(Employee employee, String password) {
		Login login = new Login(idSequence.nextValue(Login.class));
		login.setLocale(Locale.forLanguageTag("ru-RU"));
		login.setTimeZone(TZ.getServerTimeZone().getID());
		login.setEmployee(employee);
		// пока для простоты в имя логина запихнем прям пароль. Кто знает, тот воспользуется.
		// #TODO: может быть потенциальным отверстием в безопасности, если этот код когданить сработает на продуктивах.
		String loginName = "ZZ_AUTOTESTS-" + password;
		login.setUsername(loginName);
		loginService.createLogin(login, password);
		return login;
	}

	/**
	 * Добавить роль для Employee
	 * <p>
	 * На текущий момент решено добавлять пользователю все имеющиеся права, т.е. Permissions. Они объединены в роль.
	 * Поэтому добавляем для Employee системную роль - Суперпользователь
	 * 
	 * @param employee
	 *            Employee, для которого требуется создание УЗ
	 */
	private void addRoleForEmployee(Employee employee) {
		Role role = new Role(1L);
		employee.addRole(role);
		em.persist(employee);
	}
}
