package ru.argustelecom.box.env.security;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.security.model.Role;
import ru.argustelecom.box.env.security.testdata.RoleProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertTrue;
import static ru.argustelecom.box.env.UITestUtils.convertToString;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class RoleCardIT extends AbstractWebUITest implements LocationParameterProvider {

    @Override
    public Map<String, String> provideLocationParameters() {

        Map<String, String> params = new HashMap<>();

        if (testName.getMethodName().equals("shouldDeleteRole")) {
            Role role = getTestRunContextProperty(RoleProvider.ROLE, Role.class);
            params.put("role", convertToString(role));
            return params;
        }

        return params;
    }

    @Test
    public void shouldDeleteRole(
            @InitialPage RoleCardPage roleCard,
            @Page RoleListPage roleList,
            @DataProvider(
                    contextPropertyName = RoleProvider.ROLE,
                    providerClass = RoleProvider.class
            ) Role role
    ) {
        roleCard.removeRole.click();
        assertTrue(roleList.openCreateDialog.isPresent());
    }
}
