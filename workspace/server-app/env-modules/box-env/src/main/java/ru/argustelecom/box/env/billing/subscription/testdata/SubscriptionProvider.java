package ru.argustelecom.box.env.billing.subscription.testdata;

import com.google.common.base.Preconditions;
import ru.argustelecom.box.env.address.model.Location;
import ru.argustelecom.box.env.address.testdata.LocationTestDataUtils;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.privilege.testdata.PrivilegeTestDataUtils;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.testframework.testdata.server.TestDataProvider;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;

import javax.inject.Inject;
import java.util.Calendar;
import java.util.Date;

import static com.google.common.base.Preconditions.checkArgument;

/**
 * Создает подписку и все необходимое ей
 * <p>
 * Подписка создается в желаемом тестом состоянии. Какое это состояние, понятно из значения соответствующего параметра в
 * контексте
 * <p>
 *
 * @author kostd
 *
 */
public class SubscriptionProvider implements TestDataProvider {

	@Inject
	private SubscriptionTestDataUtils subscriptionTestDataUtils;

	@Inject
	private LocationTestDataUtils locationTestDataUtils;

	@Inject
	private PrivilegeTestDataUtils privilegeTestDataUtils;

	public static final String CREATED_SUBSCRIPTION_PROP_NAME = "subscription.provider.subscription";
	public static final String CUSTOMER_TYPE = "subscription.provider.customer.type";
	public static final String CUSTOMER = "subscription.provider.customer";

	public static final String DESIRED_LIFECYCLE_QUALIFIER = "desiredLifecycleQualifier";
	public static final String DESIRED_STATE_PARAM_NAME = "desiredState";
	public static final String DESIRED_RESERVE_FUNDS_RULE = "desiredReserveFundsRule";
	public static final String DESIRED_PERSONAL_ACCOUNT_BALANCE = "desiredBalance";
	public static final String DESIRED_PRODUCT_OFFERING_PRICE = "desiredProductOfferingPrice";

	public static final String DESIRED_VALID_FROM = "desiredValidFrom";
	public static final String DESIRED_VALID_TO = "desiredValidTo";

	public static final String SHOULD_HAVE_TRUST_PERIOD = "shouldHaveTrustPeriod";
	public static final String SHOULD_HAVE_TRIAL_PERIOD = "shouldHaveTrialPeriod";

	@Override
	public void provide(TestRunContext testRunContext) {
		Preconditions.checkState(testRunContext != null);

		SubscriptionState desiredState
				= testRunContext.getProviderParam(DESIRED_STATE_PARAM_NAME, SubscriptionState.class);

		SubscriptionLifecycleQualifier qualifier
				= testRunContext.getProviderParam(DESIRED_LIFECYCLE_QUALIFIER, SubscriptionLifecycleQualifier.class);

		boolean reserveFunds = testRunContext.getProviderParam(DESIRED_RESERVE_FUNDS_RULE, Boolean.class);

		String balance = testRunContext.getProviderParam(DESIRED_PERSONAL_ACCOUNT_BALANCE, String.class);

		String productOfferingPrice = testRunContext.getProviderParam(DESIRED_PRODUCT_OFFERING_PRICE, String.class);
		Date validFrom = testRunContext.getProviderParam(DESIRED_VALID_FROM, Date.class);
		Date validTo = testRunContext.getProviderParam(DESIRED_VALID_TO, Date.class);

		boolean shouldHaveTrustPeriod = testRunContext.getProviderParam(SHOULD_HAVE_TRUST_PERIOD, Boolean.class);
		boolean shouldHaveTrialPeriod = testRunContext.getProviderParam(SHOULD_HAVE_TRIAL_PERIOD, Boolean.class);

		Preconditions.checkState(desiredState != null);
		Preconditions.checkState(qualifier != null);
		Preconditions.checkState(balance != null);

		Location location = locationTestDataUtils.findOrCreateTestLocation();

		Subscription subscription = subscriptionTestDataUtils.createTestSubscription(
				location, desiredState, qualifier, reserveFunds, new Money(balance), new Money(productOfferingPrice),
				validFrom, validTo
		);

		if (shouldHaveTrustPeriod) {
			privilegeTestDataUtils.createTestTrustPeriod(validFrom, plusOneMonth(validFrom), subscription);
		}
		if (shouldHaveTrialPeriod) {
			privilegeTestDataUtils.createTestTrialPeriod(validFrom, plusOneMonth(validFrom), subscription);
		}

		checkArgument(desiredState == subscription.getState());
		checkArgument(qualifier == subscription.getLifecycleQualifier());
		checkArgument(reserveFunds == subscription.getProvisionTerms().isReserveFunds());

        // необходимо для TransactionProvider
        testRunContext.addFieldsToMarshalling(Subscription.class, "personalAccount");
		testRunContext.setBusinessPropertyWithMarshalling(CREATED_SUBSCRIPTION_PROP_NAME, subscription);

		// необходимо для BillCreationProvider
		testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER_TYPE,
                subscription.getPersonalAccount().getCustomer().getTypeInstance().getType());
		testRunContext.setBusinessPropertyWithMarshalling(CUSTOMER,
                subscription.getPersonalAccount().getCustomer().getObjectName());
	}

	private Date plusOneMonth(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		calendar.add(Calendar.MONTH, 1);
		return calendar.getTime();
	}
}