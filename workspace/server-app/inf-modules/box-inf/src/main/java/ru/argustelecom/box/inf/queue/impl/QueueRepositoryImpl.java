package ru.argustelecom.box.inf.queue.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.FlushModeType;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.transaction.Transactional;

import org.jboss.logging.Logger;

import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueStatus;
import ru.argustelecom.box.inf.queue.impl.context.ContextMapper;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventErrorImpl;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;
import ru.argustelecom.box.inf.queue.impl.model.QueueImpl;
import ru.argustelecom.box.inf.queue.impl.util.QueueUtils;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.modelbase.Identifiable;
import ru.argustelecom.system.inf.utils.converters.EntityConverter;

@Transactional
@ApplicationScoped
public class QueueRepositoryImpl implements QueueProducer, Serializable {

	@PersistenceContext
	private EntityManager em;

	@Inject
	private QueueHistoryServiceImpl historySvc;

	// ********************************************* PUBLIC API ***************************************************

	@Override
	public QueueEvent schedule(Identifiable queueObject, String groupId, Priority priority, Date scheduledTime,
			String handlerName, Context context) {

		checkArgument(queueObject != null);
		String queueId = getQueueId(queueObject);
		return schedule(queueId, groupId, priority, scheduledTime, handlerName, context);
	}

	@Override
	public QueueEvent schedule(String queueId, String groupId, Priority priority, Date scheduledTime,
			String handlerName, Context context) {

		checkArgument(queueId != null);
		checkArgument(!isNullOrEmpty(handlerName));

		QueueImpl queue = createUnboundQueue(queueId, groupId, priority, scheduledTime, QueueStatus.ACTIVE);
		QueueEventImpl event = createUnboundEvent(nextEventId(), scheduledTime, handlerName, context);
		bind(queue, event, null);

		// Если кто-то другой инсертит или удаляет очередь, то нужно подождать, пока он не закончит. В данном случае
		// возможны следующие варианты:
		// 1. очереди еще нет, в таком случае, она будет создана
		// 2. очередь есть и она удаляется другим потоком (при удалении тоже залочена), в таком случае текущий поток
		// будет ждать окончания удаляющего потока. После того, как мы сможем получить блокировку, увидим, что очереди
		// больше нет и сможем создать ее вновь (при помощи merge)
		// 3. очередь есть и она никем не удаляется и не редактируется. В данном случае просто создадим новое событие в
		// этой очереди (если его еще нет) или проигнорируем, если оно уже есть (операция merge)
		//
		// Причем, в данном случае нам не нужно знать, смогли ли мы получить блокировку, нам нужно просто подождать,
		// пока очередь не будет отпущена заблокировавшим ее потоком (т.е. подождать завершения блокирующей транзакции)
		lockQueue(queueId);
		mergeQueueIgnoreConflicts(queue);
		mergeEventIgnoreConflicts(event);

		historySvc.pendingInCurrentTx(event);

		return event;
	}

	@Override
	public void remove(Identifiable queueObject) {
		remove(getQueueId(checkNotNull(queueObject)));
	}

	@Override
	public void remove(String queueId) {
		deleteQueueAndEvents(queueId);
		historySvc.cancelInCurrentTx(queueId);
	}

	@Override
	public boolean restart(Identifiable queueObject) {
		return restartFailedOrInactiveQueue(getQueueId(checkNotNull(queueObject)));
	}

	@Override
	public boolean restart(String queueId) {
		return restartFailedOrInactiveQueue(queueId);
	}

	// ********************************************* PRIVATE API ***************************************************

