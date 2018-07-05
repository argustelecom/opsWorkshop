package ru.argustelecom.box.env.stl.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface MoneyMessagesBundle {

    @Message("Сумма не может равняться 0")
    String amountCannotBeZero();

    @Message("Поле \"Стоимость (с учетом НДС)\" заполнено некорректно")
    String amountIsNotCorrect();

}


