package ru.argustelecom.ops.inf.queue.impl;

import java.util.Date;

import javax.enterprise.context.ApplicationScoped;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;
import javax.transaction.Transactional;
import javax.transaction.Transactional.TxType;

import ru.argustelecom.ops.inf.queue.impl.model.QueueEventImpl;
import ru.argustelecom.ops.inf.queue.impl.util.QueueUtils;
import ru.argustelecom.ops.inf.queue.impl.util.QueueUtils.ErrorInfo;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.exception.ExceptionUtils;

@Transactional
@ApplicationScoped
public class QueueHistoryServiceImpl {

	@PersistenceContext
	private EntityManager em;

	public void pendingInCurrentTx(QueueEventImpl event) {
		//@formatter:off
		insertHistoryRecord(
			event.getQueue().getId(),
			event.getQueue().getGroupId(),
			event.getQueue().getStatus().name(),
			event.getId(),
			event.getQueue().getScheduledTime(),
			new Date(),
			event.getHandlerName(),
			event.getMarshalledContext(),
			"PENDING"
		);//@formatter:on
	}

	public void cancelInCurrentTx(String queueId) {
		insertCancelRecord(queueId);
	}

	public void successInCurrentTx(String status, QueueEventImpl event) throws QueueSystemException {
		try {

			//@formatter:off
			insertHistoryRecord(
				event.getQueue().getId(),
				event.getQueue().getGroupId(),
				event.getQueue().getStatus().name(), 
				event.getId(),
				event.getQueue().getScheduledTime(),
				new Date(),
				event.getHandlerName(),
				event.getMarshalledContext(),
				status
			);//@formatter:on

		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	public void errorInCurrentTx(String status, QueueEventImpl event, Throwable error) throws QueueSystemException {
		saveError(status, event, error);
	}

	@Transactional(TxType.REQUIRES_NEW)
	public void errorInAutonomousTx(String status, QueueEventImpl event, Throwable error) throws QueueSystemException {
		saveError(status, event, error);
	}

	// ***************************************************************************************************************

	private void saveError(String status, QueueEventImpl event, Throwable error) throws QueueSystemException {
		try {

			//@formatter:off
			Long entryId = insertHistoryRecord(
				event.getQueue().getId(),
				event.getQueue().getGroupId(),
				event.getQueue().getStatus().name(), 
				event.getId(),
				event.getQueue().getScheduledTime(),
				new Date(),
				event.getHandlerName(),
				event.getMarshalledContext(),
				status
			);
			
			if (error != null) {
				ErrorInfo errorInfo = QueueUtils.getErrorInfo(error);
				String errorStack = ExceptionUtils.getStackTrace(error);
				insertHistoryErrorRecord(entryId, errorInfo.errorClass, errorInfo.errorText, errorStack);
			}//@formatter:on

		} catch (Exception e) {
			throw new QueueSystemException(e);
		}
	}

	//@formatter:off
	@NamedNativeQuery(name = INSERT_HISTORY_RECORD, query
		= "SELECT hid "
		+ "  FROM qms.save_history("
		+ "        :event_id,"
		+ "        :queue_id,"
		+ "        :group_id,"
		+ "        :handler_name,"
		+ "        :context,"
		+ "        :queue_status,"
		+ "        :scheduled_time,"
		+ "        :execution_status,"
		+ "        :execution_time"
		+ "  ) hid"
	)//@formatter:on
	private Long insertHistoryRecord(String queueId, String groupId, String queueStatus, Long eventId,
			Date scheduledTime, Date executionTime, String handlerName, String context, String status) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		Query query = em.createNamedQuery(INSERT_HISTORY_RECORD);
		query.setParameter(cb.parameter(Long.class, "event_id"), eventId);
		query.setParameter(cb.parameter(String.class, "queue_id"), queueId);
		query.setParameter(cb.parameter(String.class, "group_id"), groupId);
		query.setParameter(cb.parameter(String.class, "handler_name"), handlerName);
		query.setParameter(cb.parameter(String.class, "context"), context);
		query.setParameter(cb.parameter(String.class, "queue_status"), queueStatus);
		query.setParameter(cb.parameter(Date.class, "scheduled_time"), scheduledTime);
		query.setParameter(cb.parameter(String.class, "execution_status"), status);
		query.setParameter(cb.parameter(Date.class, "execution_time"), executionTime);

		return QueueUtils.ResultSet.getLong("hid", query.getSingleResult(), true);
	}

	//@formatter:off
	@NamedNativeQuery(name = INSERT_HISTORY_ERROR_RECORD, query
		= "SELECT hid FROM qms.save_history_error(:history_id, :error_class, :error_text, :error_stack) hid"
	)//@formatter:on
	private void insertHistoryErrorRecord(Long historyId, String errorClass, String errorText, String errorStack) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Query query = em.createNamedQuery(INSERT_HISTORY_ERROR_RECORD);
		query.setParameter(cb.parameter(Long.class, "history_id"), historyId);
		query.setParameter(cb.parameter(String.class, "error_class"), errorClass);
		query.setParameter(cb.parameter(String.class, "error_text"), errorText);
		query.setParameter(cb.parameter(String.class, "error_stack"), errorStack);
		query.getSingleResult();
	}

	//@formatter:off
	@NamedNativeQuery(name = INSERT_CANCEL_RECORD, query
		= "SELECT result FROM qms.save_history_cancel(:queue_id, :execution_time) result"
	)//@formatter:on
	private void insertCancelRecord(String queueId) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		Query query = em.createNamedQuery(INSERT_CANCEL_RECORD);
		query.setParameter(cb.parameter(String.class, "queue_id"), queueId);
		query.setParameter(cb.parameter(Date.class, "execution_time"), new Date());
		query.getSingleResult();
	}

	private static final String INSERT_HISTORY_RECORD = "QueueHistoryServiceImpl.insertHistoryRecord";
	private static final String INSERT_HISTORY_ERROR_RECORD = "QueueHistoryServiceImpl.insertHistoryErrorRecord";
	private static final String INSERT_CANCEL_RECORD = "QueueHistoryServiceImpl.insertCancelRecord";
}
