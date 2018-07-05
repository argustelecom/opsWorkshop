package ru.argustelecom.box.env.billing.provision.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ProvisionTermsMessagesBundle {

	// lifecycle routes
	@Message("Активировать")
	String routeActivate();

	@Message("Закрыть")
	String routeClose();

	@Message("Условие предоставления в статусе \"Архив\" становится недоступно при создании продуктовых предложений. "
			+ "Тарификация по незакрытым подпискам, \n сформированным на продукты с архивным условием предоставления, "
			+ "продолжает осуществляться согласно\n установленным ранее параметрам.")
	String warnBeforeClose();

	// RecurrentTermsState
	@Message("Оформление")
	String stateFormalization();

	@Message("Активна")
	String stateActive();

	@Message("Закрыта")
	String stateClosed();

	// PrematureActionPolicy
	@Message("Стоимость фактического использования")
	String prematureActionPolicyFactualPeriodCost();

	@Message("Стоимость периода списания")
	String prematureActionPolicyChargingPeriodCost();

	@Message("Стоимость периода расчёта")
	String prematureActionPolicyAccountingPeriodCost();

	// RoundingPolicy
	@Message("В большую сторону")
	String roundingPolicyUp();

	@Message("В меньшую сторону")
	String roundingPolicyDown();

	// MustHaveFilledInvoiceProcessingRules
	@Message("Для перехода \"%s\" должно быть настроено правило \"%s\"")
	String emptyRuleWarn(String nextState, String rule);

	@Message("Единовременное")
	String recurrent();

}
