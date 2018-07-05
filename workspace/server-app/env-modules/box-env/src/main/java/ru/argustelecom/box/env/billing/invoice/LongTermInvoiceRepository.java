package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkArgument;
import static java.util.Collections.singletonList;
import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsLast;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.joining;
import static java.util.stream.Collectors.toList;
import static org.apache.commons.lang.StringUtils.stripToEmpty;
import static ru.argustelecom.box.env.billing.invoice.model.InvoiceState.ACTIVE;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.chrono.DateUtils.before;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice.LongTermInvoiceQuery;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.privilege.discount.model.Discount;
import ru.argustelecom.box.env.privilege.model.Privilege;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQueries;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Repository
public class LongTermInvoiceRepository implements Serializable {

	private static final long serialVersionUID = 964934197629593486L;

	private static final String QN_INVOICES_BY_DISCOUNT = "LongTermInvoiceRepository.findInvoicesByDiscount";
	private static final String QN_ALL_INVOICES_BY_SUBSCRIPTIONS_INS_PERIOD = "LongTermInvoiceRepository.findAllInovicesBySubscriptionsInsPeriod";
	private static final String QN_LAST_INVOICES_BY_SUBSCRIPTIONS = "LongTermInvoiceRepository.findLastInvoicesBySubscriptions";
	private static final String QN_LAST_CLOSED_INVOICE_BY_SUBSCRIPTION = "LongTermInvoiceRepository.findLastClosedInvoice";
	private static final String QN_PREVIOUS_INVOICE = "LongTermInvoiceRepository.findPrevInvoice";
	private static final String QN_FETCH_INVOICES_LAZY = "LongTermInvoiceRepository.fetchLongTermInvoicesLazy";
	private static final String QN_FETCH_INVOICES_EAGER = "LongTermInvoiceRepository.fetchLongTermInvoicesEager";

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	// @formatter:off

	/**
	 * Создает инвойс для тарификации указанной подписки. При создании инвойса используется обязательный план
	 * тарификации. Подписки, привилегии и прочее состояние созданного инвойса инициализируется состоянием из плана
	 * 
	 * @param subscription
	 *            - подписка, которую необходимо в очередной раз протарифицировать
	 * @param invoicePlan
	 *            - план ближайшего списания для указанной подписки
	 * 
	 * @return созданный инвойс для подписки, в состоянии ACTIVE
	 * 
	 * @see LongTermInvoice#applyPlan(InvoicePlan)
	 */
	public LongTermInvoice createInvoice(Subscription subscription, InvoicePlan invoicePlan) {
		checkRequiredArgument(subscription, "subscription");
		checkRequiredArgument(invoicePlan, "invoicePlan");

		LongTermInvoice instance = LongTermInvoice.builder()
			.id(idSequence.nextValue(LongTermInvoice.class))
			.subscription(subscription)
			.plan(invoicePlan)
			.build();

		em.persist(instance);
		return instance;
	}
	
