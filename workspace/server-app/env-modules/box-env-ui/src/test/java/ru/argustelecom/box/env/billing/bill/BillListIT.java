package ru.argustelecom.box.env.billing.bill;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.jboss.arquillian.graphene.page.Page;
import org.junit.Ignore;
import org.junit.Test;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.bill.testdata.BillCreationProvider;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.testdata.SubscriptionProvider;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.ParamsDonator;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import java.util.Calendar;
import java.util.UUID;

import static java.util.Calendar.FEBRUARY;

@LoginProvider(providerClass = BoxLoginProvider.class)
public class BillListIT extends AbstractWebUITest {

    public static class BillListITDonator implements ParamsDonator {

        private static final long serialVersionUID = 6501589724861999070L;

        @Override
        public void donate(TestRunContext testRunContext, String methodName) {
            GroupingMethod groupingMethod = GroupingMethod.CONTRACT;
            PaymentCondition paymentCondition = PaymentCondition.PREPAYMENT;

            Calendar calendar = Calendar.getInstance();
            calendar.set(2018, FEBRUARY, 15, 0, 0, 0);

            switch (methodName) {
                case "shouldCreateBill": {
                    groupingMethod = GroupingMethod.CONTRACT;
                    paymentCondition = PaymentCondition.PREPAYMENT;
                    break;
                }
            }

            testRunContext.setProviderParam(BillCreationProvider.DESIRED_GROUPING_METHOD, groupingMethod);
            testRunContext.setProviderParam(BillCreationProvider.DESIRED_PAYMENT_CONDITION, paymentCondition);

            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_STATE_PARAM_NAME, SubscriptionState.ACTIVE);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_LIFECYCLE_QUALIFIER, SubscriptionLifecycleQualifier.FULL);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_RESERVE_FUNDS_RULE, true);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_PERSONAL_ACCOUNT_BALANCE, "100");
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_PRODUCT_OFFERING_PRICE, "50");
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_VALID_FROM, calendar.getTime());
            testRunContext.setProviderParam(SubscriptionProvider.SHOULD_HAVE_TRUST_PERIOD, false);
            testRunContext.setProviderParam(SubscriptionProvider.SHOULD_HAVE_TRIAL_PERIOD, false);
        }
    }

    @Test
    public void shouldOpenPage(@InitialPage BillListPage billList) {
        billList.openCreateBillDialog.click();
        billList.createOneBill.click();
        billList.cancelCreation.click();
    }

    @Ignore("In development")
    @Test
    public void shouldCreateBill(
            @InitialPage BillListPage billList,
            @Page BillCardPage billCard,
            @DataProvider(
                    ///#fixme test 3.20.0.4
//precedence = 0,
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = BillListITDonator.class
            ) Subscription subscription,
            @DataProvider(
                    ///fixme test 3.20.0.4
//precedence = 1,
                    providerClass = BillCreationProvider.class,
                    contextPropertyName = BillCreationProvider.BILL_TYPE,
                    donatorClass = BillListITDonator.class
            ) String billTypeName
    ) {
        String customerTypeName = getTestRunContextProperty(SubscriptionProvider.CUSTOMER_TYPE, CustomerType.class).getName();
        String customerName = getTestRunContextProperty(SubscriptionProvider.CUSTOMER, String.class);

        billList.openCreateBillDialog.click();

        billList.billTypes.select(billTypeName);
        billList.number.input(UUID.randomUUID().toString());
       // billList.customerTypes.select(customerTypeName);
        billList.customer.search(customerName);
        billList.billDate.setValue("02.03.2018");

        billList.createBill.click();
    }
}
