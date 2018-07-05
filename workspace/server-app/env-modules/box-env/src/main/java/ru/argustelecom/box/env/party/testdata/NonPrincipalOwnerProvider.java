package ru.argustelecom.box.env.party.testdata;

import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

public class NonPrincipalOwnerProvider implements TestDataProvider {

    public static final String NON_PRINCIPAL_OWNER_NAME = "NonPrincipalOwnerProvider.nonPricipal";

    @Inject
    private OwnerTestDataUtils ownerTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {
        Owner nonPrincipal = ownerTestDataUtils.findOrCreateTestOwner(false);
        testRunContext.setBusinessPropertyWithMarshalling(NON_PRINCIPAL_OWNER_NAME, nonPrincipal.getObjectName());
    }
}