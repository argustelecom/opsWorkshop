package ru.argustelecom.box.env.personnel;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.login.testdata.EmployeePermissionsProvider;
import ru.argustelecom.box.env.party.model.Appointment;
import ru.argustelecom.box.env.party.testdata.AppointmentCreationProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.ParamsDonator;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.Assert.assertEquals;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class EmployeeListIT extends AbstractWebUITest {

    // Оставлено до лучших времен, до доработки параметризации LoginProvider
    public static class EmployeeListITDonator implements ParamsDonator {

        private static final long serialVersionUID = -7545538987643845096L;

        @Override
        public void donate(TestRunContext testRunContext, String methodName) {
            List<String> permissions = new ArrayList<>();

            switch (methodName) {
                case "shouldCreateEmployee": {
                    // permissions.add("System_RoleView");
                    break;
                }
            }

            testRunContext.setProviderParam(EmployeePermissionsProvider.DESIRED_PERMISSIONS, permissions);
        }
    }

    /**
     * Сценарий id = C99673 Создание пользователя
     * <p>
     * Предварительные условия: Открыта страница "Список пользователей".
     * Справочник "Должности" содержит записи о должностях.
     * Справочник "Типы контактов" содержит записи о контактах.
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Поиск пользователя" нажать кнопку "Создать пользователя".
     * <li>В диалоге ввести: "Префикс"
     * <li>В диалоге ввести: "Имя"
     * <li>В диалоге ввести: "Фамилию"
     * <li>В диалоге ввести: "Суффикс"
     * <li>В диалоге ввести: "Отчество"
     * <li>В диалоге ввести: "Должность"
     * <li>В диалоге ввести: "Табельный номер"
     * <li>В диалоге ввести: "Примечание"
     * <li>Указать контакты для каждой из доступных категорий:
     * <li>В диалоге ввести: "Тип контакта"
     * <li>В диалоге ввести: "Значение контакта"
     * <li>В диалоге ввести: "Примечание контакта"
     * <li>Нажать кнопку "Сохранить".
     * <li>Проверить, что данные созданного пользователя совпадают с введнными при создании
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Test
    //@formatter:off
    public void shouldCreateEmployee(
            @InitialPage EmployeeListPage employeeList,
            @Page EmployeeCardPage employeeCard,
            @DataProvider(
                    providerClass = AppointmentCreationProvider.class,
                    contextPropertyName = AppointmentCreationProvider.APPOINTMENT_PROP_NAME
            ) Appointment testAppointment
    ) {
        //@formatter:on
        String prefix = "Его высочество";
        String lastName = "Кочкин";
        String firstName = "Михаил";
        String secondName = "Васильевич";
        String appointment = testAppointment.getObjectName();
        String suffix = "ибн";
        String number = UUID.randomUUID().toString();
        String note = "Тестовое описание";

        employeeList.openCreateEmployeeDialog.click();
        employeeList.employeeInfo.prefix.input(prefix);
        employeeList.employeeInfo.lastName.input(lastName);
        employeeList.employeeInfo.firstName.input(firstName);
        employeeList.employeeInfo.secondName.input(secondName);
        employeeList.employeeInfo.suffix.input(suffix);
        employeeList.employeeInfo.appointments.select(appointment);
        employeeList.employeeInfo.number.input(number);
        employeeList.employeeInfo.note.input(note);
        employeeList.employeeInfo.create.click();

        assertEquals("Префикс", prefix, employeeCard.prefix.getValue());
        assertEquals("Фамилия", lastName, employeeCard.lastName.getValue());
        assertEquals("Имя", firstName, employeeCard.firstName.getValue());
        assertEquals("Отчество", secondName, employeeCard.secondName.getValue());
        assertEquals("Суффикс", suffix, employeeCard.suffix.getValue());
        assertEquals("Номер", number, employeeCard.number.getValue());
        assertEquals("Заметка", note, employeeCard.note.getValue());
        assertEquals("Должность", appointment, employeeCard.appointment.getValue());
    }
}