package ru.argustelecom.box.env.saldo.imp;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.saldo.imp.model.*;
import ru.argustelecom.box.env.stl.Money;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.singletonList;
import static org.junit.Assert.*;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.when;
import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.*;

@RunWith(MockitoJUnitRunner.class)
public class ED108ImportServiceTest {

    @Mock
    private EntityManager em;
    @Mock
    private Query query;
    @Mock
    private TransactionRepository transactionRp;
    @InjectMocks
    private ED108ImportService importService;

    private RegisterContext ctx;

    @Before
    public void initMockPersonalAccounts() {
        ctx = new RegisterContext(new ED108Register());

        List<Object[]> accounts = new ArrayList<>();
        Object[] firstPersonalAccount = {1L, "К001"};
        Object[] secondPersonalAccount = {2L, "К002"};

        accounts.add(firstPersonalAccount);
        accounts.add(secondPersonalAccount);

        when(em.createNamedQuery(eq("RegisterImportService.findAccounts"))).thenReturn(query);
        when(query.setParameter(eq("numbers"), anyListOf(String.class))).thenReturn(query);
        when(query.getResultList()).thenReturn(accounts);
    }

    @Test
    public void shouldSuccessfullyParseRecord() throws IOException, RegisterException {
        String register = "13-06-2017;" +
                "20-38-23;" +
                "8617;" +
                "8617999V;" +
                "250901027740;" +
                "К001;" +
                "Корзов Андрей Сергеевич;" +
                "г.Санкт-Петербург, ул.Корнея Чуковского, д.5 корпус 3 квартира 141;" +
                "интернет;" +
                "600,00;" +
                "600,00;" +
                "0,00\n" +
                "=1;600,00;600,00;0,00;341335;14-06-2017";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);

        assertEquals(new BigDecimal("600.00"), ctx.getRegister().getSum());
        assertEquals(1, ctx.getRegister().getSuitableItems().size());

