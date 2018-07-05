package ru.argustelecom.box.env.billing.bill.testdata;

import com.google.common.collect.Lists;
import ru.argustelecom.box.env.billing.bill.BillTypeRepository;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.bill.model.SummaryBillAnalyticType;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.testdata.SubscriptionProvider;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.party.model.CustomerType;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.party.testdata.OwnerTestDataUtils;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.util.List;

public class BillCreationProvider implements TestDataProvider {

    public static final String BILL_TYPE = "bill.creation.provider.bill.type";
    public static final String BILL_GROUPING_SUBJECT = "bill.creation.provider.bill.subject";

    public static final String DESIRED_GROUPING_METHOD = "bill.creation.provider.desired.grouping.method";
    public static final String DESIRED_PAYMENT_CONDITION = "bill.creation.provider.desired.payment.condition";

    @Inject
    private BillTypeRepository billTypeRp;

    @Inject
    private OwnerTestDataUtils ownerTestDataUtils;

    @PersistenceContext
    private EntityManager em;

    @Override
    public void provide(TestRunContext testRunContext) {
        Subscription subscription =
                (Subscription) testRunContext.getBusinessPropertyWithUnmarshalling(SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME);
        CustomerType customerType =
                (CustomerType) testRunContext.getBusinessPropertyWithUnmarshalling(SubscriptionProvider.CUSTOMER_TYPE);

        GroupingMethod groupingMethod
                = testRunContext.getProviderParam(DESIRED_GROUPING_METHOD, GroupingMethod.class);
        PaymentCondition paymentCondition
                = testRunContext.getProviderParam(DESIRED_PAYMENT_CONDITION, PaymentCondition.class);

        BillType billType = findOrCreateTestBillType(customerType, groupingMethod, paymentCondition);

        testRunContext.setBusinessPropertyWithMarshalling(BILL_TYPE, billType.getObjectName());

        if (groupingMethod == GroupingMethod.PERSONAL_ACCOUNT) {
            testRunContext.setBusinessPropertyWithMarshalling(BILL_GROUPING_SUBJECT, subscription.getPersonalAccount());
        }
    }

    public BillType findOrCreateTestBillType(
            CustomerType customerType, GroupingMethod groupingMethod, PaymentCondition paymentCondition
    ) {

       /* BillType.BillTypeQuery<BillType> query = new BillType.BillTypeQuery<>(BillType.class);
        query.and(
                query.groupingMethod().equal(groupingMethod),
                query.paymentCondition().equal(paymentCondition),
                query.customerType().equal(customerType)
        );

        List<BillType> availableBillTypes = query.getResultList(em);
        if (!availableBillTypes.isEmpty()) {
            return availableBillTypes.get(0);
        }*/
        Long summaryBillAnalyticTypeId = 1L;
        SummaryBillAnalyticType summaryToPay = em.find(SummaryBillAnalyticType.class, summaryBillAnalyticTypeId);

        Owner owner = ownerTestDataUtils.findOrCreateTestOwner();

        return billTypeRp.create(
                "Тестовый тип счёта",
                customerType,
                BillPeriodType.CUSTOM,
                PeriodUnit.DAY,
                groupingMethod,
                paymentCondition,
                null,
                "Тестовое описание",
                Lists.newArrayList(owner)
        );
    }
}