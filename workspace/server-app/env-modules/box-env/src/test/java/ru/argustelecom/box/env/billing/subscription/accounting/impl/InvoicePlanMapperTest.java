package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToDate;

import org.junit.Test;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.env.privilege.model.PrivilegeType;
import ru.argustelecom.box.env.privilege.model.SubscriptionPrivilege;

import java.math.BigDecimal;

public class InvoicePlanMapperTest extends AbstractAccountingTest {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	private Privilege trustPeriod;
	private Discount discount1;
	private Discount discount2;

	@Override
	public void setup() {
		super.setup();

		// @formatter:off
		
		trustPeriod = new SubscriptionPrivilege(1L, 
			PrivilegeType.TRUST_PERIOD, 
			strToDate("2017-10-16 00:00:00.000"), 
			strToDate("2017-10-18 23:59:59.999"),
			subscription
		);
		
		discount1 = Discount.builder()
			.id(10L)
			.validFrom(strToDate("2017-10-16 00:00:00.000"))
			.validTo(strToDate("2017-10-16 23:59:59.999"))
			.subscription(subscription)
			.rate(BigDecimal.valueOf(30L))
			.build();	
		
		discount2 = Discount.builder()
			.id(20L)
			.validFrom(strToDate("2017-10-18 00:00:00.000")) 
			.validTo(strToDate("2017-10-18 23:59:59.999"))
			.subscription(subscription)
			.rate(BigDecimal.TEN)
			.build();	
		
		// @formatter:on
	}

	@Test
	public void restoredSimplePlanShouldBeEqualsWithStoredPlan() throws JsonProcessingException {
		LongTermInvoice invoice = mockInvoice(CPID.Tc01);
		testPlanRestoration(invoice);
	}

	@Test
	public void restoredComplexPlanShouldBeEqualsWithStoredPlan() throws JsonProcessingException {
		LongTermInvoice invoice = mockInvoice(CPID.Tc01, trustPeriod, discount1, discount2);
		testPlanRestoration(invoice);
	}

	private void testPlanRestoration(LongTermInvoice testedInvoice) throws JsonProcessingException {
		InvoicePlanMapper mapper = new InvoicePlanMapper(testedInvoice);
		ObjectNode rootNode = JsonNodeFactory.instance.objectNode();
		mapper.saveInvoicePlan(testedInvoice.getPlan(), rootNode);

		System.out.println(objectMapper.writeValueAsString(rootNode));

		InvoicePlan restoredPlan = mapper.loadInvoicePlan(rootNode);
		assertThatInvoicePlanEquals(restoredPlan, testedInvoice.getPlan());
	}
}
