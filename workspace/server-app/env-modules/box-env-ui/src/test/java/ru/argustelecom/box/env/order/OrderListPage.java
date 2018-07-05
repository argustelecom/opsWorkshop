package ru.argustelecom.box.env.order;

import org.jboss.arquillian.graphene.page.Location;
import org.openqa.selenium.support.FindBy;
import ru.argustelecom.system.inf.page.PageInf;
import ru.argustelecom.system.inf.testframework.it.ui.comp.*;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

/**
 * Список заявок
 * Домашняя страницы -> Работа с клиентами -> Заявки
 */
@Location("views/env/order/OrderListView.xhtml")
public class OrderListPage extends PageInf {

    @FindBy(xpath = "//body")
    public OrderFilterFragment filterBlock;

    @FindByFuzzyId("order_search_result_form-create_order_button")
    private Button openCreateDialog;

    @FindByFuzzyId("order_search_result_form-possible_order_categories")
    private ListBox orderCategories;

    @FindByFuzzyId("order_creation_form-new_type_label")
    public ComboBox customerTypes;

    @FindByFuzzyId("order_creation_form-new_last_name")
    public InputText lastName;

    @FindByFuzzyId("order_creation_form-new_first_name")
    public InputText firstName;

    @FindByFuzzyId("order_creation_form-new_second_name")
    public InputText secondName;

    @FindByFuzzyId("#order_creation_form-new_building-new_building_input")
    public AutoComplete location;

    @FindByFuzzyId("order_creation_form-new_lodging_type_label")
    public ComboBox lodgingTypes;

    @FindByFuzzyId("order_creation_form-new_lodging")
    public InputText lodging;

    @FindByFuzzyId("order_creation_form-new_connection_address_comment")
    public InputText description;

    @FindByFuzzyId("order_creation_form-create_button")
    public Button create;

    public void openCreateDialog(String customerCategory) {
        openCreateDialog.click();
        orderCategories.select(customerCategory);
    }
}
