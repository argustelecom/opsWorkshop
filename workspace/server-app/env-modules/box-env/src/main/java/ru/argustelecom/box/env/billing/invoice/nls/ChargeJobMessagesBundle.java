package ru.argustelecom.box.env.billing.invoice.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ChargeJobMessagesBundle {

    // lc states
    @Message("Оформление")
    String stateFormalization();

    @Message("Выполняется в предбиллинге")
    String statePerformedPreBilling();

    @Message("Синхронизация")
    String stateSynchronization();

    @Message("Синхронизация завершена")
    String stateSynchronized();

	@Message("Выполняется в биллинге")
	String statePerformedBilling();

	@Message("Выполнено")
	String stateDone();

    @Message("Прервано")
    String stateAborted();

    //Data type
	@Message("Штатное")
	String regular();

	@Message("Повторное для пригодных данных")
	String suitable();

	@Message("Повторное для непригодных данных")
	String unsuitable();

	//Recharging cause
	@Message("Был выбран ошибочный тарифный план, ошибка исправлена")
	String selectWrongTariff();

	@Message("Была исправлена ошибка в тарифном плане, тарифный план не изменялся")
	String errorsInTariff();

	@Message("Некорректные настройки предбиллинга (полная тарификация)")
	String incorrectPreBillingSettings();

	@Message("Неверно определена связь услуги и ресурса, ошибка исправлена")
	String incorrectResourceServiceRelationship();
	@Message("Жизненный цикл задания на перетарификацию")
	String rechargingLifecycleName();

	@Message("Жизненный цикл задания на тарификацию")
	String chargingLifecycleName();

	//Route names

	@Message("Выполнить в предбилинге")
	String performAtPreBilling();

	@Message("Синхронизировать задание на тарификацию")
	String synchronizeCharging();

	@Message("Синхронизировать задание на перетарификацию")
	String synchronizeRecharging();

	@Message("Выполнить в биллинге")
	String performAtBilling();

	@Message("Завершить")
	String finish();

	@Message("Прервать")
	String abort();

	//validation

	@Message("Дата начала не может быть больше даты окончания")
	String endDateMustBeAfterStartDate();

	@Message("Заполните поля: услуга или тариф")
	String serviceOrTariffShouldBeEntered();
}
