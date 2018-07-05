package ru.argustelecom.box.env.billing.account;

import lombok.val;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.provision.model.RecurrentTerms;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanPeriod;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.stl.Money;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.math.BigDecimal;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import static ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution.*;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier.FULL;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier.SHORT;

@RunWith(MockitoJUnitRunner.class)
public class PersonalAccountBalanceServiceTest {

    @Mock
    private EntityManager em;
    @Mock
    private Query query;
    @InjectMocks
    private PersonalAccountBalanceService service;
    @Mock
    private Subscription subscription;
    @Mock
    private RecurrentTerms terms;
    @Mock
    private InvoicePlan plan;
    @Mock
    private InvoicePlanPeriod invoicePlanPeriod;
    @Mock
    private InvoicePlanModifier modifier;
    @Mock
    private PersonalAccount personalAccount;

    @Test
    public void positiveTestReserveFundsOff() {
        // 1. Денег достаточно с нулевым порогом
        initInvoiceAndPersonalAccount("100.00", "0.00", "10.00", false, false, FULL);
        assertBalanceCheckingResult("100.00", "0.00", "0.00", ALLOWED, false, false);

        // 2. Увеличиваем стоимость инвойс-плана, теперь он больше доступных средств,
        // за счет отсутсвия резервирования денег хватит
        initInvoiceAndPersonalAccount("100.00", "0.00", "150.00", false, false, FULL);
        assertBalanceCheckingResult("100.00", "0.00", "0.00", ALLOWED, false, false);

        // 3. Увеличим порог
        initInvoiceAndPersonalAccount("100.00", "100.00", "150.00", false, false, FULL);
        assertBalanceCheckingResult("100.00", "100.00", "0.00", ALLOWED, false, false);

        // 4. Порог отрицательный, поэтому с отрицательным балансом денег хватит
        initInvoiceAndPersonalAccount("-0.01", "-50.01", "50.00", false, false, FULL);
        assertBalanceCheckingResult("-0.01", "-50.01", "0.00", ALLOWED, false, false);
    }

    @Test
    public void negativeTestReserveFundsOff() {
        // Порог слишком большой, становится доступно меньше нуля, поэтому DISALLOWED
        initInvoiceAndPersonalAccount("100.00", "100.01", "150.00", false, false, FULL);
        assertBalanceCheckingResult("100.00", "100.01", "0.00", DISALLOWED, false, false);

        // С настройкой trustOnBalanceChecking = true разрешаем, даже если доступно меньше нуля
        initInvoiceAndPersonalAccount("100.00", "100.01", "150.00", false, true, FULL);
        assertBalanceCheckingResult("100.00", "100.01", "0.00", ALLOWED_WITH_DEBT, false, true);

        // в коротком ЖЦ мы уходим в минус, т.к. нельзя приостановится
        initInvoiceAndPersonalAccount("100.00", "100.01", "150.00", false, false, SHORT);
        assertBalanceCheckingResult("100.00", "100.01", "0.00", ALLOWED_WITH_DEBT, false, false);
    }

    @Test
    public void testReserveFundsOn() {
        // Денег достаточно с нулевым порогом
        initInvoiceAndPersonalAccount("100.00", "0.00", "100.00", true, false, FULL);
        assertBalanceCheckingResult("100.00", "0.00", "100.00", ALLOWED, true, false);

        // Денег недостаточно, но порог отрицательный
        initInvoiceAndPersonalAccount("100.00", "-1.00", "101.00", true, false, FULL);
        assertBalanceCheckingResult("100.00", "-1.00", "101.00", ALLOWED, true, false);
    }

