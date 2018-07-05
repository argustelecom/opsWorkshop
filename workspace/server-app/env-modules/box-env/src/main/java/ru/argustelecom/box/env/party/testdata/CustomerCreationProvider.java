package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class CustomerCreationProvider implements TestDataProvider {

    public static final String CUSTOMER_PROP_NAME = "customer.provider.customer";

    @Inject
    private PartyTestDataUtils partyTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        Customer customer = partyTestDataUtils.createTestIndividualCustomerByTestCustomerType();
        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_PROP_NAME, customer);
    }
}
