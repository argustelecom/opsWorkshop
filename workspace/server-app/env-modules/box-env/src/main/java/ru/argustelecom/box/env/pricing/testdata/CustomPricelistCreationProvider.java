package ru.argustelecom.box.env.pricing.testdata;

import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.testdata.PartyTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class CustomPricelistCreationProvider implements TestDataProvider {

    public static final String CUSTOMER_FULLNAME_PROP_NAME = "custom.pricelist.provider.customer.fullName";
    public static final String CUSTOMER_TYPE_PROP_NAME = "custom.pricelist.provider.customer.type";

    @Inject
    private PartyTestDataUtils partyTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        Customer customer = partyTestDataUtils.createTestIndividualCustomerByTestCustomerType();
        testRunContext.setBusinessPropertyWithMarshalling(
                CUSTOMER_TYPE_PROP_NAME,
                customer.getTypeInstance().getType().getObjectName()
        );
        testRunContext.setBusinessPropertyWithMarshalling(
                CUSTOMER_FULLNAME_PROP_NAME,
                partyTestDataUtils.getIndividualLastName(customer)
        );
    }
}
