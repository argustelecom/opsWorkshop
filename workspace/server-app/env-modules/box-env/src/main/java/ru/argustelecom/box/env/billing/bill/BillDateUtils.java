package ru.argustelecom.box.env.billing.bill;

import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.util.Collection;
import java.util.Date;
import java.util.stream.Stream;

import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.bill.model.Analytic;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillDateGetter;
import ru.argustelecom.box.env.billing.bill.model.BillDateGetter.BillPeriodDate;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.system.inf.exception.SystemException;

@NoArgsConstructor
public class BillDateUtils {

	/**
	 * Ищет минимальную начальную дату среди всех аналитик.
	 */
	public Date findMinStartDate(Collection<BillAnalyticType> analyticTypes, BillPeriod period, Date billDate,
			Date billCreationDate) {
		BillPeriodDate billPeriodDate = new BillDateGetter.BillPeriodDate(period, toLocalDateTime(billDate),
				billCreationDate != null ? toLocalDateTime(billCreationDate) : null);
		return findMin(analyticTypes.stream()
				.map(at -> fromLocalDateTime(at.getBillDateGetter().getStartDate(billPeriodDate))));
	}

	/**
	 * Ищет максимальную конечную дату среди всех аналитик.
	 */
	public Date findMaxEndDate(Collection<BillAnalyticType> analyticTypes, BillPeriod period, Date billDate,
			Date billCreationDate) {
		BillPeriodDate billPeriodDate = new BillPeriodDate(period, toLocalDateTime(billDate),
				billCreationDate != null ? toLocalDateTime(billCreationDate) : null);
		return findMax(
				analyticTypes.stream().map(at -> fromLocalDateTime(at.getBillDateGetter().getEndDate(billPeriodDate))));
	}

	public Date findMinStartDate(Collection<Analytic> analytics) {
		return findMin(analytics.stream().map(Analytic::getStartDate));
	}

	public Date findMaxEndDate(Collection<Analytic> analytics) {
		return findMax(analytics.stream().map(Analytic::getEndDate));
	}

	private Date findMin(Stream<Date> dates) {
		return dates.min(Date::compareTo).orElseThrow(SystemException::new);
	}

	private Date findMax(Stream<Date> dates) {
		return dates.max(Date::compareTo).orElseThrow(SystemException::new);
	}

}
