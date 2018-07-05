package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static java.util.Collections.emptyMap;
import static java.util.Collections.singletonList;
import static java.util.Collections.singletonMap;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToDate;

import java.math.BigDecimal;
import java.util.Collections;

import org.junit.Test;
import org.mockito.Mockito;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.PeriodDuration;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.env.stl.period.PeriodUnit;

public class AccountingServiceTest extends AbstractAccountingServiceTest {

	//@formatter:off
	
	@Test
	public void shouldPassBox519TestCase() {
		// Тест для проверки кейса http://support.argustelecom.ru:10609/browse/BOX-519

		when(provisionTerms.getPeriodType()).thenReturn(PeriodType.DEBUG);
		when(provisionTerms.getChargingDuration()).thenReturn(PeriodDuration.of(40, PeriodUnit.MINUTE));
		when(subscription.getAccountingDuration()).thenReturn(PeriodDuration.of(1, PeriodUnit.HOUR));
		when(subscription.getCost()).thenReturn(new Money("100"));
		when(subscription.getValidFrom()).thenReturn(strToDate("2018-02-16 19:05:43.194"));
		
		when(discountsRp.findDiscounts(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(emptyMap());
		when(privilegeRp.findPrivileges(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(emptyMap());
		
		when(subscription.getState()).thenReturn(SubscriptionState.FORMALIZATION);
		InvoicePlan firstPlan = accountingSvc.calculateNextAccruals(
			subscription, null, strToDate("2018-02-16 19:05:43.194")
		);
		LongTermInvoice firstInvoice = LongTermInvoice.builder()
			.id(invoiceIdCounter.getAndIncrement())
			.subscription(subscription)
			.plan(firstPlan)
			.build();
		
		assertThat(firstPlan, is(notNullValue()));
		assertThat(firstPlan.summary().startDate(), equalTo(strToDate("2018-02-16 19:05:43.194")));
		assertThat(firstPlan.summary().endDate(), equalTo(strToDate("2018-02-16 19:45:43.193")));
		assertThat(firstPlan.summary().baseUnitCount(), equalTo(40L));
		assertThat(firstPlan.summary().cost().getRoundAmount(), equalTo(new BigDecimal("66.67")));
		
		
		when(subscription.getState()).thenReturn(SubscriptionState.ACTIVE);
		InvoicePlan secondPlan = accountingSvc.calculateNextAccruals(
			subscription, firstInvoice, strToDate("2018-02-16 19:45:43.194")
		);
		
		assertThat(secondPlan, is(notNullValue()));
		assertThat(secondPlan.summary().startDate(), equalTo(strToDate("2018-02-16 19:45:43.194")));
		assertThat(secondPlan.summary().endDate(), equalTo(strToDate("2018-02-16 20:05:43.193")));
		assertThat(secondPlan.summary().baseUnitCount(), equalTo(20L));
		assertThat(secondPlan.summary().cost().getRoundAmount(), equalTo(new BigDecimal("33.33")));
	}

	@Test
	public void shouldApplyDiscount(){
		Discount discount = Discount.builder()
			.id(10L)
			.validFrom(cpOf(CPID.Tc05).startDate())
			.validTo(cpOf(CPID.Tc05).endDate())
			.subscription(subscription)
			.rate(BigDecimal.valueOf(50L))
		.build();

		when(privilegeRp.findPrivileges(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(emptyMap());
		when(discountsRp.findDiscounts(Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(
			singletonMap(subscription, singletonList(discount))
		);

		InvoicePlan nextPlan = accountingSvc.calculateNextAccruals(
			subscription, mockInvoice(CPID.Tc03), cpOf(CPID.Tc05).startDate()
		);

		assertThat(nextPlan, is(notNullValue()));
		assertThat(nextPlan.summary().totalCost().getRoundAmount(), equalTo(new BigDecimal("14.52")));
	}
}
