package ru.argustelecom.box.env.commodity.testdata;

import ru.argustelecom.box.env.commodity.CommodityTypeRepository;
import ru.argustelecom.box.env.commodity.model.CommodityTypeGroup;
import ru.argustelecom.box.env.commodity.model.ServiceType;
import ru.argustelecom.box.env.commodity.telephony.TelephonyOptionTypeRepository;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionType;

import javax.inject.Inject;
import java.io.Serializable;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class CommodityTypeTestDataUtils implements Serializable {

    private static final long serialVersionUID = 3417149256160580628L;

    @Inject
    private CommodityTypeRepository commodityTypeRp;

    @Inject
    private TelephonyOptionTypeRepository telephonyOptionTypeRp;

    public ServiceType findOrCreateTestServiceType() {
        return getOrElse(commodityTypeRp.findAllServiceTypes(), this::createTestServiceType);
    }

    public ServiceType createTestServiceType() {
        CommodityTypeGroup group = findOrCreateTestCommodityTypeGroup();
        return commodityTypeRp.createServiceType(
                uniqueId("Тестовый типу услуги"),
                uniqueId(),
                group,
                "Для тестирования"
        );
    }

    public TelephonyOptionType findOrCreateTestOptionType() {
        return getOrElse(telephonyOptionTypeRp.findAll(), this::createTestOptionType);
    }

    public TelephonyOptionType createTestOptionType() {
        CommodityTypeGroup group = findOrCreateTestCommodityTypeGroup();
        return telephonyOptionTypeRp.create(
                uniqueId("Тестовый тип опции"),
                uniqueId(),
                group,
                "Тестовое описание"
        );
    }

    public CommodityTypeGroup findOrCreateTestCommodityTypeGroup() {
        return getOrElse(commodityTypeRp.findGroups(), () -> commodityTypeRp.createGroup(
                uniqueId("Тестовая группа"),
                uniqueId(),
                null)
        );
    }
}