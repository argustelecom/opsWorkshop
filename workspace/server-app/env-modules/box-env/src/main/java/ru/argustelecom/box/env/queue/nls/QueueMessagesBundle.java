package ru.argustelecom.box.env.queue.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface QueueMessagesBundle {

    @Message("Активируется")
    String stateActivating();

    @Message("Активирована")
    String stateActive();

    @Message("Деактивируется")
    String stateDeactivating();

    @Message("Деактивирована")
    String stateInactive();

}
