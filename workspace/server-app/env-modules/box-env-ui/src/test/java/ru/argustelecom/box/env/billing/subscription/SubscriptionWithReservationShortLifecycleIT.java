package ru.argustelecom.box.env.billing.subscription;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Ignore;
import org.junit.Test;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionShortLifecycle;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.testdata.SubscriptionProvider;
import ru.argustelecom.box.env.login.testdata.BoxLoginProvider;
import ru.argustelecom.system.inf.testframework.it.ui.navigation.LocationParameterProvider;
import ru.argustelecom.system.inf.testframework.it.ui.test.AbstractWebUITest;
import ru.argustelecom.system.inf.testframework.testdata.client.DataProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.LoginProvider;
import ru.argustelecom.system.inf.testframework.testdata.client.ParamsDonator;
import ru.argustelecom.system.inf.testframework.testdata.server.TestRunContext;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SuppressWarnings("Duplicates")
@LoginProvider(providerClass = BoxLoginProvider.class)
public class SubscriptionWithReservationShortLifecycleIT extends AbstractWebUITest implements LocationParameterProvider {

    public static class SubscriptionWithReservationShortLifecycleITParamsDonator implements ParamsDonator {

        private static final long serialVersionUID = 9118266198193902754L;

        @Override
        public void donate(TestRunContext testRunContext, String methodName) {

            SubscriptionState desiredState = SubscriptionState.FORMALIZATION;
            String balance = "100";
            String productOfferingPrice = "100";
            Date validFrom = new Date();
            boolean shouldHaveTrustPeriod = false;
            boolean shouldHaveTrialPeriod = false;

            switch (methodName) {
                case "shouldMoveSubscriptionToActivationWaiting": {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.HOUR, 1);
                    validFrom = calendar.getTime();
                    break;
                }
                case "shouldActivateSubscription": {
                    balance = "100";
                    break;
                }
                case "shouldNotActivateSubscriptionWithNoMoney": {
                    balance = "0";
                    break;
                }
                case "shouldActivateSubscriptionWithTrustPeriod": {
                    shouldHaveTrustPeriod = true;
                    balance = "-100";
                    break;
                }
                case "shouldNotActivateSubscriptionWithTrialPeriod": {
                    shouldHaveTrialPeriod = true;
                    balance = "-100";
                    break;
                }
                case "shouldCloseActiveSubscription": {
                    desiredState = SubscriptionState.ACTIVE;
                    break;
                }
                case "shouldNotManuallyActivateSubscriptionInActivationWaiting": {
                    // установим время действие на час вперёд, чтобы перевести в ожидание активации
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.HOUR, 1);
                    validFrom = calendar.getTime();
                    break;
                }
                case "shouldCloseSubscriptionInActivationWaiting": {
                    desiredState = SubscriptionState.ACTIVATION_WAITING;

                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.HOUR, 1);
                    validFrom = calendar.getTime();
                    break;
                }
            }

            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_STATE_PARAM_NAME, desiredState);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_LIFECYCLE_QUALIFIER, SubscriptionLifecycleQualifier.SHORT);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_RESERVE_FUNDS_RULE, true);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_PERSONAL_ACCOUNT_BALANCE, balance);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_PRODUCT_OFFERING_PRICE, productOfferingPrice);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_VALID_FROM, validFrom);
            testRunContext.setProviderParam(SubscriptionProvider.SHOULD_HAVE_TRUST_PERIOD, shouldHaveTrustPeriod);
            testRunContext.setProviderParam(SubscriptionProvider.SHOULD_HAVE_TRIAL_PERIOD, shouldHaveTrialPeriod);
        }
    }

    @Override
    public Map<String, String> provideLocationParameters() {
        Map<String, String> params = new HashMap<>();

        Subscription subscription = getTestRunContextProperty(
                SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME, Subscription.class
        );

        params.put("subscription", new EntityConverter().convertToString(subscription));
        return params;
    }

    /**
     * Перевод в ожидание активации
     */
    @Test
    public void shouldMoveSubscriptionToActivationWaiting(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithReservationShortLifecycleITParamsDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionShortLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVATION_WAITING.getName(), page.state.getValue());
        assertTrue("Инвойсы", page.invoices.getRowCount() == 0);

        assertEquals("Переход из состояния", SubscriptionState.FORMALIZATION.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVATION_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }


    /**
     * Первичная активация подписки с резервированием и без привилегий
     */
    @Test
    public void shouldActivateSubscription(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithReservationShortLifecycleITParamsDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionShortLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVE.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.ACTIVE.getName(), page.getLastInvoiceState());

        assertEquals("Переход из состояния", SubscriptionState.FORMALIZATION.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Актвация невозможна, т.к. нет средств
     */
    @Test
    public void shouldNotActivateSubscriptionWithNoMoney(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithReservationShortLifecycleITParamsDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionShortLifecycle.Routes.ACTIVATE.getName());

        assertTrue(page.lifecycleRoutingBlock.confirm.isDisabled());
    }

    /**
     * Нет средств, но действует доверительный период
     */
    @Test
    public void shouldActivateSubscriptionWithTrustPeriod(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithReservationShortLifecycleITParamsDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionShortLifecycle.Routes.ACTIVATE.getName());
        page.lifecycleRoutingBlock.setRead();
        page.lifecycleRoutingBlock.confirmTransition();
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVE.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.ACTIVE.getName(), page.getLastInvoiceState());

        assertEquals("Переход из состояния", SubscriptionState.FORMALIZATION.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Нет средств, но дейтсвутет тестовый период, активация невозможна
     */
    @Test
    public void shouldNotActivateSubscriptionWithTrialPeriod(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithReservationShortLifecycleITParamsDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionShortLifecycle.Routes.ACTIVATE.getName());

        assertTrue(page.lifecycleRoutingBlock.confirm.isDisabled());
    }

    /**
     * Закрытие действующей подписки
     */
    @Test
    public void shouldCloseActiveSubscription(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithReservationShortLifecycleITParamsDonator.class
            ) Subscription subscription
    ) {

        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionShortLifecycle.Routes.CLOSE.getName());
        page.lifecycleRoutingBlock.setRead();
        page.lifecycleRoutingBlock.confirmTransition();
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.CLOSURE_WAITING.getName(), page.state.getValue());
        assertEquals("Инвойсы", page.getLastInvoiceState(), InvoiceState.CLOSED.getName());

        assertEquals("Переход из состояния", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.CLOSURE_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Ручная активация ожидающей активацию подписки
     */
    @Ignore("BOX-2428")
    @Test
    public void shouldNotManuallyActivateSubscriptionInActivationWaiting(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithReservationShortLifecycleITParamsDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionShortLifecycle.Routes.ACTIVATE.getName());
        page.lifecycleRoutingBlock.setRead();
        page.lifecycleRoutingBlock.confirmTransition();
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVATION_WAITING.getName(), page.state.getValue());
        assertTrue("Инвойсы", page.invoices.getRowCount() == 0);

        assertEquals("Переход из состояния", SubscriptionState.FORMALIZATION.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVATION_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Закрытие ожидающей активацию подписки
     */
    @Test
    //@formatter:off
	public void shouldCloseSubscriptionInActivationWaiting(
			@InitialPage SubscriptionCardPage page,
			@DataProvider(
					providerClass = SubscriptionProvider.class,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithReservationShortLifecycleITParamsDonator.class
			) Subscription subscription
	) {
        //@formatter:on
        page.lifecycleRoutingBlock.performTransition(SubscriptionShortLifecycle.Routes.CLOSE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.CLOSED.getName(), page.state.getValue());
        assertTrue("Инвойсы", page.invoices.getRowCount() == 0);

        assertEquals("Переход из состояния", SubscriptionState.ACTIVATION_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.CLOSED.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }
}