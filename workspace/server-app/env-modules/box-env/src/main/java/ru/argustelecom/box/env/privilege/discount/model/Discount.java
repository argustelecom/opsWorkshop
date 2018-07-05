package ru.argustelecom.box.env.privilege.discount.model;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.persistence.Version;

import lombok.Builder;
import lombok.Getter;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPriceModifier;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryNumericFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
public class Discount extends BusinessObject implements InvoicePlanPriceModifier {

	private static final long serialVersionUID = 5665470447842149791L;

	@Getter(onMethod = @__(@Override))
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false)
	private Date validFrom;

	@Getter(onMethod = @__(@Override))
	@Temporal(TemporalType.TIMESTAMP)
	private Date validTo;

	@Getter
	@Column(precision = 5, scale = 2)
	private BigDecimal rate;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "subscription_id", updatable = false)
	private Subscription subscription;

	@Getter
	@Temporal(TemporalType.TIMESTAMP)
	private Date creationDate;

	@Transient
	private BigDecimal priceFactor;

	@Version
	@Temporal(TemporalType.TIMESTAMP)
	private Date version;

	protected Discount() {
	}

	@Builder
	protected Discount(Long id, Date validFrom, Date validTo, Subscription subscription, BigDecimal rate) {
		super(id);

		checkRequiredArgument(validFrom, "validFrom");
		checkRequiredArgument(validTo, "validTo");
		checkRequiredArgument(subscription, "subscription");
		checkRate(rate);

		checkArgument(validFrom.before(validTo) || validFrom.equals(validTo),
				"validFrom should be less or equals then validTo");

		this.validFrom = fromLocalDateTime(
				toLocalDateTime(validFrom).withHour(0).withMinute(0).withSecond(0).withNano(0));
		this.validTo = fromLocalDateTime(
				toLocalDateTime(validTo).withHour(23).withMinute(59).withSecond(59).withNano(999_000_000));

		this.subscription = subscription;
		this.rate = rate;
		this.creationDate = new Date();
	}

	public static void checkRate(BigDecimal rate) {
		checkRequiredArgument(rate, "rate");
		checkArgument(rate.compareTo(BigDecimal.ZERO) >= 0 && rate.compareTo(BigDecimal.valueOf(100)) <= 0,
				"rate should be in range [0, 100]");
	}

	public boolean isActual(Date poi) {
		return !validFrom.after(poi) && validTo.after(poi);
	}

	public void setRate(BigDecimal rate) {
		// после изменения ставки нужно сбрасывать фактор BOX-2551
		if (!Objects.equals(this.rate, rate)) {
			this.rate = rate;
			priceFactor = null;
		}
	}

	@Override
	public String getObjectName() {
		return "Скидка " + rate + "%";
	}

	@Override
	public BigDecimal getPriceFactor() {
		if (this.priceFactor == null) {
			BigDecimal k = this.rate.divide(BigDecimal.valueOf(100), 5, RoundingMode.HALF_EVEN);
			this.priceFactor = BigDecimal.ONE.subtract(k);
		}
		return priceFactor;
	}

	@Override
	public void attachTo(LongTermInvoice invoice) {
		invoice.unsafeAddDiscount(this);
	}

	@Override
	public void detachFrom(LongTermInvoice invoice) {
		invoice.unsafeRemoveDiscount(this);
	}

	public void setValidTo(Date validTo) {
		this.validTo = fromLocalDateTime(
				toLocalDateTime(validTo).withHour(23).withMinute(59).withSecond(59).withNano(999_000_000));
	}

	public static class DiscountQuery extends EntityQuery<Discount> {

		private EntityQueryDateFilter<Discount> validFrom;
		private EntityQueryDateFilter<Discount> validTo;
		private EntityQueryEntityFilter<Discount, Subscription> subscription;
		private EntityQueryDateFilter<Discount> creationDate;
		private EntityQueryNumericFilter<Discount, BigDecimal> rate;

		public DiscountQuery() {
			super(Discount.class);
			validFrom = createDateFilter(Discount_.validFrom);
			validTo = createDateFilter(Discount_.validTo);
			subscription = createEntityFilter(Discount_.subscription);
			creationDate = createDateFilter(Discount_.creationDate);
			rate = createNumericFilter(Discount_.rate);
		}

		public EntityQueryDateFilter<Discount> validFrom() {
			return validFrom;
		}

		public EntityQueryDateFilter<Discount> validTo() {
			return validTo;
		}

		public EntityQueryEntityFilter<Discount, Subscription> subscription() {
			return subscription;
		}

		public EntityQueryDateFilter<Discount> creationDate() {
			return creationDate;
		}

		public EntityQueryNumericFilter<Discount, BigDecimal> rate() {
			return rate;
		}
	}

}
