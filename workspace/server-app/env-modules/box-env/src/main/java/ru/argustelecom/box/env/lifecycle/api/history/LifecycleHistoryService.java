package ru.argustelecom.box.env.lifecycle.api.history;

import java.util.List;

import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.lifecycle.api.history.model.LifecycleHistoryItem;

/**
 * Предоставляет возможность получить историю жизненного цикла для определенного бизнес-объекта
 */
public interface LifecycleHistoryService {

	/**
	 * Возвращает историю жизненного цикла для указанного объекта жизненного цикла. Если истории еще нет, то будет
	 * возвращена пустая коллекция
	 * 
	 * @param businessObject
	 *            - бизнес-объект, чью историю необходимо получить
	 * 
	 * @return Историю жизненного цикла бизнес-объекта
	 */
	List<LifecycleHistoryItem> getHistory(LifecycleObject<?> businessObject);

}
