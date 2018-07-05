package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static java.time.temporal.ChronoUnit.MILLIS;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlannerCase.CONTINUATION_OF_CHARGING;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlannerCase.EXPLICIT_PROHIBITION;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlannerCase.PRIMARY_ACTIVATION;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlannerCase.RENEWAL_OF_CHARGIGN;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.ACTIVE;
import static ru.argustelecom.box.env.billing.subscription.model.SubscriptionState.SUSPENDED_ON_DEMAND;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToDate;
import static ru.argustelecom.box.env.stl.period.PeriodTestHelpers.strToLdt;

import org.junit.Test;

import ru.argustelecom.box.env.billing.subscription.model.SubscriptionState;

public class InvoicePlannerCaseTest extends AbstractAccountingTest {

	//@formatter:off
	
	@Test
	public void shouldIdentifyExpliciteProhibitionCase() {
		InvoicePlannerConfig config;

		// Проверка явного запрета на планирование. Не путать с необходимостью планирования

		// Для подписок в состоянии CLOSED
		when(subscription.getState()).thenReturn(SubscriptionState.CLOSED);
		config = config(null, null, null);
		assertThat("Явно запрещено планирование если подписка закрыта",
			EXPLICIT_PROHIBITION.isComplying(config), is(true)	
		);

		// Для подписок в состоянии FORMALIZATION
		when(subscription.getState()).thenReturn(SubscriptionState.FORMALIZATION);
		config = config(null, null, null, null, false);
		assertThat("Явно запрещено планирование если подписка в оформлении и allowPrimaryActivation == false",
			EXPLICIT_PROHIBITION.isComplying(config), is(true)	
		);
		config = config(null, null, null, null, true);
		assertThat("Явно не запрещено планирование если подписка в оформлении и allowPrimaryActivation == true",
			EXPLICIT_PROHIBITION.isComplying(config), is(false)
		);

		// Для подписок в состоянии CLOSURE_WAITING
		when(subscription.getState()).thenReturn(SubscriptionState.CLOSURE_WAITING);
		config = config(planOf(CPID.Tc08, false), null, null, null, false);
		assertThat("Явно запрещено планирование если подписка ожидает закрытия и не тарифицируется",
			EXPLICIT_PROHIBITION.isComplying(config), is(true)		
		);
	}

	@Test
	public void shouldIdentifyPrimaryActivationCase() {
		InvoicePlannerConfig config;

		// Кейсы планирования отложенной активации из состояния ACTIVATION_WAITING
		when(subscription.getState()).thenReturn(SubscriptionState.ACTIVATION_WAITING);
		config = config(null, null, null); 
		assertThat(
			"Если подписка в состоянии ожидания активации, то планирование в выполнять МОЖНО",
			PRIMARY_ACTIVATION.isComplying(config), is(true)
		);

		// Кейсы планирования немедленной активации из состояния FORMALIZATION
		when(subscription.getState()).thenReturn(SubscriptionState.FORMALIZATION);
		config = config(null, null, null, null, true);
		assertThat(
			"Если подписка в состоянии оформления, планирование первичной активации разрешено в планировщике, " + 
			"то планирование выполнять МОЖНО",
			PRIMARY_ACTIVATION.isComplying(config), is(true)
		);
		
		config = config(null, null, null);
		assertThat(
			"Если подписка в состоянии оформления, планирование первичной активации запрещено в планировщике, " + 
			"то планирование списаниий выполнять НЕЛЬЗЯ",
			PRIMARY_ACTIVATION.isComplying(config), is(false)
		);
	}

	@Test
	public void shouldIdentifyContinuationOfChargignCase() {
		InvoicePlannerConfig config; 

		// Отрицательное определение кейса условно непрерывной тарификации
		config = config(null, null, null);
		
		when(subscription.getState()).thenReturn(SUSPENDED_ON_DEMAND);
		assertThat(
			"Если подписка в нетарифицируемом состоянии, то продолжение тарификации НЕВОЗМОЖНО",
			CONTINUATION_OF_CHARGING.isComplying(config), is(false)
		);
		
		when(subscription.getState()).thenReturn(ACTIVE);
		assertThat(
			"Если подписка в тарифицируемом состоянии, но не указан последний инвойс, то продолжение тарификации " + 
			"НЕВОЗМОЖНО",
			CONTINUATION_OF_CHARGING.isComplying(config), is(false)
		);
		
		// Положительное определение кейса условно непрерывной тарификации
		config = config(planOf(CPID.Tc09, true), null, null, null);
		assertThat(
			"Если подписка в тарифицируемом состоянии, указан последний инвойс, то ВОЗМОЖНА дальнейшая тарификация",
			CONTINUATION_OF_CHARGING.isComplying(config), is(true)
		);
	}

