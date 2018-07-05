package ru.argustelecom.box.env.pricing.testdata;

import ru.argustelecom.box.env.party.CustomerCategory;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.testdata.CustomerSegmentTestDataUtils;
import ru.argustelecom.box.env.party.testdata.CustomerTypeTestDataUtils;
import ru.argustelecom.box.env.party.testdata.OwnerTestDataUtils;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

/**
 * Провайдер создает один сегмент {@link CustomerSegment} клиента для {@link CustomerCategory} - персона
 */
public class CommonPricelistCreationProvider implements TestDataProvider {

    public static final String CUSTOMER_SEGMENT_PROP_NAME = "common.pricelist.provider.segment";
    public static final String OWNER_NAME = "common.pricelist.provider.owner";

    @Inject
    private CustomerTypeTestDataUtils customerTypeTestDataUtils;

    @Inject
    private CustomerSegmentTestDataUtils customerSegmentTestDataUtils;

    @Inject
    private OwnerTestDataUtils ownerTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {

        // для прайс-листа не важно какой конкретно будет сегмент и для какого типа клиента он был создан
        // поэтому всегда создаем сегмент для для CustomerCategory.PERSON
        CustomerType customerType = customerTypeTestDataUtils.findOrCreateTestCustomerType();

        CustomerSegment customerSegment = customerSegmentTestDataUtils.findOrCreateTestCustomerSegment(customerType);

        Owner owner = ownerTestDataUtils.findOrCreateTestOwner();
        // Создаем второго фиктивного владельца, чтобы быть увереным,
        // что поле "Компания" отрендерится в диалоге создания прайса
        ownerTestDataUtils.createTestOwner(false);

        testRunContext.setBusinessPropertyWithMarshalling(OWNER_NAME, owner.getObjectName());
        testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_SEGMENT_PROP_NAME, customerSegment);
    }
}