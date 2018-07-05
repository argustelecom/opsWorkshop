package ru.argustelecom.box.env.task;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Test;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.testdata.SubscriptionProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.task.model.Task;
import ru.argustelecom.box.env.task.testdata.TaskProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.ParamsDonator;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class TaskCardIT extends AbstractWebUITest implements LocationParameterProvider {

    public static class TaskCardITDonator implements ParamsDonator {

        @Override
        public void donate(TestRunContext testRunContext, String methodName) {
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_STATE_PARAM_NAME, SubscriptionState.ACTIVE);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_LIFECYCLE_QUALIFIER, SubscriptionLifecycleQualifier.FULL);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_RESERVE_FUNDS_RULE, true);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_PERSONAL_ACCOUNT_BALANCE, "100");
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_PRODUCT_OFFERING_PRICE, "100");
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_VALID_FROM, new Date());
            testRunContext.setProviderParam(SubscriptionProvider.SHOULD_HAVE_TRUST_PERIOD, false);
            testRunContext.setProviderParam(SubscriptionProvider.SHOULD_HAVE_TRIAL_PERIOD, false);
        }
    }

    @Override
    public Map<String, String> provideLocationParameters() {
        Map<String, String> params = new HashMap<>();

        Task task = getTestRunContextProperty(TaskProvider.TASK, Task.class);
        params.put("task", new EntityConverter().convertToString(task));

        return params;
    }

    @Test
    public void shouldSetAssignee(
            @InitialPage TaskCardPage page,
            @DataProvider(
                    ///fixme test 3.20.0.4
//precedence = 0,
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = TaskCardITDonator.class
            ) Object subscription,
            @DataProvider(
                    ///fixme test 3.20.0.4
//precedence = 1,
                    providerClass = TaskProvider.class,
                    contextPropertyName = TaskProvider.ASSIGNEE_NAME
            ) String assigneeName
    ) {
        page.openAssignDialog.click();
        page.assignees.select(assigneeName);
        page.assign.click();

        assertEquals(assigneeName, page.assignee.getValue());
    }
}