package ru.argustelecom.ops.env.overall.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface OverallMessagesBundle {

	@Message(value = "Невозможно сохранить изменения")
	String cannotSaveChanges();

	@Message(value = "Нарушение ограничения уникальности")
	String uniqueConstraintViolation();

	@Message("Попытка загрузить пустой файл")
	String attemptToDownloadEmptyFile();

	@Message("Нельзя прикреплять пустые файлы")
	String cannotAttachEmptyFile();

	@Message("Файл повреждён")
	String fileIsCorrupted();

	@Message("Ошибка")
	String error();

	@Message("Успех")
	String success();

	@Message("Внимание")
	String warning();

	@Message("Данная функциональность не реализован в данной версии")
	String notImplemented();

	@Message("Невозможно удалить объект")
	String cannotDeleteObject();

	@Message("Есть зависимые объекты")
	String objectHasDependentObjects();

	@Message("Сохранить")
	String save();

	@Message("Создать")
	String create();

	@Message("Редактировать")
	String edit();

	@Message("Создание объекта")
	String objectCreation();

	@Message("Редактирование объекта")
	String objectEditing();

}
