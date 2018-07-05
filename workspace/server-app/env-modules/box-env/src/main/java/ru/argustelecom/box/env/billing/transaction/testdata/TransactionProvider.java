package ru.argustelecom.box.env.billing.transaction.testdata;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.testdata.SubscriptionProvider;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkArgument;

public class TransactionProvider implements TestDataProvider {

    public static final String AMOUNT = "transactionProvider.amount";
    public static final String TRANSACTION = "transactionProvider.transaction";

    @Inject
    private TransactionTestDataUtils transactionTestDataUtils;

    @Override
    public void provide(TestRunContext testRunContext) {

        Subscription subscription =
                (Subscription) testRunContext.getBusinessPropertyWithUnmarshalling(SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME);

        checkArgument(subscription.getPersonalAccount() != null, "Personal Account is null");

        String amount = testRunContext.getProviderParam(AMOUNT, String.class);

        Transaction transaction =
                transactionTestDataUtils.createTestTransaction(subscription.getPersonalAccount(), new Money(amount));

        testRunContext.setBusinessPropertyWithMarshalling(TRANSACTION, transaction);
    }
}
