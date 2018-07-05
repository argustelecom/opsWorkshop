package ru.argustelecom.box.env.billing.transaction.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface TransactionMessagesBundle {

    @Message("Пополнение баланса")
    String replenishment();

    @Message("Списание средств")
    String charge();

}
