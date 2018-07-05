package ru.argustelecom.box.env.commodity.testdata;

import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class ServiceTypeOptionTypeProvider implements TestDataProvider {

    public static final String SERVICE_TYPE_ID = "ServiceTypeOptionTypeProvider.service";
    public static final String OPTION_TYPE_NAME = "ServiceTypeOptionTypeProvider.option";

    @Inject
    private CommodityTypeTestDataUtils commodityTypeTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        ServiceType serviceType = commodityTypeTestDataUtils.createTestServiceType();
        TelephonyOptionType optionType = commodityTypeTestDataUtils.findOrCreateTestOptionType();

        testRunContext.setBusinessPropertyWithMarshalling(SERVICE_TYPE_ID, serviceType.getId());
        testRunContext.setBusinessPropertyWithMarshalling(OPTION_TYPE_NAME, optionType.getName());
    }
}