	@Test
	public void shouldIdentifyRenewalOfChargignCase() {
		InvoicePlannerConfig config;
		
		// Отрицательное определение кейса возобновления тарификации
		config = config(null, null, null);
		when(subscription.getState()).thenReturn(ACTIVE);
		assertThat(
			"Подписка в тарифицируемом состоянии, возобновление НЕВОЗМОЖНО",
			RENEWAL_OF_CHARGIGN.isComplying(config), is(false)
		);
		
		when(subscription.getState()).thenReturn(SUSPENDED_ON_DEMAND);
		assertThat(
			"Подписка в нетарифицируемом состоянии, но дата возобновления не определена, возобновление НЕВОЗМОЖНО",
			RENEWAL_OF_CHARGIGN.isComplying(config), is(false)
		);
		
		config = config(null, null, "2017-11-12 23:59:59.999");
		assertThat(
			"Подписка в нетарифицируемом состоянии (приостановлено по требованию), дата возобновления определена, " + 
			"но нет последнего плана, возобновление НЕВОЗМОЖНО",
			RENEWAL_OF_CHARGIGN.isComplying(config), is(false)
		);
		
		config = config(planOf(CPID.Tc10, true), null, null, "2017-11-12 23:59:59.999");
		assertThat(
			"Подписка в нетарифицируемом состоянии (приостановлено по требованию), дата возобновления определена, " + 
			"последний план есть, но он в активном состоянии, возобновление НЕВОЗМОЖНО (рассинхронизация состояний)",
			RENEWAL_OF_CHARGIGN.isComplying(config), is(false)
		);
		
		// Положительное определение кейса возобновления тарификации
		config = config(planOf(CPID.Tc09, false), null, null, "2017-11-10 00:00:00.000");
		assertThat(
			"Подписка в нетарифицируемом состоянии (приостановлено по требованию), дата возобновления определена, " + 
			"последний план есть, он в закрытом состоянии, возобновление тарификации ВОЗМОЖНО",
			RENEWAL_OF_CHARGIGN.isComplying(config), is(true)
		);
		
		when(subscription.getState()).thenReturn(SubscriptionState.SUSPENDED_FOR_DEBT);
		config = config(planOf(CPID.Tc09, false), null, null, "2017-11-04 00:00:00.000");
		assertThat(
			"Подписка в нетарифицируемом состоянии (приостановлено за неуплату), дата возобновления определена, " + 
			"последний план есть, он в закрытом состоянии, возобновление тарификации ВОЗМОЖНО",
			RENEWAL_OF_CHARGIGN.isComplying(config), is(true)
		);
		config = config(null, null, "2017-11-04 00:00:00.000");
		assertThat(
			"Подписка в нетарифицируемом состоянии (приостановлено за неуплату), дата возобновления определена, " +
			"последнего плана нет, то это особый случай приостановки из активации, возобновление тарификации ВОЗМОЖНО",
			RENEWAL_OF_CHARGIGN.isComplying(config), is(true)
		);
	}
	
	
	@Test
	public void shouldComputePlannedStartDateOnPrimaryActivationCase() {
		InvoicePlannerConfig config = config("2017-10-14 12:00:00.000", "2017-10-16 11:59:59.999", null);
		
		// Здесь используется понятие "период списания от lowerBound". Это значит, что нужно по параметрам текущей подписки 
		// subscription (по типу периода, длительности периода расчета, длительности периода списания, даты начала 
		// интереса и т.д.) построить такой период списания Tc, чтобы lowerBound входил в этот период. Обозначаться это 
		// будет как Tc(lowerBound)
		
		assertThat(
			"validFrom > Tc(lowerBound).startDate -> validFrom",
			PRIMARY_ACTIVATION.calculatePlannedStartDate(config), 
			equalTo(config.subscriptionStart())
		);
		
		when(subscription.getValidFrom()).thenReturn(strToDate("2017-10-12 12:00:00.000"));
		assertThat(
			"validFrom <= Tc(lowerBound).startDate -> Tc(lowerBound).startDate",
			PRIMARY_ACTIVATION.calculatePlannedStartDate(config), 
			equalTo(config.chargingPeriod(config.boundaries().lowerBound()).startDateTime())
		);
	}
	
