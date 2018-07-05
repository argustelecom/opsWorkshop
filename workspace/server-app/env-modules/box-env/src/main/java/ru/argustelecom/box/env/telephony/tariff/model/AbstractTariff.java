package ru.argustelecom.box.env.telephony.tariff.model;

import lombok.Getter;
import lombok.Setter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.activity.attachment.model.HasAttachments;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.env.telephony.tariff.model.TariffEntry.TariffEntryStatus;
import ru.argustelecom.box.env.telephony.tariff.nls.TariffMessagesBundle;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.exception.BusinessException;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Collections.unmodifiableList;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.telephony.tariff.model.TariffState.FORMALIZATION;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "tariff")
public abstract class AbstractTariff extends BusinessObject implements LifecycleObject<TariffState>,
		HasComments, HasAttachments {

	private static final long serialVersionUID = 4333695282908738858L;

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "name", nullable = false, length = 128)
	public String getObjectName() {
		return super.getObjectName();
	}

	@OneToMany(orphanRemoval = true)
	@JoinColumn(name = "tariff_id")
	private List<TariffEntry> entries = new ArrayList<>();

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private TariffState state;

	@Getter
	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;

	@Getter
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

	@Getter
	@Enumerated(EnumType.STRING)
	private PeriodUnit ratedUnit;

	@Getter
	@Enumerated(EnumType.STRING)
	private RoundingPolicy roundingPolicy;

	@Version
	private Long version;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_context_id", insertable = true, updatable = false)
	private CommentContext commentContext;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_context_id", insertable = true, updatable = false)
	private AttachmentContext attachmentContext;

	@Transient
	private List<TariffEntry> activeEntries;

	protected AbstractTariff() {
	}

	protected AbstractTariff(Long id) {
		super(id);
		this.state = FORMALIZATION;
		this.commentContext = new CommentContext(id);
		this.attachmentContext = new AttachmentContext(id);
	}

	public void setValidFrom(Date validFrom) {
		checkValidDates(validFrom, validTo);
		this.validFrom = validFrom;
	}

	public void setValidTo(Date validTo) {
		checkValidDates(validFrom, validTo);
		this.validTo = validTo;
	}

	public void setRatedUnit(PeriodUnit ratedUnit) {
		checkNotNull(ratedUnit);

		TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);

		if (ratedUnit.equals(PeriodUnit.SECOND) || ratedUnit.equals(PeriodUnit.MINUTE)) {
			this.ratedUnit = ratedUnit;
		} else {
			throw new BusinessException(messages.validRatedUnit());
		}
	}

	public void setRoundingPolicy(RoundingPolicy roundingPolicy) {
		checkNotNull(roundingPolicy);
		this.roundingPolicy = roundingPolicy;
	}

	public boolean hasEntry(TariffEntry entry) {
		checkNotNull(entry);
		return getEntries().contains(entry);
	}

	public List<TariffEntry> getEntries() {
		if (activeEntries == null) {
			activeEntries = unmodifiableList(entries());
		}
		return activeEntries;
	}

	protected List<TariffEntry> entries() {
		return entries.stream().filter(tariffEntry -> TariffEntryStatus.ACTIVE.equals(tariffEntry.getStatus())).collect(toList());
	}

	public boolean addEntry(TariffEntry entry) {
		boolean contains = hasEntry(entry);
		if (!contains) {
			entries.add(entry);
			evictCachedEntries();
		}
		return !contains;
	}

	public boolean removeEntry(TariffEntry entry) {
		evictCachedEntries();
		return entries.remove(entry);
	}

	protected void evictCachedEntries() {
		activeEntries = null;
	}

	private static boolean checkValidDates(Date validFrom, Date validTo) {
		TariffMessagesBundle messages = LocaleUtils.getMessages(TariffMessagesBundle.class);

		if (validFrom == null || validTo == null)
			return false;
		if (validFrom.after(validTo))
			throw new BusinessException(messages.validFromAfterValidTo());
		if (validTo.before(validFrom))
			throw new BusinessException(messages.validToBeforeValidFrom());
		return true;
	}

	@Override
	public AttachmentContext getAttachmentContext() {
		return attachmentContext;
	}

	@Override
	public CommentContext getCommentContext() {
		return commentContext;
	}

	public static class TariffQuery<T extends AbstractTariff> extends EntityQuery<T> {

		private EntityQuerySimpleFilter<T, String> name;
		private EntityQuerySimpleFilter<T, PeriodUnit> ratedUnit;
		private EntityQuerySimpleFilter<T, RoundingPolicy> roundingPolicy;
		private EntityQuerySimpleFilter<T, TariffState> state;
		private EntityQueryDateFilter<T> validFrom;
		private EntityQueryDateFilter<T> validTo;

		public TariffQuery(Class<T> entityClass) {
			super(entityClass);
			name = createFilter(AbstractTariff_.objectName);
			ratedUnit = createFilter(AbstractTariff_.ratedUnit);
			roundingPolicy = createFilter(AbstractTariff_.roundingPolicy);
			state = createFilter(AbstractTariff_.state);
			validFrom = createDateFilter(AbstractTariff_.validFrom);
			validTo = createDateFilter(AbstractTariff_.validTo);
		}

		public EntityQuerySimpleFilter<T, String> name() {
			return name;
		}

		public EntityQuerySimpleFilter<T, PeriodUnit> ratedUnit() {
			return ratedUnit;
		}

		public EntityQuerySimpleFilter<T, RoundingPolicy> roundingPolicy() {
			return roundingPolicy;
		}

		public EntityQuerySimpleFilter<T, TariffState> state() {
			return state;
		}

		public EntityQueryDateFilter<T> validFrom() {
			return validFrom;
		}

		public EntityQueryDateFilter<T> validTo() {
			return validTo;
		}
	}
}
