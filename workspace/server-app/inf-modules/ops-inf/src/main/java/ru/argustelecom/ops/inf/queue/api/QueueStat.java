package ru.argustelecom.ops.inf.queue.api;

import static com.google.common.base.Preconditions.checkArgument;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.CriteriaBuilder;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import ru.argustelecom.ops.inf.queue.impl.util.QueueUtils;
import ru.argustelecom.ops.inf.service.ApplicationService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

/**
 * FIXME Не стал делать интерфейс и реализацию, т.к. Queue будет переделана в ближайшее время
 */
@ApplicationService
public class QueueStat implements Serializable {

	private static final long serialVersionUID = -7370357333358251244L;
	private static final String QN_GATHER_STATISTICS = "QueueStat.gather";

	@PersistenceContext
	private EntityManager em;

	public Statistic gatherByQueue(String queueId) {
		return gather(queueId, null);
	}

	public Statistic gatherByGroup(String groupId) {
		return gather(null, groupId);
	}

	// @formatter:off
	@NamedNativeQuery(name = QN_GATHER_STATISTICS, query
		= "  SELECT queue_status, "
		+ "         count(*) "
		+ "    FROM qms.get_last_history(:queue_id, :group_id, NULL)"
		+ "GROUP BY queue_status"
	)
	// @formatter:on
	private Statistic gather(String queueId, String groupId) {
		checkArgument(isNotBlank(queueId) || isNotBlank(groupId));

		CriteriaBuilder cb = em.getCriteriaBuilder();
		Query query = em.createNamedQuery(QN_GATHER_STATISTICS);
		query.setParameter(cb.parameter(String.class, "queue_id"), queueId);
		query.setParameter(cb.parameter(String.class, "group_id"), groupId);

		Map<HistoryStatus, Long> result = new HashMap<>();
		List<?> queryResult = query.getResultList();
		for (Object row : queryResult) {
			String status = QueueUtils.ResultSet.getString("status", ((Object[]) row)[0], true);
			Long count = QueueUtils.ResultSet.getLong("count", ((Object[]) row)[1], true);
			result.put(HistoryStatus.valueOf(status), count);
		}

		return new Statistic(isNotBlank(queueId) ? queueId : groupId, result);
	}

	public enum HistoryStatus {
		PENDING, SUSPENDED, COMPLETED, CANCELLED, FAILED;
	}

	@EqualsAndHashCode
	@ToString(of = "container")
	public static class Statistic {

		private final String groupingCriterion;
		private final Map<HistoryStatus, Long> container;

		Statistic(String groupingCriterion, Map<HistoryStatus, Long> container) {
			this.groupingCriterion = groupingCriterion;
			this.container = container;
		}

		public String getGroupingCriterion() {
			return groupingCriterion;
		}

		public boolean isCompleted() {
			return count(HistoryStatus.PENDING) == 0;
		}

		public boolean hasErrors() {
			return count(HistoryStatus.FAILED) > 0;
		}

		public long count(HistoryStatus status) {
			return container.getOrDefault(status, 0L);
		}

		public void forEach(BiConsumer<? super HistoryStatus, ? super Long> consumer) {
			container.forEach(consumer);
		}

		public Map<HistoryStatus, Long> toMap() {
			return Collections.unmodifiableMap(container);
		}
	}
}