        assertEquals(new Long(1L), ctx.getRegister().getSuitableItems().get(0).getAccountId());
        assertEquals("К001", ctx.getRegister().getSuitableItems().get(0).getAccountNumber());
        assertEquals("341335", ctx.getRegister().getSuitableItems().get(0).getPaymentDocNumber());
    }

    @Test(expected = RegisterException.class)
    public void shouldFailWithNoRecords() throws IOException, RegisterException {
        String register = "=1;600,00;600,00;0,00;341335;14-06-2017";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);
    }

    @Test(expected = RegisterException.class)
    public void shouldFailRegisterMalformed() throws IOException, RegisterException {
        String register = "13-06-2017;" +
                "20-38-23;" +
                "8617;" +
                "8617999V;" +
                "250901027740;" +
                "К001;" +
                "Корзов Андрей Сергеевич;" +
                "г.Санкт-Петербург, ул.Корнея Чуковского, д.5 корпус 3 квартира 141;" +
                "интернет;" +
                "600.0;" +
                "600,00;" +
                "0,00\n" +
                "=";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);
    }

    @Test
    public void testPersonalAccountNotPresented() throws IOException, RegisterException {
        String register = "13-06-2017;" +
                "20-38-23;" +
                "8617;" +
                "8617999V;" +
                "250901027740;" +
                ";" +
                "Корзов Андрей Сергеевич;" +
                "г.Санкт-Петербург, ул.Корнея Чуковского, д.5 корпус 3 квартира 141;" +
                "интернет;" +
                "600,00;" +
                "600,00;" +
                "0,00\n" +
                "=1;600,00;600,00;0,00;341335;14-06-2017";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);

        Container requiredCorrection = ctx.getRegister().getContainers().stream()
                .filter(container -> container.getType() == ResultType.REQUIRED_CORRECTION)
                .findAny()
                .orElse(null);

        assertEquals(0, ctx.getRegister().getSuitableItems().size());

        assertNotNull(requiredCorrection);
        assertEquals(1, requiredCorrection.getItems().size());
        assertEquals(2, requiredCorrection.getErrors().size());
        assertTrue(requiredCorrection.getErrors().contains(IMPOSSIBLE_DETERMINE_ACCOUNT));
        assertTrue(requiredCorrection.getErrors().contains(INCORRECT_ACCOUNT_NUMBER));
    }

    @Test
    public void testPersonalAccountNotFound() throws IOException, RegisterException {
        String register = "13-06-2017;" +
                "20-38-23;" +
                "8617;" +
                "8617999V;" +
                "250901027740;" +
                "К000;" +
                "Корзов Андрей Сергеевич;" +
                "г.Санкт-Петербург, ул.Корнея Чуковского, д.5 корпус 3 квартира 141;" +
                "интернет;" +
                "600,00;" +
                "600,00;" +
                "0,00\n" +
                "=1;600,00;600,00;0,00;341335;14-06-2017";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);

        Container requiredCorrection = ctx.getRegister().getContainers().stream()
                .filter(container -> container.getType() == ResultType.REQUIRED_CORRECTION)
                .findAny()
                .orElse(null);

        assertEquals(0, ctx.getRegister().getSuitableItems().size());

        assertNotNull(requiredCorrection);
        assertEquals(1, requiredCorrection.getItems().size());
        assertEquals(1, requiredCorrection.getErrors().size());
        assertTrue(requiredCorrection.getErrors().contains(IMPOSSIBLE_DETERMINE_ACCOUNT));
    }

    @Test
    public void testSumIsNegative() throws IOException, RegisterException {
        String register = "13-06-2017;" +
                "20-38-23;" +
                "8617;" +
                "8617999V;" +
                "250901027740;" +
                "К001;" +
                "Корзов Андрей Сергеевич;" +
                "г.Санкт-Петербург, ул.Корнея Чуковского, д.5 корпус 3 квартира 141;" +
                "интернет;" +
                "600,00;" +
                "-600,00;" +
                "0,00\n" +
                "=1;600,00;600,00;0,00;341335;14-06-2017";

        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);

        Container requiredCorrection = ctx.getRegister().getContainers().stream()
                .filter(container -> container.getType() == ResultType.REQUIRED_CORRECTION)
                .findAny()
                .orElse(null);

        assertEquals(0, ctx.getRegister().getSuitableItems().size());

        assertNotNull(requiredCorrection);
        assertEquals(1, requiredCorrection.getItems().size());
        assertEquals(1, requiredCorrection.getErrors().size());
        assertTrue(requiredCorrection.getErrors().contains(INCORRECT_SUM));
    }

    @Test
    public void testAttemptToImportAgain() throws IOException, RegisterException {
        String register = "13-06-2017;" +
                "20-38-23;" +
                "8617;" +
                "8617999V;" +
                "250901027740;" +
                "К001;" +
                "Корзов Андрей Сергеевич;" +
                "г.Санкт-Петербург, ул.Корнея Чуковского, д.5 корпус 3 квартира 141;" +
                "интернет;" +
                "600,00;" +
                "600,00;" +
                "0,00\n" +
                "=1;600,00;600,00;0,00;341335;14-06-2017";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);
        assertEquals(1, ctx.getRegister().getSuitableItems().size());
        importService.importing(ctx);

        when(transactionRp.generatePaymentDocId(anyLong(), eq("341335"), anyObject(), eq(new Money("600.00"))))
                .thenReturn("test-tx");
        when(transactionRp.findSameTransactions(singletonList("test-tx"))).thenReturn(singletonList("test-tx"));

        inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));
        importService.process(ctx, inputStream);

        Container notSuitable = ctx.getRegister().getContainers().stream()
                .filter(container -> container.getType() == ResultType.NOT_SUITABLE)
                .findAny()
                .orElse(null);

        assertNotNull(notSuitable);
        assertTrue(notSuitable.getErrors().contains(TRYING_TO_RE_IMPORT));
    }
}