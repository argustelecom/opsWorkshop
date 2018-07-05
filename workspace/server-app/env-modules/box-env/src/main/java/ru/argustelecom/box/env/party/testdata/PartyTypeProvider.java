package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.PartyCategory;
import ru.argustelecom.box.env.party.model.PartyType;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class PartyTypeProvider implements TestDataProvider {

    public static final String PARTY_TYPE_NAME = "party.type.provider.party.type";

    @Inject
    private PartyTypeTestDataUtils partyTypeTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        PartyType partyType = partyTypeTestDataUtils.findOrCreateTestPartyType(PartyCategory.COMPANY);
        testRunContext.setBusinessPropertyWithMarshalling(PARTY_TYPE_NAME, partyType.getName());
    }
}
