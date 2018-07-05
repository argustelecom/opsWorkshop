package ru.argustelecom.box.env.billing.account.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface PersonalAccountMessagesBundle {

    @Message("Закрыть")
    String routeClose();
    
    @Message("Действует")
    String stateActive();
    
    @Message("Закрыт")
    String stateClosed();

}
