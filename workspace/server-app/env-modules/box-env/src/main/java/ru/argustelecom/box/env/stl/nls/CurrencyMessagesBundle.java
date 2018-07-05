package ru.argustelecom.box.env.stl.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface CurrencyMessagesBundle {

    @Message("Евро")
    String currencyEuro();

    @Message("Российский рубль")
    String currencyRussianRuble();

    @Message("Доллар США")
    String currencyUsDollar();

}
