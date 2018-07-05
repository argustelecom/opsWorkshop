package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.model.role.Employee;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class EmployeeCreationProvider implements TestDataProvider {

    public static final String EMPLOYEE_PROP_NAME = "employee.provider.employee";

    @Inject
    private EmployeeTestDataUtils employeeTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        Employee employee = employeeTestDataUtils.createTestEmployee();
        testRunContext.setBusinessPropertyWithMarshalling(EMPLOYEE_PROP_NAME, employee);
    }
}
