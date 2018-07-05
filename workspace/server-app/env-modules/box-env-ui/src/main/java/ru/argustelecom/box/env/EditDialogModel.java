package ru.argustelecom.box.env;

import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.modelbase.Identifiable;

/**
 * Интерфейс, который вводит общую структуру для модели диалога создания и редактирования некоторого
 * {@link Identifiable}
 */
public interface EditDialogModel<T> {

	/**
	 * Определяет режим работы диалога.
	 * 
	 * @return <b>true</b> если режим редактирования ({@linkplain #getEditableObject() есть редактируемые объект})
	 */
	default boolean isEditMode() {
		return getEditableObject() != null;
	}

	/**
	 * Реализация метода должна создавать новый объект или изменять существующий в зависимости от
	 */
	void submit();

	void cancel();

	default String getSubmitButtonLabel() {
		OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		if (isEditMode()) {
			return messages.save();
		} else {
			return messages.create();
		}
	}

	/**
	 * @return Заголовок для диалога.
	 */
	default String getHeader() {
		OverallMessagesBundle messages = LocaleUtils.getMessages(OverallMessagesBundle.class);
		if (isEditMode()) {
			return messages.objectEditing();
		} else {
			return messages.objectCreation();
		}
	}

	/**
	 * @return Объект, для которого открыт диалог редактирования.
	 */
	T getEditableObject();

	void setEditableObject(T editableObject);

	/**
	 * @return callback, который нужно выполнить после создания/изменения объекта.
	 */
	Callback<T> getCallback();

	void setCallback(Callback<T> callback);

}