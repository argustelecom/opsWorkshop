package ru.argustelecom.box.env.personnel;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.testdata.EmployeeCreationProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class EmployeeCardIT extends AbstractWebUITest implements LocationParameterProvider {

    @Override
    public Map<String, String> provideLocationParameters() {
        Map<String, String> params = new HashMap<>();

        Employee employee = getTestRunContextProperty(EmployeeCreationProvider.EMPLOYEE_PROP_NAME, Employee.class);
        params.put("employee", new EntityConverter().convertToString(employee));

        return params;
    }

    /**
     * Сценарий id = C125079 Создание учетной записи
     * <p>
     * Предварительные условия: Открыта карточка пользователя, которого требуется создать учетную запись.
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Учетная запись" нажать кнопку "Создать учетную запись".
     * <li>В диалоге ввести: "Логин"
     * <li>В диалоге ввести: "Email"
     * <li>В диалоге ввести: "Пароль"
     * <li>В диалоге ввести: "Подтверждение"
     * <li>В диалоге ввести: "Примечание"
     * <li>Нажать кнопку "Создать".
     * <li>Проверить, что в блоке "Учетная запись" корреткно отображены данные созданной учётной записи.
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Test
    //@formatter:off
    public void shouldCreateLogin(
            @InitialPage EmployeeCardPage page,
            // провайдер должен отработать, чтобы оказаться на странице нового сотрудника без учетной записи
            // при этом сам сотрудник тесту не нужен
            @DataProvider(
                    providerClass = EmployeeCreationProvider.class,
                    contextPropertyName = EmployeeCreationProvider.EMPLOYEE_PROP_NAME
            ) Employee employee
    ) {
        //@formatter:on
        String username = UUID.randomUUID().toString().substring(0, 15);
        String email = "username@example.com";
        String password = "username";
        String description = "Тестовое описание";

        page.openCreateLoginDialog.click();
        page.username.input(username);
        page.email.input(email);
        page.password.input(password);
        page.confirmation.input(password);
        page.description.input(description);
        page.createLogin.click();

        assertEquals(username, page.username.getValue());
        assertEquals(email, page.email.getValue());
        assertEquals(description, page.description.getValue());
    }

    /**
     * Сценарий id = C99670 Увольнение пользователя
     * <p>
     * Предварительные условия: Открыта карточка пользователя, которого требуется уволить.
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Атрибуты" нажать кнопку "Уволить пользователя".
     * <li>Подтвердить удаление.
     * <li>Название блока "Сведения о пользователе" отображены красным цветом.
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Test
    //@formatter:off
    public void shouldFireEmployee(
            @InitialPage EmployeeCardPage page,
            @DataProvider(
                    providerClass = EmployeeCreationProvider.class,
                    contextPropertyName = EmployeeCreationProvider.EMPLOYEE_PROP_NAME
            ) Employee employee
    ) {
        //@formatter:on
        page.fire();
        assertTrue(page.fired.isDisplayed());
    }
}