package ru.argustelecom.box.env.lifecycle.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface LifecycleMessagesBundle {

    @Message("Изменение состояния %s (%s - %s)")
    String stateTransition(String object, String from, String to);

    @Message("Переход заблокирован из-за наличия ошибок: %s")
    String transitionIsBlocked(String explanation);

}
