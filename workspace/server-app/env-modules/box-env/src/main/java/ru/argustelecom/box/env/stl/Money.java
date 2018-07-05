package ru.argustelecom.box.env.stl;

import static com.google.common.base.Preconditions.checkArgument;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.EqualsAndHashCode;

/**
 * Сумма денег
 * <p>
 *
 */
@Embeddable
@EqualsAndHashCode(of = { "amount" })
@Access(AccessType.FIELD)
public class Money implements Serializable, Comparable<Money> {

	private static final int SCALE = 12;
	private static final int ROUNDING_SCALE = 2;
	public static final Money ZERO;

	static {
		ZERO = new Money(BigDecimal.ZERO);
	}

	@Column(precision = 19, scale = 12)
	private BigDecimal amount;

	public Money() {
	}

	public Money(BigDecimal amount) {
		checkArgument(amount != null);
		this.amount = amount;
	}

	public Money(String amount) {
		checkArgument(amount != null);
		this.amount = new BigDecimal(amount);
	}

	public Money add(Money term) {
		checkArgument(term != null);
		return new Money(amount.add(term.amount));
	}

	public Money subtract(Money subtrahend) {
		checkArgument(subtrahend != null);
		return new Money(amount.subtract(subtrahend.amount));
	}

	public Money multiply(BigDecimal multiplicand) {
		checkArgument(multiplicand != null);
		return new Money(amount.multiply(multiplicand));
	}

	public Money multiply(long multiplicand) {
		return new Money(amount.multiply(new BigDecimal(multiplicand)));
	}

	public Money divide(BigDecimal divisor) {
		checkArgument(divisor != null);
		return new Money(amount.divide(divisor, SCALE, RoundingMode.HALF_EVEN));
	}

	public Money divide(long divisor) {
		checkArgument(divisor != 0);
		return new Money(amount.divide(new BigDecimal(divisor), SCALE, RoundingMode.HALF_EVEN));
	}

	public Money negate() {
		return new Money(amount.negate());
	}

	public Money abs() {
		return new Money(amount.abs());
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public BigDecimal getRoundAmount() {
		return amount.setScale(ROUNDING_SCALE, RoundingMode.HALF_EVEN);
	}

	public boolean isNegative() {
		return this.compareTo(ZERO) < 0;
	}

	public boolean isNonPositive() {
		return this.compareTo(ZERO) <= 0;
	}

	public boolean isZero() {
		return this.compareTo(ZERO) == 0;
	}

	public boolean isPositive() {
		return this.compareTo(ZERO) > 0;
	}

	public boolean isNonNegative() {
		return this.compareTo(ZERO) >= 0;
	}

	@Override
	public String toString() {
		return getRoundAmount().toString();
	}

	@Override
	public int compareTo(Money that) {
		return amount.compareTo(that.amount);
	}

	public int compareRounded(Money that) {
		BigDecimal thisRoundedAmount = this.getRoundAmount();
		BigDecimal thatRoundedAmount = that.getRoundAmount();
		return thisRoundedAmount.compareTo(thatRoundedAmount);
	}

	private static final long serialVersionUID = 1075588630095133310L;
}