	/**
	 * Находит все "длинные" инвойсы на указанном лицевом счете с указанными состояниями
	 *  
	 * @param personalAccount 
	 *            - лицевой счет, на котором хочется посмотреть инвойсы
	 * @param states
	 *            - состояния, в которых ожидаются инвойсы
	 * 
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<LongTermInvoice> findInvoices(PersonalAccount personalAccount, List<InvoiceState> states) {
		checkRequiredArgument(personalAccount, "personalAccount");
		
		LongTermInvoiceQuery query = new LongTermInvoiceQuery();
		
		if (states != null && !states.isEmpty()) {
			query.or(states.stream()
				.map(s -> query.state().equal(s))
				.collect(toList())
				.toArray(new Predicate[] {})
			);
		}
		query.and(query.personalAccount().equal(personalAccount));

		return query.getResultList(em);
	}

	/**
	 * Находит ВСЕ (!!!!) инвойсы во всех состояниях для указанной подписки
	 * 
	 * @param subscription 
	 *            - подписка, для которой хотим найти инвойсы
	 * 
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<LongTermInvoice> findInvoices(Subscription subscription) {
		// FIXME опасный метод, потому что со временем количество инвойсов только растет
		// не правильно искать ВСЕ инвойсы по подписке, их может быть внезапно очень много
		// необходимо добавить ограничение и переработать форму показа инвойсов по подписке в карточке
		checkRequiredArgument(subscription, "subscription");
		
		LongTermInvoiceQuery query = new LongTermInvoiceQuery();
		query.and(query.subscription().equal(subscription));
		
		return query.getResultList(em);
	}
	
	/**
	 * Находит все открытые инвойсы для указанной привилегии
	 * 
	 * @param privilege
	 *            - привилегия, по которой выполняется поиск инвойсов
	 * 
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<LongTermInvoice> findInvoices(Privilege privilege) {
		checkRequiredArgument(privilege, "privilege");
		
		LongTermInvoiceQuery query = new LongTermInvoiceQuery();
		
		query.and(
			query.privilege().equal(privilege), 
			query.state().equal(ACTIVE)
		);
		
		return query.getResultList(em);
	}
	
	/**
	 * Находит все инвойсы в любых состояниях для указанной скидки
	 * 
	 * @param discount
	 *            - скидка, по привязке к которой выполняется поиск инвойсов
	 * 
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	@NamedNativeQuery(name = QN_INVOICES_BY_DISCOUNT, query 
		= "SELECT invoice_id " 
		+ "  FROM system.invoice_discounts "
		+ " WHERE discount_id = :discount_id"
	)
	public List<LongTermInvoice> findInvoices(Discount discount) {
		checkRequiredArgument(discount, "discount");
		
		Query query = em.createNamedQuery(QN_INVOICES_BY_DISCOUNT);
		query.setParameter("discount_id", discount.getId());
		
		@SuppressWarnings("unchecked")
		List<BigInteger> rawResult = query.getResultList();
		List<Long> discountIds = rawResult.stream().map(BigInteger::longValue).collect(toList());

		return EntityManagerUtils.findList(em, LongTermInvoice.class, discountIds);
	}
	
	/**
	 * Находит все инвойсы в любых состояниях, периоды которых пересекаются с указанными датами, а сами инвойсы 
	 * принадлежат указанным подпискам. Найденные инвойсы группируются по подпискам и помещаются в карту. Если для 
	 * какой-то подписки инвойса за указанный период не обнаружится, то в качестве значения в карте для этой подписки 
	 * будет указан пустой список. После построения такой карты подписок все инвойсы сортируются по порядку следования
	 * их периодов 
	 * 
	 * @param subscriptions 
	 *            - список подписок, для которых необходимо определить инвойсы за период
	 * @param startDate
	 *            - дата начала периода, за который нам интересны инвойсы
	 * @param endDate
	 *            - дата окончания периода, за который нам интересны инвойсы
	 * 
	 * @return карта найденных инвойсов с группировкой по подпискам
	 */
	@NamedQuery(name = QN_ALL_INVOICES_BY_SUBSCRIPTIONS_INS_PERIOD, query
		= "select i "
		+ "  from LongTermInvoice i "
		+ "       left join fetch i.discounts"
		+ "       left join fetch i.privilege"
		+ " where i.subscription in :subscriptions "
		+ "   and i.startDate < :endDate "
		+ "   and i.endDate > :startDate "
		/* Запрос написан верно. Да, i.startDate < :endDate и да i.endDate > :startDate. Таким образом получится 
		   пересечение инвойсов с указанным периодом [:startDate .. :endDate] */ 
	)
	public Map<Subscription, List<LongTermInvoice>> findInvoices(List<Subscription> subscriptions,
			Date startDate, Date endDate) {
		
		checkRequiredArgument(subscriptions, "subscriptions");
		checkRequiredArgument(startDate, "startDate");
		checkRequiredArgument(endDate, "endDate");
		
		if (subscriptions.isEmpty()) {
			return Collections.emptyMap();
		}
		
		checkArgument(before(startDate, endDate));
		
		TypedQuery<LongTermInvoice> query = em.createNamedQuery(
			QN_ALL_INVOICES_BY_SUBSCRIPTIONS_INS_PERIOD, 
			LongTermInvoice.class
		);
		query.setParameter("subscriptions", subscriptions);
		query.setParameter("startDate", startDate);
		query.setParameter("endDate", endDate);
		
		Map<Subscription, List<LongTermInvoice>> result = query.getResultList().stream().distinct()
				.collect(groupingBy(LongTermInvoice::getSubscription));
		
		subscriptions.forEach(s -> {
			if (!result.containsKey(s)) {
				result.put(s, new ArrayList<>());
			}
		});
		
		result.forEach((subscription, invoices) -> invoices.sort(nullsLast(comparing(LongTermInvoice::getEndDate))));
		return result;
	}
	
