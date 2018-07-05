package ru.argustelecom.box.env.billing.invoice.provision;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.comp.TreeNode;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;

import static com.google.common.collect.Lists.newArrayList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static ru.argustelecom.box.env.UITestUtils.uniqueId;
import static ru.argustelecom.box.env.billing.provision.ProvisionTermsDirectoryViewModel.TermsNodeType.RECURRENT;
import static ru.argustelecom.box.env.billing.provision.model.RoundingPolicy.UP;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier.FULL;
import static ru.argustelecom.box.env.stl.period.PeriodType.CUSTOM;
import static ru.argustelecom.box.env.stl.period.PeriodUnit.MONTH;
import static ru.argustelecom.box.env.billing.provision.lifecycle.RecurrentTermsLifecycle.Routes.ACTIVATE;
import static ru.argustelecom.box.env.billing.provision.model.RecurrentTermsState.ACTIVE;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class ProvisionTermsDirectoryIT extends AbstractWebUITest {

    @Test
    public void shouldCreateAndActivateProvisionTerms(@InitialPage ProvisionTermsDirectoryPage page) {
        String nameVal = uniqueId("Тестовые условия предоставления");
        String descriptionVal = "Тестовое описание";

        page.openCreationDialog.click();

        page.creationDialog.name.input(nameVal);
        page.creationDialog.description.input(descriptionVal);
        page.creationDialog.create.click();

        assertEquals(nameVal, page.name.getValue());
        assertEquals(descriptionVal, page.description.getValue());

        page.parameters.chargingPeriod.edit();
        page.parameters.periodType.select(CUSTOM.getName());
        page.parameters.periodUnit.select(MONTH.getName());
        page.parameters.amount.input("2");
        page.parameters.chargingPeriod.save();

        page.parameters.reserveFundsRule.edit();
        page.parameters.reserveFunds.check();
        page.parameters.reserveFundsRule.save();

        page.parameters.roundingPolicyRule.edit();
        page.parameters.roundingPolicy.select(UP.getName());
        page.parameters.roundingPolicyRule.save();

        page.parameters.lifecycleQualifierParameter.edit();
        page.parameters.lifecycleQualifier.select(FULL.getName());
        page.parameters.lifecycleQualifierParameter.save();

        page.lifecycleRoutingBlock.performSingleTransition(ACTIVATE.getName());

        TreeNode createdProvisionTermsNode = page.provisionTermsTree.getTreeNode(
                newArrayList(RECURRENT.getName(), ACTIVE.getName())
        );
        assertNotNull(createdProvisionTermsNode);
    }
}
