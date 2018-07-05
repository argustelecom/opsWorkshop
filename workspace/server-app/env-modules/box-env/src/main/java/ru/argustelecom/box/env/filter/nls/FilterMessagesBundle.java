package ru.argustelecom.box.env.filter.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface FilterMessagesBundle {

    @Message("Название фильтра не может быть пустым")
    String presetNameIsBlank();

    @Message("Фильтр с таким именем уже существует")
    String presetAlreadyExists();

    @Message("Невозможно сохранить фильтр")
    String cannotSavePreset();

    @Message("Не задан ни один параметр фильтрации")
    String noParamsSpecified();

    @Message("Фильтр %s успешно сохранен")
    String presetSaved(String name);

}
