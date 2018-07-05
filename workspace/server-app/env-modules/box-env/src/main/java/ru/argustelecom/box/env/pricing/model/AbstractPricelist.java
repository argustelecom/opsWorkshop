package ru.argustelecom.box.env.pricing.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Version;

import lombok.Getter;
import ru.argustelecom.box.env.activity.attachment.model.AttachmentContext;
import ru.argustelecom.box.env.activity.attachment.model.HasAttachments;
import ru.argustelecom.box.env.activity.comment.model.CommentContext;
import ru.argustelecom.box.env.activity.comment.model.HasComments;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.party.model.role.Owner;
import ru.argustelecom.box.env.pricing.nls.PricelistMessagesBundle;
import ru.argustelecom.box.env.util.QueryWrapper;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryStringFilter;
import ru.argustelecom.system.inf.exception.BusinessException;

/**
 * Базовый класс для прайс-листов. Прайс-лист - документ, содержащий перечень цен на предоставляемые компанией продукты.
 */
@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "pricelist")
public abstract class AbstractPricelist extends BusinessObject
		implements LifecycleObject<PricelistState>, HasComments, HasAttachments {

	@Enumerated(EnumType.STRING)
	private PricelistState state;

	@Temporal(TemporalType.TIMESTAMP)
	private Date validFrom;

	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "comment_context_id", insertable = true, updatable = false)
	private CommentContext commentContext;

	@OneToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
	@JoinColumn(name = "attachment_context_id", insertable = true, updatable = false)
	private AttachmentContext attachmentContext;

	/**
	 * Владелец прайс-листа
	 */
	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "owner_id", nullable = false)
	private Owner owner;

	@Version
	private Long version;

	protected AbstractPricelist() {
	}

	protected AbstractPricelist(Long id, Owner owner) {
		super(id);
		this.owner = checkNotNull(owner);
		this.commentContext = new CommentContext(id);
		this.attachmentContext = new AttachmentContext(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@Column(name = "name", nullable = false, length = 128)
	public String getObjectName() {
		return super.getObjectName();
	}

	public static boolean checkValidDates(Date validFrom, Date validTo) {
		PricelistMessagesBundle messages = LocaleUtils.getMessages(PricelistMessagesBundle.class);

		if (validFrom == null || validTo == null)
			return false;
		if (validFrom.after(validTo))
			throw new BusinessException(messages.validFromAfterValidTo());
		if (validTo.before(validFrom))
			throw new BusinessException(messages.validToBeforeValidFrom());
		return true;
	}

	@Override
	public PricelistState getState() {
		return state;
	}

	@Override
	public void setState(PricelistState state) {
		this.state = state;
	}

	public Date getValidFrom() {
		return validFrom;
	}

	public void setValidFrom(Date validFrom) {
		checkValidDates(validFrom, validTo);
		this.validFrom = validFrom;
	}

	public Date getValidTo() {
		return validTo;
	}

	public void setValidTo(Date validTo) {
		checkValidDates(validFrom, validTo);
		this.validTo = validTo;
	}

	public void setOwner(Owner owner) {
		checkNotNull(owner);
		this.owner = owner;
	}

	public BigDecimal getTaxMultiplier() {
		return owner.getTaxRate().divide(BigDecimal.valueOf(100L));
	}

	public boolean isSuitableForCustomer(Customer customer) {
		return false;
	}

	@Override
	public AttachmentContext getAttachmentContext() {
		return attachmentContext;
	}

	@Override
	public CommentContext getCommentContext() {
		return commentContext;
	}

	private static final long serialVersionUID = -8043243706332545780L;

	// *****************************************************************************************************************
	// Inner classes
	// *****************************************************************************************************************

	public static class PricelistQuery<T extends AbstractPricelist> extends EntityQuery<T> {

		private EntityQueryStringFilter<T> name;
		private EntityQuerySimpleFilter<T, PricelistState> state;
		private EntityQueryDateFilter<T> validFrom;
		private EntityQueryDateFilter<T> validTo;

		{
			name = createStringFilter(AbstractPricelist_.objectName);
			state = createFilter(AbstractPricelist_.state);
			validFrom = createDateFilter(AbstractPricelist_.validFrom);
			validTo = createDateFilter(AbstractPricelist_.validTo);
		}

		public PricelistQuery(Class<T> entityClass) {
			super(entityClass);
		}

		public EntityQueryStringFilter<T> name() {
			return name;
		}

		public EntityQuerySimpleFilter<T, PricelistState> state() {
			return state;
		}

		public EntityQueryDateFilter<T> validFrom() {
			return validFrom;
		}

		public EntityQueryDateFilter<T> validTo() {
			return validTo;
		}

	}

	public static class PricelistQueryWrapper extends QueryWrapper<AbstractPricelist> {

		public static final String ID = "pl.id";
		public static final String OBJECT_NAME = "pl.objectName";
		public static final String STATE = "pl.state";
		public static final String VALID_TO = "pl.validTo";
		public static final String VALID_FROM = "pl.validFrom";
		public static final String SEGMENT = "pl.customerSegments";
		public static final String SORT_NAME = "p.sortName";
		public static final String CUSTOMER = "pl.customer";
		public static final String DTYPE = "pl.class";
		public static final String OWNER = "pl.owner";

		private static final String SELECT = "pl";
		private static final String FROM = "AbstractPricelist pl left join pl.customer c left join c.party p";

		public PricelistQueryWrapper() {
			super(AbstractPricelist.class, SELECT, FROM);
		}
	}

}