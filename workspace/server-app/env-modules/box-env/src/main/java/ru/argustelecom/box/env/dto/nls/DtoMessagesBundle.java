package ru.argustelecom.box.env.dto.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface DtoMessagesBundle {

    @Message("Для поиска по имени клиента необходимо выбрать значение из предложенных")
    String customerShouldBeChosenFromList();

}
