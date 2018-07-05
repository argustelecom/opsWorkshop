package ru.argustelecom.box.env.billing.bill.queue;

import static org.junit.Assert.assertEquals;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.beust.jcommander.internal.Lists;

import ru.argustelecom.box.env.billing.bill.model.BillGroup;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.contract.model.PaymentCondition;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.queue.impl.context.ContextMapper;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;
import ru.argustelecom.system.inf.chrono.DateUtils;

public class BillCreationContextTest {

	private Long billId;
	private String number;
	private BillGroup billGroup;
	private PaymentCondition paymentCondition;
	private BillPeriodType periodType;
	private PeriodUnit periodUnit;
	private Date periodStartDate;
	private Date periodEndDate;
	private Date billDate;
	private Date billCreationDate;
	private Long billTypeId;
	private Long templateId;
	private String contextAsJson;
	private Long contractId;
	private GroupingMethod groupingMrthod;
	private Long providerId;
	private Long brokerId;
	private Long customerId;
	private List<Long> subsIds;
	private List<Long> usageInvoiceIds;
	private List<Long> shortTermInvoiceIds;

	@Before
	public void setup() throws ParseException {
		initBillGroup();
		billId = 1L;
		number = "C17111-0001";
		paymentCondition = PaymentCondition.PREPAYMENT;
		periodType = BillPeriodType.CALENDARIAN;
		periodUnit = PeriodUnit.MONTH;
		periodStartDate = new SimpleDateFormat(DateUtils.DATE_DEFAULT_PATTERN).parse("01.10.2017");
		periodEndDate = new SimpleDateFormat(DateUtils.DATE_DEFAULT_PATTERN).parse("30.10.2017");
		billDate = new SimpleDateFormat(DateUtils.DATE_DEFAULT_PATTERN).parse("26.10.2017");
		billCreationDate = new SimpleDateFormat(DateUtils.DATE_DEFAULT_PATTERN).parse("26.10.2017");
		billTypeId = 123L;
		templateId = 3345L;
		contextAsJson = "{\"id\":1,\"number\":\"C17111-0001\",\"billGroup\":{\"id\":1,\"type\":\"CONTRACT\",\"providerId\":8,\"brokerId\":9,\"customerId\":2,"
				+ "\"subscriptionIds\":[100,200,100500],\"usageInvoiceIds\":[123456,444,1000000,7779999],\"shortTermInvoiceIds\":[]},"
				+ "\"paymentCondition\":\"PREPAYMENT\",\"periodType\":\"CALENDARIAN\",\"periodUnit\":\"MONTH\",\"periodStartDate\":1506805200000,"
				+ "\"periodEndDate\":1509310800000,\"billTypeId\":123,\"templateId\":3345,\"billDate\":1508965200000,\"billCreationDate\":1508965200000}";
	}

	@Test
	public void shouldCreateBillCreationContext() {
		BillCreationContext context = createContext();

		checkContext(context);
	}

	@Test
	public void shouldMarshallToJson() {
		String json = ContextMapper.marshall(createContext());

		assertEquals(contextAsJson, json);

		BillCreationContext contextFromJson = new BillCreationContext(new QueueEventImpl());
		ContextMapper.update(contextFromJson, contextAsJson);

		checkContext(contextFromJson);
	}

	private BillCreationContext createContext() {
		//@formatter:off
		return BillCreationContext.builder()
					.id(billId)
					.number(number)
					.billGroup(billGroup)
					.paymentCondition(paymentCondition)
					.periodType(periodType)
					.periodUnit(periodUnit)
					.periodStartDate(periodStartDate)
					.periodEndDate(periodEndDate)
					.billDate(billDate)
					.billCreationDate(billCreationDate)
					.billTypeId(billTypeId)
					.templateId(templateId)
				.build();
		//@formatter:on
	}

	private void checkContext(BillCreationContext context) {
		checkBillGroup(context.getBillGroup());
		assertEquals(billId, context.getId());
		assertEquals(number, context.getNumber());
		assertEquals(paymentCondition, context.getPaymentCondition());
		assertEquals(periodType, context.getPeriodType());
		assertEquals(periodUnit, context.getPeriodUnit());
		assertEquals(periodStartDate, context.getPeriodStartDate());
		assertEquals(periodEndDate, context.getPeriodEndDate());
		assertEquals(billDate, context.getBillDate());
		assertEquals(billCreationDate, context.getBillCreationDate());
		assertEquals(billTypeId, context.getBillTypeId());
		assertEquals(templateId, context.getTemplateId());
	}

	private void checkBillGroup(BillGroup billGroup) {
		assertEquals(contractId, billGroup.getId());
		assertEquals(groupingMrthod, billGroup.getType());
		assertEquals(providerId, billGroup.getProviderId());
		assertEquals(brokerId, billGroup.getBrokerId());
		assertEquals(customerId, billGroup.getCustomerId());
		assertEquals(subsIds, billGroup.getSubscriptionIds());
		assertEquals(usageInvoiceIds, billGroup.getUsageInvoiceIds());
		assertEquals(shortTermInvoiceIds, billGroup.getShortTermInvoiceIds());
	}

	private void initBillGroup() {
		contractId = 1L;
		groupingMrthod = GroupingMethod.CONTRACT;
		providerId = 8L;
		brokerId = 9L;
		customerId = 2L;
		subsIds = Lists.newArrayList(100L, 200L, 100500L);
		usageInvoiceIds = Lists.newArrayList(123456L, 444L, 1000000L, 7779999L);
		shortTermInvoiceIds = new ArrayList<>();
		billGroup = new BillGroup(contractId, groupingMrthod, providerId, brokerId, customerId, subsIds,
				usageInvoiceIds, shortTermInvoiceIds);
	}

}