	@Test
	public void shouldComputePlannedStartDateOnContinuationOfCharging() {
		InvoicePlannerConfig config = config("2017-11-08 12:00:00.000", "2017-11-30 23:59:59.999", null);
		
		// Здесь также используется понятие "период списания от lowerBound" и обозначается как Tc(lowerBound)
		// кроме этого используется понятие "следующая дата после планируемой даты окончания предыдущего плана". 
		// Обозначается как Lp.planndeEnd + 1
		
		config.setLastPlan(planOf(CPID.Tc09, true));
		config.prepare();
		assertThat(
			"Lp.plannedEnd + 1 > Tc(lowerBound).startDate -> Lp.plannedEnd + 1",
			CONTINUATION_OF_CHARGING.calculatePlannedStartDate(config), 
			equalTo(planOf(CPID.Tc09, true).plannedPeriod().endDateTime().plus(1, MILLIS))
		);
		
		config.setLastPlan(planOf(CPID.Tc07, true));
		config.prepare();
		assertThat(
			"Lp.plannedEnd + 1 <= Tc(lowerBound).startDate -> Tc(lowerBound).startDate",
			CONTINUATION_OF_CHARGING.calculatePlannedStartDate(config),
			equalTo(config.chargingPeriod(config.boundaries().lowerBound()).startDateTime())
		);
	}
	
	@Test
	public void shouldComputePlannedStartDateOnRenewalOfCharging() {
		InvoicePlannerConfig config = config("2017-11-08 12:00:00.000", "2017-11-30 23:59:59.999", null);
		when(subscription.getState()).thenReturn(SubscriptionState.SUSPENDED_ON_DEMAND);
		
		// Здесь также используется понятие "период списания от lowerBound" и обозначается как Tc(lowerBound)
		// кроме этого используется понятие "следующая дата после планируемой даты окончания предыдущего плана". 
		// Обозначается как Lp.billingEnd + 1. И еще используется понятие "дата возобновления", 
		// обозначается как renewalDate
		
		config.setLastPlan(planOf(CPID.Tc09, false));
		config.setRenewalDate(strToLdt("2017-11-07 00:00:00.000"));
		assertThat(
			"(Lp.planndeEnd + 1 > Tc(lowerBound).startDate) > renewalDate -> Lp.planndeEnd + 1",
			RENEWAL_OF_CHARGIGN.calculatePlannedStartDate(config), 
			equalTo(planOf(CPID.Tc09, true).plannedPeriod().endDateTime().plus(1, MILLIS))
		);
		
		config.setLastPlan(planOf(CPID.Tc09, false));
		config.setRenewalDate(strToLdt("2017-11-13 00:00:00.000"));
		assertThat(
			"(Lp.planndeEnd + 1 > Tc(lowerBound).startDate) <= renewalDate -> renewalDate",
			RENEWAL_OF_CHARGIGN.calculatePlannedStartDate(config), 
			equalTo(config.renewalDate())
		);
		
		config.setLastPlan(planOf(CPID.Tc07, false));
		config.setRenewalDate(strToLdt("2017-11-07 00:00:00.000"));
		assertThat(
			"(Lp.planndeEnd + 1 < Tc(lowerBound).startDate) > renewalDate -> Tc(lowerBound).startDate",
			RENEWAL_OF_CHARGIGN.calculatePlannedStartDate(config), 
			equalTo(config.chargingPeriod(config.boundaries().lowerBound()).startDateTime())
		);
		
		config.setLastPlan(planOf(CPID.Tc07, false));
		config.setRenewalDate(strToLdt("2017-11-13 00:00:00.000"));
		assertThat(
			"(Lp.planndeEnd + 1 < Tc(lowerBound).startDate) <= renewalDate -> renewalDate",
			RENEWAL_OF_CHARGIGN.calculatePlannedStartDate(config), 
			equalTo(config.renewalDate())
		);
		
		config.setLastPlan(planOf(CPID.Tc07, false));
		config.setRenewalDate(strToLdt("2017-11-13 00:00:00.000"));
		assertThat(
			"Tc(lowerBound).startDate <= renewalDate -> renewalDate",
			RENEWAL_OF_CHARGIGN.calculatePlannedStartDate(config), 
			equalTo(config.renewalDate())
		);

		config.setLastPlan(planOf(CPID.Tc07, false));
		config.setRenewalDate(strToLdt("2017-11-07 00:00:00.000"));
		assertThat(
			"Tc(lowerBound).startDate > renewalDate -> Tc(lowerBound).startDate",
			RENEWAL_OF_CHARGIGN.calculatePlannedStartDate(config), 
			equalTo(config.chargingPeriod(config.boundaries().lowerBound()).startDateTime())
		);
	}
	
}
