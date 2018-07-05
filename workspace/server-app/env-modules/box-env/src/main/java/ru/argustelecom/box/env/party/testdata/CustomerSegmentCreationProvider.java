package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class CustomerSegmentCreationProvider implements TestDataProvider {

    public static final String CUSTOMER_TYPE_NAME = "CustomerSegmentCreationProvider.customerType";

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        CustomerType customerType = customerTypeTestDataUtils.createTestCustomerType();
        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_TYPE_NAME, customerType.getName());
    }
}
