package ru.argustelecom.box.env.billing.bill.model;

import java.util.Date;

import com.google.common.collect.Range;

import lombok.Getter;

/**
 * Враппер типа аналитики на период, за который эту аналитику надо посчитать.<br/>
 * <b>Могут быть случаи</b>, когда счёт выставляется за текущий период, но для него расчитывается аналитика по правилу
 * {@linkplain ru.argustelecom.box.env.billing.bill.model.BillDateGetter#NEXT_PERIOD_INCOMES_BEFORE_BILL_CREATION_DATE
 * за следующий период, до даты создания квитанции} (такую аналитику можно посчитать только при выставлении счёта, за
 * прошлый период). В этом случае {@link #boundaries} не будет создан и эта аналитика будет добавлена с нулевым
 * значением суммы.
 */
@Getter
public class Analytic {

	private BillAnalyticType type;
	private Date startDate;
	private Date endDate;
	private Date date;
	private Range<Date> boundaries;

	public Analytic(BillAnalyticType type, Date startDate, Date endDate, Date date) {
		this.type = type;
		this.startDate = startDate;
		this.endDate = endDate;
		this.date = date;

		if (isValidBoundaries()) {
			boundaries = Range.closed(startDate, endDate);
		}
	}

	/**
	 * Проверяет корректность границ интервала.
	 */
	public boolean isValidBoundaries() {
		return startDate.before(endDate);
	}

}
