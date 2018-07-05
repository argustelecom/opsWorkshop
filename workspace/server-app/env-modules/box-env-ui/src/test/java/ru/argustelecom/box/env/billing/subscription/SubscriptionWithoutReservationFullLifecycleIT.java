package ru.argustelecom.box.env.billing.subscription;

import org.jboss.arquillian.graphene.page.InitialPage;
import org.junit.Ignore;
import org.junit.Test;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.subscription.lifecycle.SubscriptionFullLifecycle;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.billing.subscription.testdata.SubscriptionProvider;
import ru.argustelecom.box.env.billing.transaction.testdata.TransactionProvider;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
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
public class SubscriptionWithoutReservationFullLifecycleIT extends AbstractWebUITest implements LocationParameterProvider {

    public static class SubscriptionWithoutReservationFullLifecycleITDonator implements ParamsDonator {

        private static final long serialVersionUID = -6721843632913888905L;

        @Override
        public void donate(TestRunContext testRunContext, String methodName) {

            SubscriptionState desiredState = SubscriptionState.FORMALIZATION;
            String startBalance = "0.01";
            String productOfferingPrice = "100";
            Date validFrom = new Date();
            boolean shouldHaveTrustPeriod = false;
            boolean shouldHaveTrialPeriod = false;
            String transactionAmount = "0";

            switch (methodName) {
                case "shouldMoveSubscriptionToActivationWaiting": {
                    Calendar calendar = Calendar.getInstance();
                    calendar.setTime(new Date());
                    calendar.add(Calendar.HOUR, 1);
                    validFrom = calendar.getTime();
                    break;
                }
                case "shouldActivateSubscription": {
                   // startBalance = "100";
                    break;
                }
                case "shouldMoveSubscriptionWithNoMoneyToSuspendedForDebt": {
                    startBalance = "-100";
                    break;
                }
                case "shouldActivateSubscriptionWithTrustPeriod": {
                    shouldHaveTrustPeriod = true;
                    startBalance = "-100";
                    break;
                }
                case "shouldMoveSubscriptionWithTrialPeriodToSuspendedForDebt": {
                    shouldHaveTrialPeriod = true;
                    startBalance = "-100";
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
                case "shouldMoveSubscriptionSuspendedForDebtToClosureWaiting": {
                    desiredState = SubscriptionState.SUSPENDED_FOR_DEBT;
                    break;
                }
                case "shouldMoveSubscriptionSuspendedOnDemandToClosureWaiting": {
                    desiredState = SubscriptionState.SUSPENDED_ON_DEMAND;
                    break;
                }
                case "shouldMoveActiveSubscriptionToSuspendedForDebt": {
                    desiredState = SubscriptionState.ACTIVE;
                    break;
                }
                case "shouldMoveActiveSubscriptionToSuspendedOnDemand": {
                    desiredState = SubscriptionState.ACTIVE;
                    break;
                }
                case "shouldActivateSubscriptionFromSuspendedForDebt": {
                    desiredState = SubscriptionState.SUSPENDED_FOR_DEBT;
                    break;
                }
                case "shouldNotActivateSubscriptionFromSuspendedForDebtWithNoMoney": {
                    desiredState = SubscriptionState.SUSPENDED_FOR_DEBT;
                    // проведем транзакцию, чтобы денег стало меньше нуля
                    transactionAmount = "-200";
                    break;
                }
                case "shouldActivateSubscriptionFromSuspendedForDebtWithTrustPeriod": {
                    desiredState = SubscriptionState.SUSPENDED_FOR_DEBT;
                    // проведем транзакцию, чтобы денег стало меньше нуля
                    transactionAmount = "-200";
                    shouldHaveTrustPeriod = true;
                    break;
                }
                case "shouldNotActivateSubscriptionFromSuspendedForDebtWithTrialPeriod": {
                    desiredState = SubscriptionState.SUSPENDED_FOR_DEBT;
                    // проведем транзакцию, чтобы денег стало меньше нуля
                    transactionAmount = "-200";
                    shouldHaveTrialPeriod = true;
                    break;
                }
                case "shouldActivateSubscriptionFromSuspendedOnDemand": {
                    desiredState = SubscriptionState.SUSPENDED_ON_DEMAND;
                    break;
                }
                case "shouldNotActivateSubscriptionFromSuspendedOnDemandWithNoMoney": {
                    desiredState = SubscriptionState.SUSPENDED_ON_DEMAND;
                    transactionAmount = "-200";
                    break;
                }
                case "shouldActivateSubscriptionFromSuspendedOnDemandWithTrustPeriod": {
                    desiredState = SubscriptionState.SUSPENDED_ON_DEMAND;
                    transactionAmount = "-200";
                    shouldHaveTrustPeriod = true;
                    break;
                }
                case "shouldNotActivateSubscriptionFromSuspendedOnDemandWithTrialPeriod": {
                    desiredState = SubscriptionState.SUSPENDED_ON_DEMAND;
                    transactionAmount = "-200";
                    shouldHaveTrialPeriod = true;
                    break;
                }
            }

            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_STATE_PARAM_NAME, desiredState);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_LIFECYCLE_QUALIFIER, SubscriptionLifecycleQualifier.FULL);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_RESERVE_FUNDS_RULE, false);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_PERSONAL_ACCOUNT_BALANCE, startBalance);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_PRODUCT_OFFERING_PRICE, productOfferingPrice);
            testRunContext.setProviderParam(SubscriptionProvider.DESIRED_VALID_FROM, validFrom);
            testRunContext.setProviderParam(SubscriptionProvider.SHOULD_HAVE_TRUST_PERIOD, shouldHaveTrustPeriod);
            testRunContext.setProviderParam(SubscriptionProvider.SHOULD_HAVE_TRIAL_PERIOD, shouldHaveTrialPeriod);

            // некоторые тесты дополнительно требуют создания транзакций после работы SubscriptionProvider на ЛС подписки
            testRunContext.setProviderParam(TransactionProvider.AMOUNT, transactionAmount);
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
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
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
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVE.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.ACTIVE.getName(), page.getLastInvoiceState());

        assertEquals("Переход из состояния", SubscriptionState.FORMALIZATION.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Актвация при недостатке средств ведёт в приостановку
     */
    @Test
    public void shouldMoveSubscriptionWithNoMoneyToSuspendedForDebt(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Subscription subscription
    ) {

        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.SUSPENDED_FOR_DEBT.getName(), page.state.getValue());
        assertTrue("Инвойсы", page.invoices.getRowCount() == 0);

        assertEquals("Переход из состояния", SubscriptionState.FORMALIZATION.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.SUSPENDED_FOR_DEBT.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
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
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVE.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.ACTIVE.getName(), page.getLastInvoiceState());

        assertEquals("Переход из состояния", SubscriptionState.FORMALIZATION.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Нет средств, но дейтсвутет тестовый период, ведёт в приостановку
     */
    @Test
    public void shouldMoveSubscriptionWithTrialPeriodToSuspendedForDebt(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
                    providerClass = SubscriptionProvider.class,
                    contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.SUSPENDED_FOR_DEBT.getName(), page.state.getValue());
        assertTrue("Инвойсы", page.invoices.getRowCount() == 0);

        assertEquals("Переход из состояния", SubscriptionState.FORMALIZATION.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.SUSPENDED_FOR_DEBT.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
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
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.CLOSE_FROM_ACTIVE.getName());
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
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Subscription subscription
    ) {
        page.lifecycleRoutingBlock.performSingleTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
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
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription
	) {
        //@formatter:on
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.CLOSE_BEFORE_ACTIVATION.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.CLOSED.getName(), page.state.getValue());
        assertTrue("Инвойсы", page.invoices.getRowCount() == 0);

        assertEquals("Переход из состояния", SubscriptionState.ACTIVATION_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.CLOSED.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Закрытие приостановленной за неуплату подписки
     */
    @Test
    //@formatter:off
	public void shouldMoveSubscriptionSuspendedForDebtToClosureWaiting(
			@InitialPage SubscriptionCardPage page,
			@DataProvider(
					providerClass = SubscriptionProvider.class,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription
	) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.CLOSE_FROM_SUSPENSION.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.CLOSURE_WAITING.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.CLOSED.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.SUSPENDED_FOR_DEBT.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.CLOSURE_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());

    }

    /**
     * Закрытие приостановленной по требованию подписки
     */
    @Test
    //@formatter:off
	public void shouldMoveSubscriptionSuspendedOnDemandToClosureWaiting(
			@InitialPage SubscriptionCardPage page,
			@DataProvider(
					providerClass = SubscriptionProvider.class,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription
	) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.CLOSE_FROM_SUSPENSION.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.CLOSURE_WAITING.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.CLOSED.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.SUSPENDED_ON_DEMAND.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.CLOSURE_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());

    }

    /**
     * Приостановка за неуплату действующей попдиски
     */
    @Test
    //@formatter:off
	public void shouldMoveActiveSubscriptionToSuspendedForDebt(
			@InitialPage SubscriptionCardPage page,
			@DataProvider(
					providerClass = SubscriptionProvider.class,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription
	) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.SUSPEND_FOR_DEBT.getName());
        page.lifecycleRoutingBlock.setRead();
        page.lifecycleRoutingBlock.confirmTransition();
        driver.navigate().refresh();


        assertEquals("Состояние ЖЦ ", SubscriptionState.SUSPENSION_FOR_DEBT_WAITING.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.CLOSED.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.SUSPENSION_FOR_DEBT_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Приостановка по требованию действующей попдиски
     */
    @Test
    //@formatter:off
	public void shouldMoveActiveSubscriptionToSuspendedOnDemand(
			@InitialPage SubscriptionCardPage page,
			@DataProvider(
					providerClass = SubscriptionProvider.class,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription
	) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.SUSPEND_ON_DEMAND.getName());
        page.lifecycleRoutingBlock.setRead();
        page.lifecycleRoutingBlock.confirmTransition();
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.SUSPENSION_ON_DEMAND_WAITING.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.CLOSED.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.SUSPENSION_ON_DEMAND_WAITING.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Активация приостановленной подписки при наличии средств
     */
    @Test
    //@formatter:off
	public void shouldActivateSubscriptionFromSuspendedForDebt(
			@InitialPage SubscriptionCardPage page,
			@DataProvider(
					providerClass = SubscriptionProvider.class,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription
	) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.ACTIVATE_AFTER_DEBT_SUSPENSION.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVE.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.ACTIVE.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.SUSPENDED_FOR_DEBT.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Активация приостановленной подписки при отсутствии средств
     */
    @Test
    //@formatter:off
	public void shouldNotActivateSubscriptionFromSuspendedForDebtWithNoMoney(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
					providerClass = SubscriptionProvider.class,
					///fixme test 3.20.0.4
//precedence = 0,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription,
            @DataProvider(
                    providerClass = TransactionProvider.class,
                    ///fixme test 3.20.0.4
//precedence = 1,
                    contextPropertyName = TransactionProvider.TRANSACTION,
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Transaction transaction
    ) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.ACTIVATE_AFTER_DEBT_SUSPENSION.getName());
        assertTrue(page.lifecycleRoutingBlock.confirm.isDisabled());
    }

    /**
     * Активация приостановленной подписки при отсутствии средств, но с довер периодом
     */
    @Test
    //@formatter:off
	public void shouldActivateSubscriptionFromSuspendedForDebtWithTrustPeriod(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
					providerClass = SubscriptionProvider.class,
					///fixme test 3.20.0.4
//precedence = 0,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription,
            @DataProvider(
                    providerClass = TransactionProvider.class,
                    ///fixme test 3.20.0.4
//precedence = 1,
                    contextPropertyName = TransactionProvider.TRANSACTION,
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Transaction transaction
    ) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.ACTIVATE_AFTER_DEBT_SUSPENSION.getName());
        page.lifecycleRoutingBlock.setRead();
        page.lifecycleRoutingBlock.confirmTransition();
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVE.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.ACTIVE.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.SUSPENDED_FOR_DEBT.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Активация приостановленной подписки при отсутствии средств с тестовым периодом
     */
    @Test
    //@formatter:off
	public void shouldNotActivateSubscriptionFromSuspendedForDebtWithTrialPeriod(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
					providerClass = SubscriptionProvider.class,
					///fixme test 3.20.0.4
//precedence = 0,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription,
            @DataProvider(
                    providerClass = TransactionProvider.class,
                    ///fixme test 3.20.0.4
//precedence = 1,
                    contextPropertyName = TransactionProvider.TRANSACTION,
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Transaction transaction
    ) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.ACTIVATE_AFTER_DEBT_SUSPENSION.getName());
        assertTrue(page.lifecycleRoutingBlock.confirm.isDisabled());
    }

    /**
     * Активация приостановленной подписки по требованию при наличии средств
     */
    @Test
    //@formatter:off
	public void shouldActivateSubscriptionFromSuspendedOnDemand(
			@InitialPage SubscriptionCardPage page,
			@DataProvider(
					providerClass = SubscriptionProvider.class,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription
	) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVE.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.ACTIVE.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.SUSPENDED_ON_DEMAND.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Активация приостановленной подписки по требованию при отсутствии средств
     */
    @Test
    //@formatter:off
	public void shouldNotActivateSubscriptionFromSuspendedOnDemandWithNoMoney(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
					providerClass = SubscriptionProvider.class,
					///fixme test 3.20.0.4
//precedence = 0,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription,
            @DataProvider(
                    providerClass = TransactionProvider.class,
                    ///fixme test 3.20.0.4
//precedence = 1,
                    contextPropertyName = TransactionProvider.TRANSACTION,
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Transaction transaction
    ) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.SUSPENDED_FOR_DEBT.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.CLOSED.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.SUSPENDED_ON_DEMAND.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.SUSPENDED_FOR_DEBT.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Активация приостановленной подписки при отсутствии средств, но с довер периодом
     */
    @Test
    //@formatter:off
	public void shouldActivateSubscriptionFromSuspendedOnDemandWithTrustPeriod(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
					providerClass = SubscriptionProvider.class,
					///fixme test 3.20.0.4
//precedence = 0,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription,
            @DataProvider(
                    providerClass = TransactionProvider.class,
                    ///fixme test 3.20.0.4
//precedence = 1,
                    contextPropertyName = TransactionProvider.TRANSACTION,
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Transaction transaction
    ) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.ACTIVE.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.ACTIVE.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.SUSPENDED_ON_DEMAND.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.ACTIVE.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }

    /**
     * Активация приостановленной подписки при отсутствии средств, но с довер периодом
     */
    @Test
    //@formatter:off
	public void shouldNotActivateSubscriptionFromSuspendedOnDemandWithTrialPeriod(
            @InitialPage SubscriptionCardPage page,
            @DataProvider(
					providerClass = SubscriptionProvider.class,
					///fixme test 3.20.0.4
//precedence = 0,
					contextPropertyName = SubscriptionProvider.CREATED_SUBSCRIPTION_PROP_NAME,
					donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
			) Subscription subscription,
            @DataProvider(
                    providerClass = TransactionProvider.class,
                    ///fixme test 3.20.0.4
//precedence = 1,
                    contextPropertyName = TransactionProvider.TRANSACTION,
                    donatorClass = SubscriptionWithoutReservationFullLifecycleITDonator.class
            ) Transaction transaction
    ) {
        page.lifecycleRoutingBlock.performTransition(SubscriptionFullLifecycle.Routes.ACTIVATE.getName());
        driver.navigate().refresh();

        assertEquals("Состояние ЖЦ ", SubscriptionState.SUSPENDED_FOR_DEBT.getName(), page.state.getValue());
        assertEquals("Инвойсы", InvoiceState.CLOSED.getName(), page.invoices.getRow(0).getCell(1).getTextString());

        assertEquals("Переход из состояния", SubscriptionState.SUSPENDED_ON_DEMAND.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionFromState());
        assertEquals("Переход в состояние", SubscriptionState.SUSPENDED_FOR_DEBT.getName(),
                page.lifecycleHistoryBlock.getLastLifecycleTransitionToState());
    }
}