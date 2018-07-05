package ru.argustelecom.box.env.billing.reason.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ReasonMessagesBundle {

    @Message("Инвойс")
    String invoice();

    @Message("за использование услуг")
    String usage();

    @Message("Основание: %s %s")
    String reason(String reasonType, String reasonNumber);

    @Message("Задание на пересчет %s")
    String jobCancelReason(Long jobId);
}
