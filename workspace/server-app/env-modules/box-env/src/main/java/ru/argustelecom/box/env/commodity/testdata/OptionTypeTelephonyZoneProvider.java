package ru.argustelecom.box.env.commodity.testdata;

import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;
import ru.argustelecom.box.env.telephony.tariff.TelephonyZoneRepository;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class OptionTypeTelephonyZoneProvider implements TestDataProvider {

    public static final String ZONE_NAME = "OptionTypeTelephonyZoneProvider.zone";
    public static final String OPTION_TYPE_ID = "OptionTypeTelephonyZoneProvider.option";

    @Inject
    private CommodityTypeTestDataUtils commodityTypeTestDataUtils;

    @Inject
    private TelephonyZoneRepository telephonyZoneRp;

    @Override
    public void provide(TestRunContext testRunContext) {
        TelephonyOptionType optionType = commodityTypeTestDataUtils.findOrCreateTestOptionType();

        TelephonyZone zone = getOrElse(telephonyZoneRp.findAll(),
                () -> telephonyZoneRp.create(uniqueId("Тестовая зона"), "Тестовое описание")
        );

        testRunContext.setBusinessPropertyWithMarshalling(OPTION_TYPE_ID, optionType.getId());
        testRunContext.setBusinessPropertyWithMarshalling(ZONE_NAME, zone.getObjectName());
    }
}
