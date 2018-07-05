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
import static org.mockito.Matchers.anyListOf;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;
import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.*;

@RunWith(MockitoJUnitRunner.class)
public class SaldoRegisterImportServiceTest {

    @Mock
    private EntityManager em;
    @Mock
    private Query query;
    @Mock
    private TransactionRepository transactionRp;
    @InjectMocks
    private SaldoRegisterImportService importService;

    private RegisterContext ctx;

    @Before
    public void initMockPersonalAccounts() {
        ctx = new RegisterContext(new SaldoRegister());

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
    public void shouldSuccessfullyParseTwoRecords() throws IOException, RegisterException {
        String register = "# 26288857\n" +
                "# 200.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 2\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329\n" +
                "Богданова Елена Владиленовна;Санкт-Петербург,Комендантский пр-кт,34К1,1;К001;100.00;;" +
                "01/09/2010;30/09/2010;100009:;397364792;16/09/2010\n" +
                "Симонов Денис Николаевич;Санкт-Петербург,Комендантский пр-кт,34К1,2;К002;100.00;;" +
                "01/06/2010;30/06/2010;100009:;397364794;16/09/2010";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);

        assertEquals(new BigDecimal("200.00"), ctx.getRegister().getSum());
        assertEquals(2, ctx.getRegister().getSuitableItems().size());

        assertEquals(new Long(1L), ctx.getRegister().getSuitableItems().get(0).getAccountId());
        assertEquals("К001", ctx.getRegister().getSuitableItems().get(0).getAccountNumber());
        assertEquals("397364792", ctx.getRegister().getSuitableItems().get(0).getPaymentDocNumber());
    }

    @Test(expected = RegisterException.class)
    public void shouldFailWithNoRecords() throws IOException, RegisterException {
        String register = "# 26288857\n" +
                "# 200.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 2\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);
    }

    @Test(expected = RegisterException.class)
    public void shouldFailIfRowStartsWithWhitespace() throws IOException, RegisterException {
        String register = " # 26288857\n" +
                "# 200.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 2\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);
    }

    @Test(expected = RegisterException.class)
    public void shouldFailRecordMalformed() throws IOException, RegisterException {
        String register = "# 26288857\n" +
                "# 100.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 2\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329\n" +
                "Богданова Елена Владиленовна;Санкт-Петербург,Комендантский пр-кт,34К1,1;К001;;" +
                "01/09/2010;30/09/2010;100009:;397364792;16/09/2010";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);
    }

    @Test
    public void testPersonalAccountNotPresented() throws IOException, RegisterException {
        String register = "# 26288857\n" +
                "# 100.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 2\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329\n" +
                "Богданова Елена Владиленовна;Санкт-Петербург,Комендантский пр-кт,34К1,1;;100.00;;" +
                "01/09/2010;30/09/2010;100009:;397364792;16/09/2010";
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
        String register = "# 26288857\n" +
                "# 100.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 2\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329\n" +
                "Богданова Елена Владиленовна;Санкт-Петербург,Комендантский пр-кт,34К1,1;К000;100.00;;" +
                "01/09/2010;30/09/2010;100009:;397364792;16/09/2010";
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
        String register = "# 26288857\n" +
                "# 100.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 2\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329\n" +
                "Богданова Елена Владиленовна;Санкт-Петербург,Комендантский пр-кт,34К1,1;К001;-100.00;;" +
                "01/09/2010;30/09/2010;100009:;397364792;16/09/2010";
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
        String register = "# 26288857\n" +
                "# 100.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 1\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329\n" +
                "Симонов Денис Николаевич;Санкт-Петербург,Комендантский пр-кт,34К1,2;К001;100.00;;" +
                "01/06/2010;30/06/2010;100009:;397364794;16/09/2010";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));

        importService.process(ctx, inputStream);
        assertEquals(1, ctx.getRegister().getSuitableItems().size());
        importService.importing(ctx);

        when(transactionRp.generatePaymentDocId(anyLong(), eq("397364794"), anyObject(), eq(new Money("100.00"))))
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

    @Test
    public void shouldCreateTransaction() throws IOException, RegisterException {
        String register = "# 26288857\n" +
                "# 100.00\n" +
                "# 0.00\n" +
                "# 201.84\n" +
                "# 9890.10\n" +
                "# 1\n" +
                "# 56797\n" +
                "# 1300000594\n" +
                "# 16/09/2010 10:08:50\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010 10:03:27\n" +
                "# 16/09/2010:349329\n" +
                "Симонов Денис Николаевич;Санкт-Петербург,Комендантский пр-кт,34К1,2;К001;100.00;;" +
                "01/06/2010;30/06/2010;100009:;397364794;16/09/2010";
        InputStream inputStream = new ByteArrayInputStream(register.getBytes("cp1251"));
        importService.process(ctx, inputStream);
        importService.importing(ctx);

        verify(transactionRp, times(1)).createPaymentDocTransaction(
                anyObject(), anyString(), eq(new Money("100.00")), eq("397364794"), anyObject(), anyString()
        );
    }
}