package ru.argustelecom.box.nri.loading.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Бандл для ResourceLoadingRepository
 */
@MessageBundle(projectCode = "")
public interface ResourceLoadingRepositoryMessagesBundle {
	/**
	 * Возвращает строку Не удалось найти ресурс с указанным идентификатором
	 *
	 * @return
	 */
	@Message("Не удалось найти ресурс с указанным идентификатором ")
	String couldNotFindResourceWithId();

}
