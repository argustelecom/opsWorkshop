package ru.argustelecom.box.env.billing.bill.model;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;

import java.time.LocalDateTime;

import org.junit.Before;
import org.junit.Test;

import ru.argustelecom.box.env.billing.bill.model.BillDateGetter.BillPeriodDate;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

public class BillDateGetterTest {

	private BillPeriodDate calendarianPeriodDate;

	private static final LocalDateTime PREV_PERIOD_START = strToLdt("2017-09-01 00:00:00.000");
	private static final LocalDateTime PREV_PERIOD_END = strToLdt("2017-09-30 23:59:59.999");

	private static final LocalDateTime CURRENT_PERIOD_START = strToLdt("2017-10-01 00:00:00.000");
	private static final LocalDateTime CURRENT_PERIOD_END = strToLdt("2017-10-31 23:59:59.999");

	private static final LocalDateTime NEXT_PERIOD_START = strToLdt("2017-11-01 00:00:00.000");
	private static final LocalDateTime NEXT_PERIOD_END = strToLdt("2017-11-30 23:59:59.999");

	private static final LocalDateTime INVOICE_DATE = strToLdt("2017-10-15 12:00:00.000");
	private static final LocalDateTime CREATION_DATE = strToLdt("2017-10-16 12:45:32.874");

	@Before
	public void setup() {
		BillPeriod currentBillPeriod = BillPeriod.of(PeriodUnit.MONTH, CURRENT_PERIOD_START);
		calendarianPeriodDate = new BillPeriodDate(currentBillPeriod, INVOICE_DATE, CREATION_DATE);
	}

	@Test
	public void prevBillPeriodTest() {
		BillDateGetter testItem = BillDateGetter.PREVIOUS_PERIOD;

		assertThat(testItem.getStartDate(calendarianPeriodDate), equalTo(PREV_PERIOD_START));
		assertThat(testItem.getEndDate(calendarianPeriodDate), equalTo(PREV_PERIOD_END));
		assertThat(testItem.getDate(calendarianPeriodDate), is(nullValue()));
	}

	@Test
	public void nextBillPeriodTest() {
		BillDateGetter testItem = BillDateGetter.NEXT_PERIOD;

		assertThat(testItem.getStartDate(calendarianPeriodDate), equalTo(NEXT_PERIOD_START));
		assertThat(testItem.getEndDate(calendarianPeriodDate), equalTo(NEXT_PERIOD_END));
		assertThat(testItem.getDate(calendarianPeriodDate), is(nullValue()));
	}

	@Test
	public void currentBillPeriodTest() {
		BillDateGetter testItem = BillDateGetter.CURRENT_PERIOD;

		assertThat(testItem.getStartDate(calendarianPeriodDate), equalTo(CURRENT_PERIOD_START));
		assertThat(testItem.getEndDate(calendarianPeriodDate), equalTo(CURRENT_PERIOD_END));
		assertThat(testItem.getDate(calendarianPeriodDate), is(nullValue()));
	}

	@Test
	public void nextBillPeriodIncomesBeforeBillDateTest() {
		BillDateGetter testItem = BillDateGetter.NEXT_PERIOD_INCOMES_BEFORE_BILL_DATE;

		assertThat(testItem.getStartDate(calendarianPeriodDate), equalTo(CURRENT_PERIOD_START));
		assertThat(testItem.getEndDate(calendarianPeriodDate), equalTo(INVOICE_DATE));
		assertThat(testItem.getDate(calendarianPeriodDate), is(nullValue()));
	}

	@Test
	public void nextBillPeriodIncomesBeforeBillCreationDateTest() {
		BillDateGetter testItem = BillDateGetter.NEXT_PERIOD_INCOMES_BEFORE_BILL_CREATION_DATE;

		assertThat(testItem.getStartDate(calendarianPeriodDate), equalTo(NEXT_PERIOD_START));
		assertThat(testItem.getEndDate(calendarianPeriodDate), equalTo(CREATION_DATE));
		assertThat(testItem.getDate(calendarianPeriodDate), is(nullValue()));
	}

	@Test
	public void prevBillPeriodStartingBalanceTest() {
		BillDateGetter testItem = BillDateGetter.PREVIOUS_PERIOD_STARTING_BALANCE;
		assertThat(testItem.getDate(calendarianPeriodDate), equalTo(PREV_PERIOD_START));
	}

	@Test
	public void currentBillPeriodStartingBalanceTest() {
		BillDateGetter testItem = BillDateGetter.CURRENT_PERIOD_STARTING_BALANCE;
		assertThat(testItem.getDate(calendarianPeriodDate), equalTo(CURRENT_PERIOD_START));
	}

	@Test
	public void currentBillPeriodEndingBalanceTest() {
		BillDateGetter testItem = BillDateGetter.CURRENT_PERIOD_ENDING_BALANCE;
		assertThat(testItem.getDate(calendarianPeriodDate), equalTo(CURRENT_PERIOD_END));
	}

	@Test
	public void nextBillPeriodEndingBalanceTest() {
		BillDateGetter testItem = BillDateGetter.NEXT_PERIOD_ENDING_BALANCE;
		assertThat(testItem.getDate(calendarianPeriodDate), equalTo(NEXT_PERIOD_END));
	}

	@Test
	public void billDateBalanceTest() {
		BillDateGetter testItem = BillDateGetter.BILL_DATE_BALANCE;
		assertThat(testItem.getDate(calendarianPeriodDate), equalTo(INVOICE_DATE));
	}

	@Test
	public void billCreationDateBalanceTest() {
		BillDateGetter testItem = BillDateGetter.BILL_CREATION_DATE_BALANCE;
		assertThat(testItem.getDate(calendarianPeriodDate), equalTo(CREATION_DATE));
	}
}