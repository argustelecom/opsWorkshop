package ru.argustelecom.box.env.commodity.testdata;

import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class CommodityGroupProvider implements TestDataProvider {

    public static final String GROUP_NAME = "CommodityGroupProvider.group";

    @Inject
    private CommodityTypeTestDataUtils commodityTypeTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        CommodityTypeGroup group = commodityTypeTestDataUtils.findOrCreateTestCommodityTypeGroup();
        testRunContext.setBusinessPropertyWithMarshalling(GROUP_NAME, group.getObjectName());
    }
}
