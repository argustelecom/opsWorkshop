package ru.argustelecom.box.env.billing.invoice.model;

import static com.google.common.collect.Maps.newEnumMap;
import static java.util.Collections.unmodifiableMap;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobLifecycleQualifier.FULL;
import static ru.argustelecom.box.env.billing.invoice.model.ChargeJobLifecycleQualifier.SHORT;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.REGULAR;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.SUITABLE;
import static ru.argustelecom.box.env.billing.invoice.model.JobDataType.UNSUITABLE;

import java.util.Date;
import java.util.Map;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.AttributeOverride;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.billing.bill.model.DataMapper;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;
import ru.argustelecom.box.publang.billing.model.IChargeJob;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

@Entity
@Access(AccessType.FIELD)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AttributeOverride(name = "filterAsJson", column = @Column(name = "filter"))
@Table(schema = "system", name = "charge_job")
@EntityWrapperDef(name = IChargeJob.WRAPPER_NAME)
public class ChargeJob extends BusinessObject implements LifecycleObject<ChargeJobState>, HasComments {

	private static final long serialVersionUID = -8871379581072635332L;

	private static final Map<JobDataType, ChargeJobLifecycleQualifier> JOB_DATA_TYPE_LIFECYCLE_QUALIFIER_MAP;

	static {
		Map<JobDataType, ChargeJobLifecycleQualifier> map = newEnumMap(JobDataType.class);
		map.put(REGULAR, SHORT);
		map.put(SUITABLE, FULL);
		map.put(UNSUITABLE, FULL);
		JOB_DATA_TYPE_LIFECYCLE_QUALIFIER_MAP = unmodifiableMap(map);
	}

	private String filterAsJson;

	@Getter
	@Column
	private String mediationId;

	@Getter
	@Enumerated(EnumType.STRING)
	private JobDataType dataType;

	@Transient
	private FilterAggData filter;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private ChargeJobState state;

	@Getter
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_context_id", updatable = false)
	private CommentContext commentContext;

	@Version
	private Long version;

	public ChargeJob(Long id, String mediationId, JobDataType dataType, Date creationDate) {
		super(id);
		this.mediationId = mediationId;
		this.dataType = dataType;
		this.commentContext = new CommentContext(id);
		this.creationDate = creationDate;
		this.state = getLifecycleQualifier().getStartState();
	}

	public void setFilter(FilterAggData filter) {
		this.filter = filter;
		filterAsJson = DataMapper.marshal(filter);
	}

	public FilterAggData getFilter() {
		if (filter == null) {
			filter = DataMapper.unmarshal(filterAsJson, FilterAggData.class);
		}
		return filter;
	}

	@Override
	public CommentContext getCommentContext() {
		return commentContext;
	}

	@Override
	public ChargeJobLifecycleQualifier getLifecycleQualifier() {
		return JOB_DATA_TYPE_LIFECYCLE_QUALIFIER_MAP.get(dataType);
	}

	public static class ChargeJobQuery extends EntityQuery<ChargeJob> {

		private EntityQuerySimpleFilter<ChargeJob, String> mediationId;
		private EntityQuerySimpleFilter<ChargeJob, JobDataType> dataType;
		private EntityQuerySimpleFilter<ChargeJob, ChargeJobState> state;
		private EntityQueryDateFilter<ChargeJob> creationDate;

		public ChargeJobQuery() {
			super(ChargeJob.class);
			mediationId = createFilter(ChargeJob_.mediationId);
			dataType = createFilter(ChargeJob_.dataType);
			state = createFilter(ChargeJob_.state);
			creationDate = createDateFilter(ChargeJob_.creationDate);
		}

		public EntityQuerySimpleFilter<ChargeJob, String> mediationId() {
			return mediationId;
		}

		public EntityQuerySimpleFilter<ChargeJob, JobDataType> dataType() {
			return dataType;
		}

		public EntityQuerySimpleFilter<ChargeJob, ChargeJobState> state() {
			return state;
		}

		public EntityQueryDateFilter<ChargeJob> creationDate() {
			return creationDate;
		}
	}
}