	/**
	 * 
	 * @return
	 */
	public QueueEvent poll() throws QueueSystemException {
		try {

			return selectNextEventAndLock();

		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void update(QueueEvent event) throws QueueSystemException {
		try {

			String queueId = event.getQueue().getId();

			// Для согласованности обновления очереди разными потоками (транзакциями), перед обновлением очередь
			// необходимо залочить. Если лок повесить удалось, то будем обновлять. Если лок повесить не удалось, то
			// текущая очередь уже кем-то удалена и обновлять просто нечего. Возможно, в этом случае нужно бросить
			// исключение, но не уверен.
			if (lockQueue(queueId)) {
				updateQueue((QueueImpl) event.getQueue());
				updateEvent((QueueEventImpl) event);
			}

		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	/**
	 * 
	 * @param event
	 */
	public void remove(QueueEvent event) throws QueueSystemException {
		try {

			String queueId = event.getQueue().getId();
			Long eventId = event.getId();

			// Удаление самого события происходит моментально, т.к. событие практически никогда никем не блокирутеся
			// (кроме экзекутора, который этот метод и вызывает). А вот для проверки очереди, пустая она или нет,
			// необходимо ее (очередь) залочить, потому что в это же самое время другой поток (транзакция) может в эту
			// очередь добавлять новое событие или также пытаться удалить ее. В случае, если лок повесить не удалось, то
			// очередь уже удалена, т.е. выполнять удаление уже не нужно
			deleteError(eventId);
			deleteEvent(eventId);
			if (lockQueue(queueId)) {
				boolean queueIsEmpty = isQueueEmpty(queueId);
				if (queueIsEmpty) {
					deleteQueue(queueId);
				}
			}

		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	// ****************************************************************************************************************

	/**
	 * 
	 * @param queueObject
	 * @return
	 */
	private String getQueueId(Identifiable queueObject) {
		EntityConverter converter = new EntityConverter(em);
		return converter.convertToString(queueObject);
	}

	/**
	 * 
	 * @return
	 */
	@NamedNativeQuery(name = NEXT_EVENT_ID, query = "SELECT nextval('qms.gen_queue_event_id')")
	private Long nextEventId() {
		Object result = em.createNamedQuery(NEXT_EVENT_ID).setFlushMode(FlushModeType.COMMIT).getSingleResult();
		return QueueUtils.ResultSet.getLong("event_id", result, true);
	}

	private static final String NEXT_EVENT_ID = "QueueRepositoryImpl.nextEventId";

	/**
	 * 
	 * @param queueId
	 * @param scheduledTime
	 * @param status
	 * @return
	 */
	private QueueImpl createUnboundQueue(String queueId, String groupId, Priority priority, Date scheduledTime,
			QueueStatus status) {

		QueueImpl queue = new QueueImpl();
		queue.setId(queueId);
		queue.setGroupId(groupId);
		queue.setPriority(priority);
		queue.setScheduledTime(scheduledTime);
		queue.setStatus(status);
		queue.cleanDirtyState();

		return queue;
	}

	/**
	 * 
	 * @param eventId
	 * @param scheduledTime
	 * @param handlerName
	 * @param context
	 * @return
	 */
	private QueueEventImpl createUnboundEvent(Long eventId, Date scheduledTime, String handlerName, Context context) {
		String marshalledContext = context != null ? ContextMapper.marshall(context) : null;
		return createUnboudEvent(eventId, scheduledTime, handlerName, marshalledContext);
	}

	/**
	 * 
	 * @param eventId
	 * @param scheduledTime
	 * @param handlerName
	 * @param marshalledContext
	 * @return
	 */
	private QueueEventImpl createUnboudEvent(Long eventId, Date scheduledTime, String handlerName,
			String marshalledContext) {

		QueueEventImpl event = new QueueEventImpl();
		event.setId(eventId);
		event.setScheduledTime(scheduledTime);
		event.setHandlerName(handlerName);
		event.setMarshalledContext(marshalledContext);
		event.cleanDirtyState();

		return event;
	}

	/**
	 * 
	 * @param resolved
	 * @param poison
	 * @param attemptsCount
	 * @param errorTime
	 * @param errorClass
	 * @param errorText
	 * @return
	 */
	private QueueEventErrorImpl createUnboundError(boolean resolved, boolean poison, int attemptsCount, Date errorTime,
			String errorClass, String errorText) {

		QueueEventErrorImpl error = new QueueEventErrorImpl();
		error.setResolved(resolved);
		error.setPoison(poison);
		error.setAttemptsCount(attemptsCount);
		error.setErrorTime(errorTime);
		error.setErrorClass(errorClass);
		error.setErrorText(errorText);

		return error;
	}

	/**
	 * 
	 * @param queue
	 * @param event
	 * @return
	 */
	private QueueEventImpl bind(QueueImpl queue, QueueEventImpl event, QueueEventErrorImpl error) {
		event.setQueue(queue);
		queue.setCurrentEvent(event);
		if (error != null) {
			event.setException(error);
			error.setEvent(event);
		}

		event.cleanDirtyState();
		queue.cleanDirtyState();
		return event;
	}

	/**
	 * 
	 * @param queueId
	 * @return
	 */
	@NamedNativeQuery(name = LOCK_QUEUE, query = "SELECT id FROM qms.queue WHERE id = :queue_id FOR UPDATE")
	private boolean lockQueue(String queueId) {
		Query query = em.createNamedQuery(LOCK_QUEUE);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("queue_id", queueId);

		List<?> result = query.getResultList();
		checkState(result.size() <= 1);
		return !result.isEmpty();
	}

	private static final String LOCK_QUEUE = "QueueRepositoryImpl.lockQueue";

	/**
	 * 
	 * @param queue
	 * 
	 *///@formatter:off
	@NamedNativeQuery(name = MERGE_QUEUE_IGNORE_CONFLICTS, query
		= "INSERT INTO qms.queue "
		+ "            (id, group_id, priority, scheduled_time, status) "
		+ "     VALUES (:queue_id, :group_id, :priority, :scheduled_time, :status) "
		+ "ON CONFLICT (id) DO NOTHING"
	)//@formatter:on
	private void mergeQueueIgnoreConflicts(QueueImpl queue) {
		Query query = em.createNamedQuery(MERGE_QUEUE_IGNORE_CONFLICTS);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("queue_id", queue.getId());
		query.setParameter("group_id", queue.getGroupId());
		query.setParameter("priority", queue.getPriority().value());
		query.setParameter("scheduled_time", queue.getScheduledTime());
		query.setParameter("status", queue.getStatus().name());
		query.executeUpdate();
	}

	private static final String MERGE_QUEUE_IGNORE_CONFLICTS = "QueueRepositoryImpl.mergeQueueIgnoreConflicts";

	/**
	 * 
	 * @param event
	 * 
	 *///@formatter:off
	@NamedNativeQuery(name = MERGE_EVENT_IGNORE_CONFLICTS, query
		= "INSERT INTO qms.queue_event "
		+ "            (id, queue_id, scheduled_time, handler_name, context) "
		+ "     VALUES (:event_id, :queue_id, :scheduled_time, :handler_name, cast(coalesce(:context, '{}') AS JSONB)) "
		+ "ON CONFLICT (queue_id, handler_name) DO NOTHING"
	)//@formatter:on
	private void mergeEventIgnoreConflicts(QueueEventImpl event) {
		Query query = em.createNamedQuery(MERGE_EVENT_IGNORE_CONFLICTS);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("event_id", event.getId());
		query.setParameter("queue_id", event.getQueue().getId());
		query.setParameter("scheduled_time", event.getScheduledTime());
		query.setParameter("handler_name", event.getHandlerName());
		query.setParameter("context", event.getMarshalledContext());
		query.executeUpdate();
	}

	private static final String MERGE_EVENT_IGNORE_CONFLICTS = "QueueRepositoryImpl.mergeEventIgnoreConflicts";

	/**
	 * 
	 * @param event
	 * 
	 *///@formatter:off
	@NamedNativeQuery(name = UPDATE_EVENT, query
		= "UPDATE qms.queue_event SET "
		+ "  scheduled_time = :scheduled_time, "
		+ "  context        = cast(coalesce(:context, '{}') AS JSONB) "
		+ "WHERE id = :event_id"
	)//@formatter:on
	private boolean updateEvent(QueueEventImpl event) {
		if (event.isDirty()) {
			Query query = em.createNamedQuery(UPDATE_EVENT);
			query.setFlushMode(FlushModeType.COMMIT);
			query.setParameter("event_id", event.getId());
			query.setParameter("scheduled_time", event.getScheduledTime());
			query.setParameter("context", event.getMarshalledContext());
			return query.executeUpdate() == 1;
		}
		return false;
	}

	private static final String UPDATE_EVENT = "QueueRepositoryImpl.updateEvent";

	/**
	 * 
	 * @param queue
	 * 
	 *///@formatter:off
	@NamedNativeQuery(name = UPDATE_QUEUE, query
		= "UPDATE qms.queue SET "
		+ "  scheduled_time = :scheduled_time, "
		+ "  status         = :status "
		+ "WHERE id = :queue_id"
	)//@formatter:on
	private boolean updateQueue(QueueImpl queue) {
		if (queue.isDirty()) {
			Query query = em.createNamedQuery(UPDATE_QUEUE);
			query.setFlushMode(FlushModeType.COMMIT);
			query.setParameter("queue_id", queue.getId());
			query.setParameter("scheduled_time", queue.getScheduledTime());
			query.setParameter("status", queue.getStatus().name());
			return query.executeUpdate() == 1;
		}
		return false;
	}

	private static final String UPDATE_QUEUE = "QueueRepositoryImpl.updateQueue";

	/**
	 * dfadsfs
	 * 
	 * @return
	 * 
	 *///@formatter:off
	@NamedNativeQuery(name = SELECT_NEXT_EVENT_AND_LOCK, query 
			= "SELECT "
			+ "  q.id                    AS queue_id, "             // 0
			+ "  q.priority              AS queue_priority, "       // 1
			+ "  q.scheduled_time        AS queue_scheduled_time, " // 2
			+ "  q.status                AS queue_status, "         // 3
			+ "  e.id                    AS event_id, "             // 4
			+ "  e.scheduled_time        AS event_scheduled_time, " // 5
			+ "  e.handler_name          AS event_handler_name, "   // 6
			+ "  cast(e.context AS TEXT) AS event_context, "        // 7
			+ "  (CASE COALESCE(er.event_id, -1) WHEN -1 THEN FALSE ELSE TRUE END) AS has_error, " // 8
			+ "  er.resolved             AS error_resolved, "       // 9
			+ "  er.poison               AS error_poison, "         // 10
			+ "  er.attempts_count       AS error_attempts_count, " // 11
			+ "  er.error_time           AS error_time, "           // 12
			+ "  er.error_class          AS error_class, "          // 13
			+ "  er.error_text           AS error_text, "           // 14
			+ "  q.group_id              AS queue_group_id "        // 15
			+ "FROM   qms.queue q "
			+ "  JOIN qms.queue_event e ON q.id = e.queue_id "
			+ "  LEFT JOIN qms.queue_event_error er ON e.id = er.event_id "
			+ "WHERE q.status = 'ACTIVE' AND q.scheduled_time <= :now "
			+"   AND e.id <= qms.nextevent(q.id) AND e.scheduled_time <= :now "
			+ "ORDER BY q.priority, q.status, q.scheduled_time "
			+ "LIMIT 1 "
			+ "FOR UPDATE OF q, e SKIP LOCKED "
	)//@formatter:on
	private QueueEventImpl selectNextEventAndLock() {
		long startTime = System.nanoTime();
		try {
			Query query = em.createNamedQuery(SELECT_NEXT_EVENT_AND_LOCK);
			query.setParameter("now", new Date());
			query.setFlushMode(FlushModeType.COMMIT);
			try {
				Object[] resultSet = (Object[]) query.getSingleResult();
				checkState(resultSet != null && resultSet.length == 16);

				//@formatter:off
				QueueImpl queue = createUnboundQueue(
					QueueUtils.ResultSet.getString("queue_id", resultSet[0], true),
					QueueUtils.ResultSet.getString("queue_group_id", resultSet[15], false),
					QueueUtils.ResultSet.getPriority("queue_priority", resultSet[1], true),
					QueueUtils.ResultSet.getDate("queue_scheduled_time", resultSet[2], true),
					QueueUtils.ResultSet.getStatus("queue_status", resultSet[3], true)
				);
				
				QueueEventImpl event = createUnboudEvent(
					QueueUtils.ResultSet.getLong("event_id", resultSet[4], true), 
					QueueUtils.ResultSet.getDate("event_scheduled_time", resultSet[5], true), 
					QueueUtils.ResultSet.getString("event_handler_name", resultSet[6], true), 
					QueueUtils.ResultSet.getString("event_context", resultSet[7], false)
				);
				
				QueueEventErrorImpl error = null;
				
				boolean hasError = QueueUtils.ResultSet.getBoolean("has_error", resultSet[8], true);
				if (hasError) {
					error = createUnboundError(
						QueueUtils.ResultSet.getBoolean("error_resolved", resultSet[9], true), 
						QueueUtils.ResultSet.getBoolean("error_poison", resultSet[10], true), 
						QueueUtils.ResultSet.getLong("error_attempts_count", resultSet[11], true).intValue(), 
						QueueUtils.ResultSet.getDate("error_time", resultSet[12], true), 
						QueueUtils.ResultSet.getString("error_class", resultSet[13], false), 
						QueueUtils.ResultSet.getString("error_text", resultSet[14], false)
					);
				}
				//@formatter:on

				return bind(queue, event, error);

			} catch (NoResultException e) {
				// Если нет резульатов, то ничего делать не нужно
			}
			return null;
		} finally {
			log.debugv("EVENT POLL TIME: {0} ms", (System.nanoTime() - startTime) / 1000000);
		}
	}

	private static final String SELECT_NEXT_EVENT_AND_LOCK = "QueueRepositoryImpl.selectNextEventAndLock";

	/**
	 * 
	 * @param queueId
	 * @return
	 */
	@NamedNativeQuery(name = IS_QUEUE_EMPTY, query = "SELECT count(*) FROM qms.queue_event WHERE queue_id = :queue_id")
	private boolean isQueueEmpty(String queueId) {
		Query query = em.createNamedQuery(IS_QUEUE_EMPTY);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("queue_id", queueId);
		return QueueUtils.ResultSet.getLong("events_count", query.getSingleResult(), true) == 0;
	}

	private static final String IS_QUEUE_EMPTY = "QueueRepositoryImpl.isQueueEmpty";

	/**
	 * 
	 * @param queueId
	 */
	@NamedNativeQuery(name = DELETE_QUEUE, query = "DELETE FROM qms.queue WHERE id = :queue_id")
	private boolean deleteQueue(String queueId) {
		Query query = em.createNamedQuery(DELETE_QUEUE);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("queue_id", queueId);
		return query.executeUpdate() == 1;
	}

	private static final String DELETE_QUEUE = "QueueRepositoryImpl.deleteQueue";

	@NamedNativeQuery(name = DELETE_ERROR, query = "DELETE FROM qms.queue_event_error WHERE event_id = :event_id")
	private boolean deleteError(Long eventId) {
		Query query = em.createNamedQuery(DELETE_ERROR);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("event_id", eventId);
		return query.executeUpdate() == 1;
	}

	private static final String DELETE_ERROR = "QueueRepositoryImpl.deleteError";

	/**
	 * 
	 * @param eventId
	 * @return
	 */
	@NamedNativeQuery(name = DELETE_EVENT, query = "DELETE FROM qms.queue_event WHERE id = :event_id")
	private boolean deleteEvent(Long eventId) {
		Query query = em.createNamedQuery(DELETE_EVENT);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("event_id", eventId);
		return query.executeUpdate() == 1;
	}

	private static final String DELETE_EVENT = "QueueRepositoryImpl.deleteEvent";

	/**
	 * 
	 * @param queueId
	 */
	private boolean deleteQueueAndEvents(String queueId) {
		// Если удалось получить блокировку на очередь, то будем согласованно удалять ее события и саму очередь
		if (lockQueue(queueId)) {
			// Сначала ошибки и события, чтобы не отвалиться по FK
			deleteErrorsByQueue(queueId);
			deleteEventsByQueue(queueId);
			// Потом саму очередь
			return deleteQueue(queueId);
		}
		return false;
	}

	/**
	 * 
	 * @param queueId
	 * @return
	 */
	@NamedNativeQuery(name = DELETE_ERRORS_BY_QUEUE, query = "DELETE FROM qms.queue_event_error WHERE queue_id = :queue_id")
	private boolean deleteErrorsByQueue(String queueId) {
		Query query = em.createNamedQuery(DELETE_ERRORS_BY_QUEUE);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("queue_id", queueId);
		return query.executeUpdate() != 0;
	}

	private static final String DELETE_ERRORS_BY_QUEUE = "QueueRepositoryImpl.deleteErrorsByQueue";

	/**
	 * 
	 * @param queueId
	 * @return
	 */
	@NamedNativeQuery(name = DELETE_EVENTS_BY_QUEUE, query = "DELETE FROM qms.queue_event WHERE queue_id = :queue_id")
	private boolean deleteEventsByQueue(String queueId) {
		Query query = em.createNamedQuery(DELETE_EVENTS_BY_QUEUE);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("queue_id", queueId);
		return query.executeUpdate() != 0;
	}

	private static final String DELETE_EVENTS_BY_QUEUE = "QueueRepositoryImpl.deleteEventsByQueue";

	/**
	 * 
	 * @param queueId
	 * @return
	 */
	@NamedNativeQuery(name = RESTART_FAILED_OR_INACTIVE_QUEUE, query = "SELECT qms.restart_queue(:queue_id)")
	private boolean restartFailedOrInactiveQueue(String queueId) {
		Query query = em.createNamedQuery(RESTART_FAILED_OR_INACTIVE_QUEUE);
		query.setFlushMode(FlushModeType.COMMIT);
		query.setParameter("queue_id", queueId);
		return QueueUtils.ResultSet.getBoolean("", query.getSingleResult(), false);
	}

	private static final String RESTART_FAILED_OR_INACTIVE_QUEUE = "QueueRepositoryImpl.restartFailedOrInactiveQueue";

	private static final long serialVersionUID = 3638983024734246347L;
	private static final Logger log = Logger.getLogger(QueueRepositoryImpl.class);
}
