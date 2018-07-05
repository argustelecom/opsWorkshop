package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class CompanyCustomerTypeProvider implements TestDataProvider {

    public static final String CUSTOMER_TYPE_NAME = "CompanyCustomerTypeProvider.customerType";

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType(CustomerCategory.COMPANY);
        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_TYPE_NAME, customerType.getName());
    }
}