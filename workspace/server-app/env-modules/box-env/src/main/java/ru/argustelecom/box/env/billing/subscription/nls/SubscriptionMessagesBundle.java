package ru.argustelecom.box.env.billing.subscription.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface SubscriptionMessagesBundle {

    // Validators
    @Message("Недостаточно средств на лицевом счете. Доступно: %s, требуется: %s")
    String insufficientFunds(String available, String required);

    @Message("Для подписки не указана дата начала действия")
    String validFromIsNotSpecified();

    @Message("Срок действия подписки истек %s")
    String isExpired(String validTo);

    @Message("При изменении статуса подписки на \"%s\" будет приостановлена тарификация. Все активные на текущий момент иновойсы будут закрыты")
    String deactivationWarn(String nextState);

    // LC routes
    @Message("Активировать")
    String routeActivate();

    @Message("Приостановить за неуплату")
    String routeSuspendForDebt();

    @Message("Приостановить за неуплату")
    String routeCompleteSuspensionForDebt();

    @Message("Активировать")
    String routeActivateAfterDebtSuspension();

    @Message("Приостановить по требованию")
    String routeSuspendOnDemand();

    @Message("Приостановить по требованию")
    String routeCompleteSuspensionOnDemand();

    @Message("Закрыть")
    String routeCloseBeforeActivation();

    @Message("Закрыть")
    String routeCloseFromActive();

    @Message("Закрыть")
    String routeCloseFromSuspension();

    @Message("Закрыть")
    String routeCompleteClosure();

    @Message("Закрыть")
    String routeClose();

    // lc states
    @Message("Оформление")
    String stateFormalization();

    @Message("Ожидает активации")
    String stateActivationWaiting();

    @Message("Действует")
    String stateActive();

    @Message("Ожидает приостановки за неуплату")
    String stateSuspensionForDebtWaiting();

    @Message("Ожидает приостановки по требованию")
    String stateSuspensionOnDemandWaiting();

    @Message("Приостановлена по требованию")
    String stateSuspendedOnDemand();

    @Message("Приостановлена за неуплату")
    String stateSuspendedForDebt();

    @Message("Приостановлена")
    String stateSuspended();

    @Message("Ожидает закрытия")
    String stateClosureWaiting();

    @Message("Закрыта")
    String stateClosed();

    // lc qualifier
    @Message("Полный")
    String lifecycleQualifierFull();

    @Message("Укороченный")
    String lifecycleQualifierShort();

    @Message("Схема полного жизненного цилка подписки")
    String schemeTitleFull();

    @Message("Схема укороченного жизненного цилка подписки")
    String schemeTitleShort();

}