    @Test
    public void negativeTestReserveFundsOn() {
        // Денег недостаточно
        initInvoiceAndPersonalAccount("100.00", "0.00", "100.01", true, false, FULL);
        assertBalanceCheckingResult("100.00", "0.00", "100.01", DISALLOWED, true, false);

        // Денег недостаточно из-за положительного порога
        initInvoiceAndPersonalAccount("100.00", "0.01", "100.00", true, false, FULL);
        assertBalanceCheckingResult("100.00", "0.01", "100.00", DISALLOWED, true, false);

        // Денег недостаточно, но trustOnBalanceChecking = true
        initInvoiceAndPersonalAccount("100.00", "0.00", "100.01", true, true, FULL);
        assertBalanceCheckingResult("100.00", "0.00", "100.01", ALLOWED_WITH_DEBT, true, true);

        // Денег недостаточно из-за положительного порога, но trustOnBalanceChecking = true
        initInvoiceAndPersonalAccount("100.00", "0.01", "100.00", true, true, FULL);
        assertBalanceCheckingResult("100.00", "0.01", "100.00", ALLOWED_WITH_DEBT, true, true);

        // Денег недостаточно, но в коротком ЖЦ мы уходим в минус, т.к. нельзя приостановится
        initInvoiceAndPersonalAccount("100.00", "0.00", "100.01", true, false, SHORT);
        assertBalanceCheckingResult("100.00", "0.00", "100.01", ALLOWED_WITH_DEBT, true, false);

        // Денег недостаточно из-за положительного порога, но в коротком ЖЦ мы уходим в минус,
        // т.к. нельзя приостановится
        initInvoiceAndPersonalAccount("100.00", "0.01", "100.00", true, false, SHORT);
        assertBalanceCheckingResult("100.00", "0.01", "100.00", ALLOWED_WITH_DEBT, true, false);
    }

    private void initInvoiceAndPersonalAccount(
            String availableBalance,
            String threshold,
            String invoicePlanTotalCost,
            boolean reserveFunds,
            boolean trustOnBalanceChecking,
            SubscriptionLifecycleQualifier qualifier
    ) {
        when(subscription.getLifecycleQualifier()).thenReturn(qualifier);

        when(subscription.getPersonalAccount()).thenReturn(personalAccount);
        when(subscription.getProvisionTerms()).thenReturn(terms);
        when(plan.summary()).thenReturn(invoicePlanPeriod);

        when(subscription.getPersonalAccount()).thenReturn(personalAccount);
        when(subscription.getProvisionTerms()).thenReturn(terms);
        when(plan.summary()).thenReturn(invoicePlanPeriod);

        when(terms.isReserveFunds()).thenReturn(reserveFunds);
        when(invoicePlanPeriod.totalCost()).thenReturn(new Money(invoicePlanTotalCost));
        when(invoicePlanPeriod.modifier()).thenReturn(modifier);

        when(modifier.trustOnBalanceChecking()).thenReturn(trustOnBalanceChecking);

        when(em.createNamedQuery(eq("PersonalAccountBalanceService.getAvailableBalance"))).thenReturn(query);
        when(query.getSingleResult()).thenReturn(new BigDecimal(availableBalance));

        when(personalAccount.getThreshold()).thenReturn(new Money(threshold));
    }

    private void assertBalanceCheckingResult(
            String expectedAvailable,
            String expectedThreshold,
            String expectedRequired,
            PersonalAccountBalanceService.BalanceCheckingResolution expectedResolution,
            boolean expectedReserveFunds,
            boolean expectedTrustInDebt
    ) {
        val actualResult = service.checkBalance(subscription, plan, false);

        assertEquals(expectedResolution, actualResult.getResolution());
        assertEquals(expectedReserveFunds, actualResult.isReservingScheme());
        assertEquals(expectedTrustInDebt, actualResult.isTrustInDebt());
        assertEquals(
                "Actual available balance is: " + actualResult.getAvailable(),
                0, new Money(expectedAvailable).compareRounded(actualResult.getAvailable())
        );
        assertEquals(
                "Actual required is: " + actualResult.getRequired(),
                0, new Money(expectedRequired).compareRounded(actualResult.getRequired())
        );
        assertEquals(
                "Actual threshold is: " + actualResult.getThreshold(),
                0, new Money(expectedThreshold).compareRounded(actualResult.getThreshold())
        );
    }
}