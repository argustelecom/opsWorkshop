package ru.argustelecom.box.inf.queue.impl.request;

import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PreDestroy;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import org.jboss.logging.Logger;
import org.jboss.weld.context.bound.BoundRequestContext;

import ru.argustelecom.box.inf.queue.impl.QueueSystemException;

@Dependent
public class QueueRequest {

	@Inject
	private BoundRequestContext requestContext;

	private QueueRequestDatastore datastore;
	private AtomicBoolean active = new AtomicBoolean(false);

	@PreDestroy
	protected void preDestroy() {
		if (active.get()) {
			log.warn("QueueRequest активен во время уничтожения CDI враппера");
			try {
				doEnd();
			} catch (Exception e) {
				log.error("Ошибка во время деактивации QueueRequest", e);
			}
		}
		cleanDatastore();
	}

	public void begin() throws QueueSystemException {
		if (active.get()) {
			log.warn("QueueRequest уже активен. Повторная активация не требуется");
			return;
		}

		try {
			doBegin();
		} catch (Exception e) {
			log.error("Ошибка во время активации QueueRequest. Попытка деактивировать реквест...", e);
			try {
				doEnd();
			} catch (Exception emergencyEndException) {
				log.error("Ошибка во время деактивации QueueRequest", emergencyEndException);
			}
			throw new QueueSystemException(e);
		}
	}

	public void end() throws QueueSystemException {
		if (!active.get()) {
			log.warn("QueueRequest уже деактивирован. Повторная деактивация не требуется");
			return;
		}

		try {
			doEnd();
		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	private void doBegin() throws Exception {
		datastore = new QueueRequestDatastore();
		requestContext.associate(datastore);
		requestContext.activate();
		active.set(true);
		log.debug("QueueRequest успешно активирован");
	}

	private void doEnd() {
		active.set(false);
		try {
			requestContext.invalidate();
			requestContext.deactivate();
		} finally {
			requestContext.dissociate(datastore);
			cleanDatastore();
		}
		log.debug("QueueRequest успешно деактивирован");
	}

	private void cleanDatastore() {
		if (datastore != null) {
			datastore.clear();
			datastore = null;
		}
	}

	private static final Logger log = Logger.getLogger(QueueRequest.class);
}
