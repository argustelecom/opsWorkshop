package ru.argustelecom.ops.inf.queue.impl.request;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jboss.logging.Logger;

public class QueueRequestDatastore extends ConcurrentHashMap<String, Object> {

	@Override
	public Object put(String key, Object value) {
		log.debugv(ADD_TO_DATASTORE_MSG, key, value);
		return super.put(key, value);
	}

	@Override
	public void putAll(Map<? extends String, ? extends Object> m) {
		m.forEach((k, v) -> log.debugv(ADD_TO_DATASTORE_MSG, k, v));
		super.putAll(m);
	}

	@Override
	public Object putIfAbsent(String key, Object value) {
		log.debugv(ADD_TO_DATASTORE_MSG, key, value);
		return super.putIfAbsent(key, value);
	}

	@Override
	public Object remove(Object key) {
		log.debugv(REMOVE_FROM_DATASTORE_MSG, key);
		return super.remove(key);
	}

	@Override
	public boolean remove(Object key, Object value) {
		log.debugv(REMOVE_FROM_DATASTORE_MSG, key);
		return super.remove(key, value);
	}

	@Override
	public void clear() {
		log.debug(CLEAN_DATASTORE_MSG);
		super.clear();
	}

	private static final String ADD_TO_DATASTORE_MSG = "Объект {0}:{1} добавляется в хранилище данных реквеста";
	private static final String REMOVE_FROM_DATASTORE_MSG = "Объект с ключом {0} удаляется из хранилища данных реквеста";
	private static final String CLEAN_DATASTORE_MSG = "Вызван метод очистки хранилища данных реквеста";

	private static final Logger log = Logger.getLogger(QueueRequestDatastore.class);
	private static final long serialVersionUID = -5956761059051506694L;
}
