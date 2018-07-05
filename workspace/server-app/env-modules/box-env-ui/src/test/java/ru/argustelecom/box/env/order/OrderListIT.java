package ru.argustelecom.box.env.order;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Ignore;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.order.testdata.OrderCreationProvider;
import ru.argustelecom.box.env.order.testdata.OrderListFilterPresetCreationProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * ui-тесты на сценарии списка заявок
 *
 * @author m.teslenko
 */
@LoginProvider(providerClass = BoxLoginProvider.class)
public class OrderListIT extends AbstractWebUITest {

    /**
     * Сценарий id = 269173
     * <p>
     * Предварительные условия:
     * <ul>
     * <li>Провайдер должен обеспечить:
     * <p>
     * Сценарий:
     * <ol>
     * <li>Задать значения Номер.
     * <li>Задать значения Исполнитель.
     * <li>Задать значения интервала Создана с - по.
     * <li>Задать значения Срок исполнения.
     * <li>Задать значения Состояние.
     * <li>Задать значения Приоритет.
     * <li>Задать значения Тип клиента.
     * <li>Нажать кнопку "Фильтры".
     * <li>В выпадающем списке выбрать "Сохранить как".
     * <li>В диалоге сохранения ввести: Название фильтра (обязательный параметр).
     * <li>Нажать кнопку "Сохранить".
     * <li>Проверяем, что: Фильтр с указанным названием отображен в выпадаеющем списке сохранненых фильтров.
     * <li>Проверяем, что: Сохранены правильные параметры созданного фильтра.
     */
    @Test
    //@formatter:off
    public void shouldCreateListFilterPreset(
            @InitialPage OrderListPage page,
            @DataProvider(
                    contextPropertyName = OrderListFilterPresetCreationProvider.ASSIGNEE_FULL_NAME_PROP_NAME,
                    providerClass = OrderListFilterPresetCreationProvider.class
            ) String assignee
    ) {
        //@formatter:on
        String name = "Фильтр " + UUID.randomUUID().toString();
        String number = "Заявка №" + UUID.randomUUID().toString();
        String createFrom = "01.11.2017 09:30";
        String createTo = "30.11.2017 18:45";
        String dueDate = "05.12.2017 23:59";
        String state = "Оформление"; // FIXME [localization]
        String priority = "Низкий"; // FIXME [localization]
        String customerType = getTestRunContextProperty(OrderListFilterPresetCreationProvider.CUSTOMER_TYPE_PROP_NAME, String.class);

        page.filterBlock.setToFilterOrderNumber(number);
        page.filterBlock.setAssignee(assignee);
        page.filterBlock.setCreateDate(createFrom, createTo);
        page.filterBlock.setDueDate(dueDate);
        page.filterBlock.setPriority(priority);
        page.filterBlock.setState(state);
        page.filterBlock.setCustomerType(customerType);
        page.filterBlock.clickFilterButton();
        page.filterBlock.saveAs(name);
        page.filterBlock.clickFilterButton();
        page.filterBlock.select(name);

        page.filterBlock.clickFilterButton();

        assertTrue(page.filterBlock.getPresets().contains(name));
        assertTrue(page.filterBlock.getCurrentPreset().endsWith(name));

        assertEquals("Фильтр: номер", number, page.filterBlock.getOrderNumber());
        assertEquals("Фильтр: исполнитель", assignee, page.filterBlock.getAssignee());
        assertEquals("Фильтр: создана с", createFrom, page.filterBlock.getCreateDateFrom());
        assertEquals("Фильтр: создана по", createTo, page.filterBlock.getCreateDateTo());
        assertEquals("Фильтр: срок исполнения", dueDate, page.filterBlock.getDueDate());
        assertEquals("Фильтр: состояние", state, page.filterBlock.getState());
        assertEquals("Фильтр: приоритет", priority, page.filterBlock.getPriority());
        assertEquals("Фильтр: тип клиента", customerType, page.filterBlock.getCustomerType());
    }

    /**
     * Сценарий id = C100413E Создание заявки: персона
     * <p>
     * Предварительные условия: Справочник "Типы клиентов" содержит записи о типах клиентах - персонах.
     * Открыт список заявок.
     * <p>
     * Сценарий:
     * <ol>
     * <li>В блоке "Результаты поиска" нажать кнопку "Создать". Выбрать пункт "Персона".
     * <li>В диалоге выбрать: Тип клиента
     * <li>В диалоге ввести: Фамилия
     * <li>В диалоге ввести: Имя
     * <li>В диалоге ввести: Отчество
     * <li>В диалоге ввести: Адрес
     * <li>В диалоге ввести: Тип помещения
     * <li>В диалоге ввести: Помещение
     * <li>В диалоге ввести: Описание
     * <li>Нажать кнопку "Создать".
     * <li>Проверить, что открыта карточка заявки
     * <li>Проверить, что данные клиента (тип, фамилия, имя, отчество) отображены корректно
     * <p>
     * Исполнитель: [v.sysoev]
     */
    @Ignore("Тест не может найти здание, даже несмотря на то, что переиндексация производится")
    @Test
    //@formatter:off
    public void shouldCreateOrderForPerson(
            @InitialPage OrderListPage orderList,
            @Page OrderCardPage orderCard,
            @DataProvider(
                    providerClass = OrderCreationProvider.class,
                    contextPropertyName = OrderCreationProvider.CUSTOMER_TYPE_PROP_NAME
            ) String customerType
    ) {
        //@formatter:on
        String firstName = "Князев";
        String lastName = "Вениамин";
        String secondName = "Васильевич";
        String location = getTestRunContextProperty(OrderCreationProvider.LOCATION_PROP_NAME, String.class);
        String lodgingType = getTestRunContextProperty(OrderCreationProvider.LODGING_TYPE_PROP_NAME, String.class);
        String lodging = "11";
        String description = "Тестовое описание";

        orderList.openCreateDialog("Персона");
        orderList.customerTypes.select(customerType);
        orderList.firstName.input(firstName);
        orderList.lastName.input(lastName);
        orderList.secondName.input(secondName);

        orderList.location.search(location);
        //orderList.setLodgingType(lodgingType);
        //orderList.setLodging(lodging);
        orderList.description.input(description);
        orderList.create.click();

        assertEquals(firstName, orderCard.customerBlock.firstName.getValue());
        assertEquals(lastName, orderCard.customerBlock.lastName.getValue());
        assertEquals(secondName, orderCard.customerBlock.secondName.getValue());
        assertEquals(customerType, orderCard.customerBlock.organizationType.getValue());
    }
}
