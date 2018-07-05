package ru.argustelecom.box.env.privilege.discount;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.groupingBy;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.chrono.DateUtils.before;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.google.common.collect.Range;

import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.discount.model.Discount.DiscountQuery;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.exception.BusinessException;

@Repository
public class DiscountRepository implements Serializable {

	private static final long serialVersionUID = 1604971409686116762L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private IdSequenceService idSequenceService;

	public Discount createDiscount(Subscription subscription, Date validFrom, Date validTo, BigDecimal rate) {
		//@formatter:off
		Discount discount = Discount.builder()
				.id(idSequenceService.nextValue(Discount.class))
				.validFrom(validFrom)
				.validTo(validTo)
				.subscription(subscription)
				.rate(rate)
				.build();
		//@formatter:on

		shouldNotHaveSamePeriodDiscounts(findDiscounts(subscription), validFrom, validTo);

		em.persist(discount);

		return discount;
	}

	public void changeDiscountValidToDate(Discount discount, Date validTo) {
		checkNotNull(discount);
		checkArgument(validTo.after(discount.getValidFrom()) || validTo.equals(discount.getValidFrom()));

		List<Discount> otherDiscounts = findDiscounts(discount.getSubscription());
		otherDiscounts.remove(discount);
		shouldNotHaveSamePeriodDiscounts(otherDiscounts, discount.getValidFrom(), validTo);

		discount.setValidTo(validTo);
	}

	public List<Discount> findDiscounts(Subscription subscription) {
		DiscountQuery query = new DiscountQuery();
		return query.and(query.subscription().equal(subscription)).getResultList(em);
	}

	private static final String QN_ALL_DISCOUNTS_BY_SUBSCRIPTIONS_INS_PERIOD = "DiscountRepository.findAllDiscountsBySubscriptionsInsPeriod";

	// Запрос написан верно. Да, d.validFrom < :validTo и да d.validTo > :validFrom. Таким образом получится пересечение
	// скидок с указанным периодом [:startDate .. :endDate]
	//@formatter:off
	@NamedQuery(name = QN_ALL_DISCOUNTS_BY_SUBSCRIPTIONS_INS_PERIOD, query
			= "  select d "
			+ "    from Discount d "
			+ "   where d.subscription in :subscriptions "
			+ "     and d.validFrom < :validTo "
			+ "     and d.validTo > :validFrom "
	)
	//@formatter:on
	public Map<Subscription, List<Discount>> findDiscounts(List<Subscription> subscriptions, Date validFrom,
			Date validTo) {

		checkRequiredArgument(subscriptions, "subscriptions");
		checkRequiredArgument(validFrom, "validFrom");
		checkRequiredArgument(validTo, "validTo");

		if (subscriptions.isEmpty()) {
			return Collections.emptyMap();
		}

		checkArgument(before(validFrom, validTo));

		TypedQuery<Discount> query = em.createNamedQuery(QN_ALL_DISCOUNTS_BY_SUBSCRIPTIONS_INS_PERIOD, Discount.class);
		query.setParameter("subscriptions", subscriptions);
		query.setParameter("validFrom", validFrom);
		query.setParameter("validTo", validTo);

		Map<Subscription, List<Discount>> result = query.getResultList().stream()
				.collect(groupingBy(Discount::getSubscription));

		subscriptions.forEach(s -> result.putIfAbsent(s, new ArrayList<>()));

		result.forEach((subscription, invoices) -> invoices.sort(nullsLast(comparing(Discount::getValidTo))));
		return result;
	}

	private void shouldNotHaveSamePeriodDiscounts(List<Discount> otherDiscounts, Date start, Date end) {
		Range<Date> range = Range.closed(start, end);
		boolean hasSamePeriodDiscounts = otherDiscounts.stream()
				.anyMatch(od -> range.contains(od.getValidTo()) || range.contains(od.getValidFrom()));
		if (hasSamePeriodDiscounts) {
			throw new BusinessException("У подписки уже существует Скидка в указанный интервал дат");
		}
	}

}
