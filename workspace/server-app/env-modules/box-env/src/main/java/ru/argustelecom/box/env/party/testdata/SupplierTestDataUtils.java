package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.PartyCategory;
import ru.argustelecom.box.env.party.SupplierRepository;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.box.env.party.model.role.Supplier;

import javax.inject.Inject;

import static ru.argustelecom.box.env.util.UITestUtils.getOrElse;
import static ru.argustelecom.box.env.util.UITestUtils.uniqueId;

public class SupplierTestDataUtils {

    @Inject
    private SupplierRepository supplierRp;

    @Inject
    private PartyTypeTestDataUtils partyTypeTestDataUtils;

    public Supplier findOrCreateTestSupplier() {
        return getOrElse(supplierRp.findAll(), () -> {
            PartyType partyType = partyTypeTestDataUtils.findOrCreateTestPartyType(PartyCategory.COMPANY);
            return supplierRp.create(uniqueId("Тестовый поставщик"), "Тестовый бренд", partyType.getId());
        });
    }
}