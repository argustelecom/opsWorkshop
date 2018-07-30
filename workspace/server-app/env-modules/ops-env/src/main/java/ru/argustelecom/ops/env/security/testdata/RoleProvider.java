package ru.argustelecom.ops.env.security.testdata;

import ru.argustelecom.ops.env.security.RoleRepository;
import ru.argustelecom.ops.env.security.model.Role;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static ru.argustelecom.ops.env.util.UITestUtils.uniqueId;

public class RoleProvider implements TestDataProvider {

    public static final String ROLE = "RoleProvider.role";

    @Inject
    private RoleRepository roleRp;

    @Override
    public void provide(TestRunContext testRunContext) {
        Role role = roleRp.createRole(uniqueId("Тестовая роль"), "Тестовое описание");
        testRunContext.setBusinessPropertyWithMarshalling(ROLE, role);
    }
}