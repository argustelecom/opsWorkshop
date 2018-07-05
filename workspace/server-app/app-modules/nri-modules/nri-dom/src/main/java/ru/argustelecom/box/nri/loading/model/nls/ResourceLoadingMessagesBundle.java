package ru.argustelecom.box.nri.loading.model.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Бандл для нагрузки
 */
@MessageBundle(projectCode = "")
public interface ResourceLoadingMessagesBundle {
	/**
	 * Возвращает строку Нагрузка ресурсов:
	 * @return
	 */
	@Message("Нагрузка ресурсов: ")
	String loadingForResources();
}
