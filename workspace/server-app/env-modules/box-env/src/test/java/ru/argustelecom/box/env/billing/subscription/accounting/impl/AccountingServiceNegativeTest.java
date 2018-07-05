package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static java.util.Collections.singletonList;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;

public class AccountingServiceNegativeTest extends AbstractAccountingServiceTest {

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnCalculateNextAccrualsWhenSubscriptionIsNull() {
		accountingSvc.calculateNextAccruals(null, START_OF_INTEREST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnCalculateNextAccrualsWhenCalculationDateIsNull() {
		accountingSvc.calculateNextAccruals(subscription, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnCalculateBillAccrualsWhenSubscriptionsIsNull() {
		accountingSvc.calculateBillAccruals((List<Subscription>) null, START_OF_INTEREST, START_OF_INTEREST,
				START_OF_INTEREST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnCalculateBillAccrualsWhenStartDateIsNull() {
		accountingSvc.calculateBillAccruals(singletonList(subscription), null, START_OF_INTEREST, START_OF_INTEREST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnCalculateBillAccrualsWhenEndDateIsNull() {
		accountingSvc.calculateBillAccruals(singletonList(subscription), START_OF_INTEREST, null, START_OF_INTEREST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnCalculateBillAccrualsWhenRenewalDateIsNull() {
		accountingSvc.calculateBillAccruals(singletonList(subscription), START_OF_INTEREST, START_OF_INTEREST, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnRecalculateWhenInvoiceIsNull() {
		accountingSvc.recalculate(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnRecalculateWhenInvoiceStateIsClosed() {
		LongTermInvoice invoice = mockInvoice();
		when(invoice.getState()).thenReturn(InvoiceState.CLOSED);
		accountingSvc.recalculate(invoice);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnRecalculateOnPrematureClosingWhenInvoiceIsNull() {
		accountingSvc.recalculateOnPrematureClosing(null, START_OF_INTEREST);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnRecalculateOnPrematureClosingWhenClosingDateIsNull() {
		accountingSvc.recalculateOnPrematureClosing(mockInvoice(), null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void shouldFailOnRecalculateOnPrematureClosingWhenInvoiceStateIsClosed() {
		LongTermInvoice invoice = mockInvoice();
		when(invoice.getState()).thenReturn(InvoiceState.CLOSED);
		accountingSvc.recalculateOnPrematureClosing(invoice, START_OF_INTEREST);
	}

	@Test(expected = IllegalStateException.class)
	public void shouldFailOnRecalculateOnPrematureClosingWhenClosingDateBeforeInvoiceStart() {
		LongTermInvoice invoice = mockInvoice(CPID.Tc01);
		accountingSvc.recalculateOnPrematureClosing(invoice, cpOf(CPID.Tc01).endDate());
	}

	@Test(expected = IllegalStateException.class)
	@Ignore("Неактуально после устранения BOX-2435")
	public void shouldFailOnRecalculateOnPrematureClosingWhenClosingDateAfterInvoiceEnd() {
		LongTermInvoice invoice = mockInvoice(CPID.Tc01);
		accountingSvc.recalculateOnPrematureClosing(invoice, cpOf(CPID.Tc03).startDate());
	}

	// TODO остальные негативные кейсы

}
