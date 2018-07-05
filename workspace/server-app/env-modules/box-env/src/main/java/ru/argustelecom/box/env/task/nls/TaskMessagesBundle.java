package ru.argustelecom.box.env.task.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface TaskMessagesBundle {

    // types
    @Message("Работы по активации ресурсов и услуг")
    String typeResourceActivation();

    @Message("Работы по приостановке предоставления ресурсов и услуг за неуплату")
    String typeResourceSuspensionForDebt();

    @Message("Работы по приостановке предоставления ресурсов и услуг по требованию")
    String typeResourceSuspensionOnDemand();

    @Message("Работы по деактивации ресурсов и услуг")
    String typeResourceDeactivation();

    // lc states
    @Message("В ожидании")
    String statePending();

    @Message("Решена")
    String stateResolved();

    // lc routes
    @Message("Решить")
    String routeResolve();
}
