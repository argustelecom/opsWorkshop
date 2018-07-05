package ru.argustelecom.box.env.order;

import ru.argustelecom.box.env.filter.FilterFragment;
import ru.argustelecom.system.inf.testframework.it.ui.comp.ComboBox;
import ru.argustelecom.system.inf.testframework.it.ui.comp.InputText;
import ru.argustelecom.system.inf.testframework.it.ui.location.FindByFuzzyId;

public class OrderFilterFragment extends FilterFragment {

    @FindByFuzzyId("order_search_form-number")
    private InputText orderNumber;

    @FindByFuzzyId("order_search_form-assignee_label")
    private ComboBox assignee;

    @FindByFuzzyId("order_search_form-create_from_input")
    private InputText createFrom;

    @FindByFuzzyId("order_search_form-create_to_input")
    private InputText createTo;

    @FindByFuzzyId("order_search_form-due_date_input")
    private InputText dueDate;

    @FindByFuzzyId("order_search_form-state_label")
    private ComboBox state;

    @FindByFuzzyId("order_search_form-priority_label")
    private ComboBox priority;

    @FindByFuzzyId("order_search_form-customer_type_label")
    private ComboBox customerType;

    public void setToFilterOrderNumber (String input) {
        orderNumber.input(input);
    }

    public void setAssignee(String input) {
        assignee.select(input);
    }

    public void setCreateDate(String from, String to) {
        createFrom.input(from);
        createTo.input(to);
    }

    public void setDueDate(String input) {
        dueDate.input(input);
    }

    public void setPriority(String input) {
        priority.select(input);
    }

    public void setState(String input) {
        state.select(input);
    }

    public void setCustomerType(String input) {
        customerType.select(input);
    }

    public String getOrderNumber() {
        return orderNumber.getValue();
    }

    public String getAssignee() {
        return assignee.getValue();
    }

    public String getCreateDateFrom() {
        return createFrom.getValue();
    }

    public String getCreateDateTo() {
        return createTo.getValue();
    }

    public String getDueDate() {
        return dueDate.getValue();
    }

    public String getState() {
        return state.getValue();
    }

    public String getPriority() {
        return priority.getValue();
    }

    public String getCustomerType() {
        return customerType.getValue();
    }
}
