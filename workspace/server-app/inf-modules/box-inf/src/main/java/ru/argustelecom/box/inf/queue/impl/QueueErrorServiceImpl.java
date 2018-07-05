package ru.argustelecom.box.inf.queue.impl;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;
import ru.argustelecom.box.inf.queue.impl.util.QueueUtils;
import ru.argustelecom.box.inf.queue.impl.util.QueueUtils.ErrorInfo;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.logging.Loggable;

@Transactional
@ApplicationScoped
public class QueueErrorServiceImpl {

	@PersistenceContext
	private EntityManager em;

	public void markResolvedInCurrentTx(QueueEventImpl event) throws QueueSystemException {
		try {

			mergeEventError(event.getQueue().getId(), event.getId(), true, false,
					QueueUtils.getAttempsCount(event, false), new Date(), null, null);

		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	@Transactional(TxType.REQUIRES_NEW)
	public void markUnresolvedInAutonomousTx(QueueEventImpl event, Throwable exception) throws QueueSystemException {
		try {

			ErrorInfo error = QueueUtils.getErrorInfo(exception);
			mergeEventError(event.getQueue().getId(), event.getId(), false, false,
					QueueUtils.getAttempsCount(event, true), new Date(), error.errorClass, error.errorText);

		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	public void markPoisonedInCurrentTx(QueueEventImpl event, Throwable exception) throws QueueSystemException {
		doMarkPoisoned(event, exception);
	}

	@Transactional(TxType.REQUIRES_NEW)
	public void markPoisonedInAutonomousTx(QueueEventImpl event, Throwable exception) throws QueueSystemException {
		doMarkPoisoned(event, exception);
	}

	private void doMarkPoisoned(QueueEventImpl event, Throwable exception) throws QueueSystemException {
		try {

			ErrorInfo error = QueueUtils.getErrorInfo(exception);
			mergeEventError(event.getQueue().getId(), event.getId(), false, true,
					QueueUtils.getAttempsCount(event, false), new Date(), error.errorClass, error.errorText);

		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	/**
	 * 
	 * @param queueId
	 * @param eventId
	 * @param resolved
	 * @param poison
	 * @param attemptsCount
	 * @param errorTime
	 * @param errorClass
	 * @param errorText
	 * 
	 *///@formatter:off
	@Loggable
	@NamedNativeQuery(name = MERGE_EVENT_ERROR, query 
			= "INSERT INTO qms.queue_event_error "
			+ "       (queue_id, event_id, resolved, poison, attempts_count, error_time, error_class, error_text) "
			+ "VALUES (:queue_id, :event_id, :resolved, :poison, :attempts_count, :error_time, :error_class, :error_text) "
			+ "ON CONFLICT (event_id) "
			+ "  DO UPDATE SET "
			+ "    resolved       = EXCLUDED.resolved, "
			+ "    poison         = EXCLUDED.poison, "
			+ "    attempts_count = EXCLUDED.attempts_count, "
			+ "    error_time     = EXCLUDED.error_time, "
			+ "    error_class    = EXCLUDED.error_class, "
			+ "    error_text     = EXCLUDED.error_text "
	)///@formatter:on
	private void mergeEventError(String queueId, Long eventId, boolean resolved, boolean poison, int attemptsCount,
			Date errorTime, String errorClass, String errorText) {
		Query query = em.createNamedQuery(MERGE_EVENT_ERROR);
		query.setParameter("queue_id", queueId);
		query.setParameter("event_id", eventId);
		query.setParameter("resolved", resolved);
		query.setParameter("poison", poison);
		query.setParameter("attempts_count", attemptsCount);
		query.setParameter("error_time", errorTime);
		query.setParameter("error_class", errorClass);
		query.setParameter("error_text", errorText);
		query.executeUpdate();
	}

	private static final String MERGE_EVENT_ERROR = "QueueErrorServiceImpl.mergeEventError";
}
