package ru.argustelecom.box.env.order.testdata;

import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.model.LocationType;
import ru.argustelecom.box.env.address.testdata.LocationTestDataUtils;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static ru.argustelecom.box.env.party.CustomerCategory.PERSON;

public class OrderCreationProvider implements TestDataProvider {

    public static final String CUSTOMER_TYPE_PROP_NAME = "order.provider.customer.type";
    public static final String LOCATION_PROP_NAME = "order.provider.location";
    public static final String LODGING_TYPE_PROP_NAME = "order.provider.lodging.type";

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Inject
    private LocationTestDataUtils locationTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {

        CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();
        Location location = locationTestDataUtils.findOrCreateTestLocation();
        LocationType lodgingType = locationTestDataUtils.findOrCreateTestLodgingType();

        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_TYPE_PROP_NAME, customerType.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(
                LOCATION_PROP_NAME,
                // так гарантируем, что поиск по адресу сразу вернет одно единственное здание
                location.getParent().getParent().getName() + " "
                        + location.getParent().getName() + " "
                        + location.getName()
        );
        testRunContext.setBusinessPropertyWithMarshalling(LODGING_TYPE_PROP_NAME, lodgingType.getObjectName());
    }
}