	/**
	 * Если имеется коллекция идентификаторов некоторых инвойсов, то их можно зафетчить из БД. При этом существует два
	 * режима: фетчинг ленивый (без привилегий и скидок), фетчинг жадный (вместе с инвойсами сразу загружаются 
	 * привилегии и скидки)
	 * 
	 * @param invoiceIds 
	 *            - идентификаторы инвойсов
	 * @param prefetchRelated
	 *            - если true, то будут загружены привилегии и скидки 
	 * 
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	@NamedQueries({
		@NamedQuery(name = QN_FETCH_INVOICES_LAZY, query
			= "select i "
			+ "  from LongTermInvoice i "
			+ " where i.id in :ids"		
		),
		@NamedQuery(name = QN_FETCH_INVOICES_EAGER, query
			= "select i "
			+ "  from LongTermInvoice i "
			+ "       left join fetch i.discounts"
			+ "       left join fetch i.privilege"
			+ " where i.id in :ids"		
		)
	})
	public List<LongTermInvoice> fetchInvoices(List<Long> invoiceIds, boolean prefetchRelated) {
		checkRequiredArgument(invoiceIds, "invoiceIds");
		String queryName = prefetchRelated ? QN_FETCH_INVOICES_EAGER : QN_FETCH_INVOICES_LAZY; 
		
		if (invoiceIds.isEmpty()) {
			return Collections.emptyList();
		}
		
		TypedQuery<LongTermInvoice> query = em.createNamedQuery(queryName, LongTermInvoice.class);
		query.setParameter("ids", invoiceIds);
		
		return query.getResultList().stream().distinct().collect(toList());
	}
	
	/**
	 * Ищет последние инвойсы в любых состояниях для указанных подписок. Опционально загружает связанные привилегии и 
	 * скидки
	 *  
	 * @param subscriptions 
	 *            - подписки, для которых выполняется поиск последних инвойсов
	 * @param prefetchRelated
	 *            - если true, то будут загружены привилегии и скидки
	 *  
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	@NamedNativeQuery(name = QN_LAST_INVOICES_BY_SUBSCRIPTIONS, query
		= "SELECT last_invoice.invoice_id "
		+ "  FROM "
		+ "  ( "
		+ "      SELECT s AS id "
		+ "        FROM unnest(CAST(:sub_ids_array AS BIGINT [])) s"
		+ "  ) subscriptions, "
		+ "  LATERAL ( "
		+ "      SELECT subscription_id, "
		+ "             end_date "
		+ "        FROM system.invoice "
		+ "       WHERE dtype = 'LongTermInvoice' "
		+ "         AND subscription_id = subscriptions.id "
		+ "    ORDER BY subscription_id, end_date DESC "
		+ "       LIMIT 1 "
		+ "  ) last_invoice_ends, "
		+ "  LATERAL ( "
		+ "      SELECT i.subscription_id, "
		+ "             i.id AS invoice_id "
		+ "        FROM system.invoice i "
		+ "       WHERE i.dtype = 'LongTermInvoice' "
		+ "         AND i.subscription_id = last_invoice_ends.subscription_id "
		+ "         AND i.end_date = last_invoice_ends.end_date "
		+ "    ORDER BY i.subscription_id, i.end_date DESC "
		+ "  ) last_invoice"
	)
	public List<LongTermInvoice> findLastInvoices(List<Subscription> subscriptions, boolean prefetchRelated) {
		checkRequiredArgument(subscriptions, "subscriptions");

		if (subscriptions.isEmpty()) {
			return Collections.emptyList();
		}
		
		String ids = subscriptions.stream()
			.filter(s -> s.getId() != null)
			.map(s -> s.getId().toString())
			.collect(joining(","));
		
		ids = "{" + stripToEmpty(ids) + "}";
		
		Query query = em.createNamedQuery(QN_LAST_INVOICES_BY_SUBSCRIPTIONS);
		query.setParameter("sub_ids_array", ids);
		
		@SuppressWarnings("unchecked")
		List<BigInteger> rawResult = query.getResultList();
		List<Long> invoiceIds = rawResult.stream().map(BigInteger::longValue).collect(toList());
		
		return invoiceIds.isEmpty() ? Collections.emptyList() : fetchInvoices(invoiceIds, prefetchRelated);
	}

	/**
	 * Ищет последний инвойс в любом состоянии для указанной подписки. Шоткат для 
	 * {@link #findLastInvoices(List, boolean)}
	 * 
	 * @param subscription
	 *            - подписка, для которой выполняется поиск последнего инвойса
	 * @param prefetchRelated
	 *            - если true, то будут загружены привилегии и скидки
	 * 
	 * @return последний инвойс для указанной подписки или null, если у подписки еще нет инвойсов 
	 */
	public LongTermInvoice findLastInvoice(Subscription subscription, boolean prefetchRelated) {
		checkRequiredArgument(subscription, "subscription");
		List<LongTermInvoice> result = findLastInvoices(singletonList(subscription), prefetchRelated);
		return !result.isEmpty() ? result.get(0) : null;
	}
	
