package ru.argustelecom.box.env.order.testdata;

import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.EmployeeTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class OrderListFilterPresetCreationProvider implements TestDataProvider {

    public static final String ASSIGNEE_FULL_NAME_PROP_NAME = "orderPreset.provider.assignee.fullName";
    public static final String CUSTOMER_TYPE_PROP_NAME = "orderPreset.provider.customerType";

    @Inject
    private EmployeeTestDataUtils employeeTestDataUtils;

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {

        Employee assignee = employeeTestDataUtils.createTestEmployee();
        CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();

        testRunContext.setBusinessPropertyWithMarshalling(ASSIGNEE_FULL_NAME_PROP_NAME, assignee.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_TYPE_PROP_NAME, customerType.getObjectName());
    }
}
