package ru.argustelecom.box.env.billing.invoice.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface InvoiceMessagesBundle {

    @Message(value = "Невозможно добавить позицию '%s'")
    String cannotAddEntry(String entry);

    @Message(value = "Недостаточно средств на лицевом счёте. Доступный баланс '%s', в том числе выбрано позиций на '%s'")
    String insufficientFunds(String availableBalance, String total);

    // lc routes
    @Message("Активировать")
    String routeActivate();

    @Message("Аннулировать")
    String routeCancel();

    @Message("Закрыть")
    String routeClose();

    // lc states
    @Message("Оформление")
    String stateCreated();

    @Message("Действует")
    String stateActive();

    @Message("Закрыт")
    String stateClosed();

    @Message("Аннулирован")
    String stateCancelled();

	@Message("Жизненный цикл инвойса по фактам использования")
	String usageInvoiceLifecycle();

    @Message("Окончание периода расчета")
    String accountingPeriodEnd();

    @Message("Окончание периода списания")
    String chargingPeriodEnd();
}
