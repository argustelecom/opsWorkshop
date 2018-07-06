package ru.argustelecom.box.env.contract.testdata;

import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.OwnerTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class BillTypeCreationProvider implements TestDataProvider {

    public static final String CUSTOMER_TYPE_NAME = "BillTypeCreationProvider.customerType";
    public static final String PROVIDER_NAME = "BillTypeCreationProvider.provider";

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Inject
    private OwnerTestDataUtils ownerTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();
        Owner provider = ownerTestDataUtils.findOrCreateTestOwner();

        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_TYPE_NAME, customerType.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(PROVIDER_NAME, provider.getObjectName());
    }
}