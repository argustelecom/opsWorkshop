package ru.argustelecom.box.env.billing.bill;

import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ru.argustelecom.box.env.billing.bill.model.Analytic;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillDateGetter;

public abstract class RawDataService {

	@PersistenceContext
	protected EntityManager em;

	/**
	 * Создаёт POJO, по которому {@linkplain BillAnalyticType аналитики} должны определить периоды, за которые
	 * необходимо произвести расчёты.
	 */
	BillDateGetter.BillPeriodDate initBillDates(BillData billData) {
		return new BillDateGetter.BillPeriodDate(billData.getPeriod(), toLocalDateTime(billData.getBillDate()),
				toLocalDateTime(billData.getCreationDate()));
	}

	/**
	 * Создаёт {@linkplain Analytic врапперы} вокруг типов аналитик, на основании дат формирования счёта.
	 */
	List<Analytic> createAnalytics(Set<BillAnalyticType> analyticTypes, BillDateGetter.BillPeriodDate periodDates) {
		if (isEmpty(analyticTypes)) {
			return Collections.emptyList();
		}

		List<Analytic> analytics = new ArrayList<>();
		analyticTypes.forEach(at -> {

			Date startDate = fromLocalDateTime(at.getBillDateGetter().getStartDate(periodDates));
			Date endDate = fromLocalDateTime(at.getBillDateGetter().getEndDate(periodDates));

			LocalDateTime billDateTime = at.getBillDateGetter().getDate(periodDates);
			Date date = billDateTime != null ? fromLocalDateTime(at.getBillDateGetter().getDate(periodDates)) : null;

			analytics.add(new Analytic(at, startDate, endDate, date));
		});
		return analytics;
	}

}