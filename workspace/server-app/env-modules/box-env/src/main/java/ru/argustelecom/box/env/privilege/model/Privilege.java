package ru.argustelecom.box.env.privilege.model;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.system.inf.chrono.DateUtils.DATETIME_DEFAULT_PATTERN;
import static ru.argustelecom.system.inf.chrono.DateUtils.format;
import static ru.argustelecom.system.inf.chrono.TZ.getServerTimeZone;

import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPeriodModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPriceModifier;
import ru.argustelecom.box.env.privilege.nls.PrivilegeMessagesBundle;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
public class Privilege extends BusinessObject implements InvoicePlanPeriodModifier, InvoicePlanPriceModifier {

	@Getter
	@Enumerated(EnumType.STRING)
	@Column(name = "type_id", nullable = false)
	private PrivilegeType type;

	@Getter(onMethod = @__({ @Override }))
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	private Date validFrom;

	@Getter(onMethod = @__({ @Override }))
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date validTo;

	protected Privilege() {
	}

	public Privilege(Long id, PrivilegeType type, Date validFrom, Date validTo) {
		super(id);
		this.type = type;
		this.validFrom = validFrom;
		this.validTo = validTo;
	}

	@Override
	public int getPriority() {
		return -1;
	}

	@Override
	public String getObjectName() {
		PrivilegeMessagesBundle messages = LocaleUtils.getMessages(PrivilegeMessagesBundle.class);

		String lowerBound = format(validFrom, DATETIME_DEFAULT_PATTERN, getServerTimeZone());
		String upperBound = format(validTo, DATETIME_DEFAULT_PATTERN, getServerTimeZone());

		switch (getType()) {
		case TRUST_PERIOD:
			return messages.trustPeriodFromTo(lowerBound, upperBound);
		case TRIAL_PERIOD:
			return messages.trialPeriodFromTo(lowerBound, upperBound);
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public BigDecimal getPriceFactor() {
		switch (getType()) {
		case TRUST_PERIOD:
			return BigDecimal.ONE;
		case TRIAL_PERIOD:
			return BigDecimal.ZERO;
		default:
			throw new IllegalStateException();
		}
	}

	@Override
	public boolean trustOnBalanceChecking() {
		return getType() == PrivilegeType.TRUST_PERIOD;
	}

	@Override
	public void attachTo(LongTermInvoice invoice) {
		invoice.unsafeSetPrivilege(this);
	}

	@Override
	public void detachFrom(LongTermInvoice invoice) {
		invoice.unsafeSetPrivilege(null);
	}

	public boolean isActive(Date poi) {
		return !validFrom.after(poi) && validTo.after(poi);
	}

	public void extend(Date newValidTo) {
		checkState(isActive(new Date()));
		checkState(newValidTo.after(getValidTo()));

		validTo = newValidTo;
	}

	public void close() {
		checkState(isActive(new Date()));

		validTo = new Date();
	}

	public static class PrivilegeQuery<T extends Privilege> extends EntityQuery<T> {
		private EntityQuerySimpleFilter<T, PrivilegeType> type;
		private EntityQueryDateFilter<T> validFrom;
		private EntityQueryDateFilter<T> validTo;

		public PrivilegeQuery(Class<T> entityClass) {
			super(entityClass);
			type = createFilter(Privilege_.type);
			validFrom = createDateFilter(Privilege_.validFrom);
			validTo = createDateFilter(Privilege_.validTo);
		}

		public EntityQuerySimpleFilter<T, PrivilegeType> type() {
			return type;
		}

		public EntityQueryDateFilter<T> validFrom() {
			return validFrom;
		}

		public EntityQueryDateFilter<T> validTo() {
			return validTo;
		}

	}

	private static final long serialVersionUID = -7475575287553033655L;

}