package ru.argustelecom.box.env.lifecycle.api.history.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;

/**
 * Элемент истории жизненного цикла некоторого бизнес-объекта
 */
@Entity
@Table(schema = "system", name = "lifecycle_history")
@Access(AccessType.FIELD)
@Getter
public class LifecycleHistoryItem extends BusinessObject {

	@Column(nullable = false, updatable = false)
	private Long lifecycleObjectId;

	@Column(nullable = false, updatable = false)
	private String lifecycleObjectEntity;

	@Column(nullable = false, updatable = false)
	private String lifecycle;

	@Column(nullable = false, updatable = false)
	private String fromState;

	@Column(nullable = false, updatable = false)
	private String toState;

	@Temporal(TemporalType.TIMESTAMP)
	private Date transitionTime;

	@Embedded
	@AttributeOverrides({ @AttributeOverride(name = "id", column = @Column(name = "initiator_id")),
			@AttributeOverride(name = "type", column = @Column(name = "initiator_type")),
			@AttributeOverride(name = "name", column = @Column(name = "initiator_name")) })
	private Initiator initiator;

	protected LifecycleHistoryItem() {
	}

	@Builder
	public LifecycleHistoryItem(Long id, Long lifecycleObjectId, String lifecycleObjectEntity, String lifecycle,
			String fromState, String toState, Date transitionTime, Initiator initiator) {
		super(id);
		this.lifecycleObjectId = lifecycleObjectId;
		this.lifecycleObjectEntity = lifecycleObjectEntity;
		this.lifecycle = lifecycle;
		this.fromState = fromState;
		this.toState = toState;
		this.transitionTime = transitionTime;
		this.initiator = initiator;
	}

	public static class LifecycleHistoryItemQuery extends EntityQuery<LifecycleHistoryItem> {

		private EntityQueryNumericFilter<LifecycleHistoryItem, Long> lifecycleObjectId;
		private EntityQueryStringFilter<LifecycleHistoryItem> lifecycleObjectEntity;
		private EntityQueryStringFilter<LifecycleHistoryItem> lifecycle;
		private EntityQueryStringFilter<LifecycleHistoryItem> fromState;
		private EntityQueryStringFilter<LifecycleHistoryItem> toState;
		private EntityQueryDateFilter<LifecycleHistoryItem> transitionTime;

		{
			lifecycleObjectId = createNumericFilter(LifecycleHistoryItem_.lifecycleObjectId);
			lifecycleObjectEntity = createStringFilter(LifecycleHistoryItem_.lifecycleObjectEntity);
			lifecycle = createStringFilter(LifecycleHistoryItem_.lifecycle);
			fromState = createStringFilter(LifecycleHistoryItem_.fromState);
			toState = createStringFilter(LifecycleHistoryItem_.toState);
			transitionTime = createDateFilter(LifecycleHistoryItem_.transitionTime);
		}

		public LifecycleHistoryItemQuery() {
			super(LifecycleHistoryItem.class);
		}

		public EntityQueryNumericFilter<LifecycleHistoryItem, Long> lifecycleObjectId() {
			return lifecycleObjectId;
		}

		public EntityQueryStringFilter<LifecycleHistoryItem> lifecycleObjectEntity() {
			return lifecycleObjectEntity;
		}

		public EntityQueryStringFilter<LifecycleHistoryItem> lifecycle() {
			return lifecycle;
		}

		public EntityQueryStringFilter<LifecycleHistoryItem> fromState() {
			return fromState;
		}

		public EntityQueryStringFilter<LifecycleHistoryItem> toState() {
			return toState;
		}

		public EntityQueryDateFilter<LifecycleHistoryItem> transitionTime() {
			return transitionTime;
		}

	}

	private static final long serialVersionUID = 5588535308466141548L;

}