	/**
	 * Ищет предыдущий инвойс в любом состоянии для указанного инвойса.
	 * 
	 * @param currentInvoice 
	 *            - инвойс, с которого необходимо начать поиск предыдущего инвойса 
	 * @param prefetchRelated
	 *            - если true, то будут загружены привилегии и скидки
	 * 
	 * @return предыдущий инвойс для указанного инвойса или null, если текущий инвойс самый первый
	 */
	@NamedNativeQuery(name = QN_PREVIOUS_INVOICE, query
		= "SELECT previous_invoice.invoice_id "
		+ "  FROM "
		+ "  (  "
		+ "      SELECT dtype, "
		+ "             subscription_id, "
		+ "             end_date "
		+ "        FROM system.invoice "
		+ "       WHERE id = :current_invoice_id"
		+ "  ) current_invoice_ends, "
		+ "  LATERAL ( "
		+ "      SELECT i.subscription_id, "
		+ "             i.id AS invoice_id "
		+ "        FROM system.invoice i "
		+ "       WHERE i.dtype = current_invoice_ends.dtype "
		+ "         AND i.subscription_id = current_invoice_ends.subscription_id "
		+ "         AND i.end_date < current_invoice_ends.end_date "
		+ "    ORDER BY i.subscription_id, i.end_date DESC "
		+ "       LIMIT 1"
		+ "  ) previous_invoice"
	)
	public LongTermInvoice findPrevInvoice(LongTermInvoice currentInvoice, boolean prefetchRelated) {
		checkRequiredArgument(currentInvoice, "currentInvoice");
		
		Query query = em.createNamedQuery(QN_PREVIOUS_INVOICE);
		query.setParameter("current_invoice_id", currentInvoice.getId());
		
		@SuppressWarnings("unchecked")
		List<BigInteger> rawResult = query.getResultList();
		List<Long> invoiceIds = rawResult.stream().map(BigInteger::longValue).collect(toList());
		
		if (!invoiceIds.isEmpty()) {
			List<LongTermInvoice> result = fetchInvoices(invoiceIds, prefetchRelated);
			return !result.isEmpty() ? result.get(0) : null;
		}
		
		return null;
	}
	
	/**
	 * Ищет последний закрытый инвойс для указанной подписки.
	 * 
	 * @param subscription
	 *            - подписка, для которой выполняется поиск последнего закрытого инвойса
	 * 
	 * @return последний закртытый инвойс для указанной подписки или null, если у подписки еще нет закрытых инвойсов
	 */
	@NamedQuery(name = QN_LAST_CLOSED_INVOICE_BY_SUBSCRIPTION, query 
		= "select i " 
		+ "  from LongTermInvoice i " 
		+ " where i.subscription = :subscription " 
		+ "   and i.state = 'CLOSED' " 
		+ " order by i.subscription, i.endDate desc"
	)
	public LongTermInvoice findLastClosedInvoice(Subscription subscription) {
		checkRequiredArgument(subscription, "subscription");
		
		TypedQuery<LongTermInvoice> query = em.createNamedQuery(
			QN_LAST_CLOSED_INVOICE_BY_SUBSCRIPTION,
			LongTermInvoice.class
		);

		query.setParameter("subscription", subscription);
		query.setFirstResult(0);
		query.setMaxResults(1);
		
		List<LongTermInvoice> result = query.getResultList();
		return !result.isEmpty() ? result.get(0) : null;
	}

}