package ru.argustelecom.ops.inf.queue.api.context;

import static com.google.common.base.Preconditions.checkState;

import java.io.Serializable;

import javax.persistence.EntityManager;

import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

/**
 * Позволяет сохранить и восстановить ссылку на персистентную сущность. Предназначен для сериализации экземпляров
 * сущностей в персистентном хранилище очередей и событий для получения доступа к сущность в процессе обработки события
 */
public class EntityReference<T extends Identifiable> implements Serializable {

	private String identity;

	/**
	 * Создает экземпляр ссылки-на-сущность по указанному готовому identity. Используется системным кодом, прикладной
	 * разработчик не должен использовать этот метод, т.к. рискует получить отложенный ClassCastException при
	 * несоответствии параметра T и реального класса сущности. Такое исключение может произойти во время восстановления
	 * ссылки-на-сущность.
	 * 
	 * @param identity
	 *            - строка-идентификатор сущности
	 */
	public EntityReference(String identity) {
		this.identity = identity;
	}

	/**
	 * Создает экземпляр ссылки-на-сущность. Предназначен для использования из прикладного кода и гарантирует, что
	 * ссылка, созданная этим методом, будет правильно восстановлена
	 * 
	 * @param entity
	 *            - сущность, которую необходимо превратить в ссылку-на-сущность
	 */
	public EntityReference(T entity) {
		set(entity);
	}

	/**
	 * Возвращает строку-идентификатор персистентной сущности, по которой эта сущность может быть восстановлена при
	 * помощи {@link EntityConverter}
	 * 
	 * @return идентификатор сущности
	 */
	public String identity() {
		return identity;
	}

	/**
	 * Проверяет, является ли текущая ссылка-на-сущность неопределенной или нет
	 * 
	 * @return true, если текущая ссылка-на-сущность не определена
	 */
	public boolean isNull() {
		return identity == null;
	}

	/**
	 * Позволяет получить экземпляр настоящей сущности, сокрытой этой ссылкой-на-сущность. Поиск будет выполняться в
	 * контектсе персистеции по-умолчанию. Если ссылка на сущность не определена, то вернет null
	 * 
	 * @return восстановленную сущность или null, если ссылка не определена
	 */
	public T get() {
		return get(null);
	}

	/**
	 * Позволяет получить экземпляр настоящей сущности, сокрытой этой ссылкой-на-сущность. Поиск будет выполняться в
	 * контектсе персистеции по-умолчанию. Если ссылка на сущность не определена, то вернет defaultValue
	 * 
	 * @param defaultValue
	 *            - значение по-умолчанию
	 * 
	 * @return восстановленную сущность или defaultValue, если ссылка не определена
	 */
	public T orElse(T defaultValue) {
		T entity = get();
		return entity != null ? entity : defaultValue;
	}

	/**
	 * Позволяет получить экземпляр настоящей сущности, сокрытой этой ссылкой-на-сущность. Поиск будет выполняться в
	 * указанном контектсе персистеции. Если ссылка на сущность не определена, то вернет null
	 * 
	 * @param em
	 *            - контекст персистенции, в котором нужно искать сущность
	 * 
	 * @return восстановленную сущность или null, если ссылка не определена
	 */

	@SuppressWarnings("unchecked")
	public T get(EntityManager em) {
		T instance = null;
		if (identity != null) {
			instance = (T) new EntityConverter(em).convertToObject(identity);
			if (instance != null) {
				instance = EntityManagerUtils.initializeAndUnproxy(instance);

			}
		}
		return instance;
	}

	/**
	 * Позволяет получить экземпляр настоящей сущности, сокрытой этой ссылкой-на-сущность. Поиск будет выполняться в
	 * указанном контектсе персистеции. Если ссылка на сущность не определена, то вернет defaultValue
	 * 
	 * @param em
	 *            - контекст персистенции, в котором нужно искать сущность
	 * @param defaultValue
	 *            - значение по-умолчанию
	 * 
	 * @return восстановленную сущность или defaultValue, если ссылка не определена
	 */
	public T orElse(EntityManager em, T defaultValue) {
		T entity = get(em);
		return entity != null ? entity : defaultValue;
	}

	/**
	 * Позволяет установить указанную сущность для текущей ссылки-на-сущность. Метод гарантирует, что созданный identity
	 * будет соответствовать типу текущей ссылки
	 * 
	 * @param entity
	 *            - сущность, ссылку на которую мы хотим сохранить
	 */
	public void set(T entity) {
		this.identity = entity != null && entity.getId() != null ? createIdentity(entity) : null;
	}

	private String createIdentity(T entity) {
		checkState(entity != null);
		return new EntityConverter().convertToString(entity);
	}

	private static final long serialVersionUID = 5100842686812943460L;
}