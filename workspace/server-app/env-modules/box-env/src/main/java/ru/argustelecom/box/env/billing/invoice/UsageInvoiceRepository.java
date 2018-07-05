package ru.argustelecom.box.env.billing.invoice;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.toList;
import static ru.argustelecom.box.env.billing.period.PeriodBuilderService.chargingOf;
import static ru.argustelecom.box.env.commodity.telephony.model.TelephonyOptionState.ACTIVE;
import static ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils.initializeAndUnproxy;

import java.io.Serializable;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.criteria.Predicate;

import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.InvoiceState;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice.UsageInvoiceQuery;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntry;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceEntryContainer;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoiceSettings;
import ru.argustelecom.box.env.billing.invoice.model.UsageInvoice_;
import ru.argustelecom.box.env.billing.subscription.SubscriptionRepository;
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlannerConfig;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.commodity.telephony.model.Option;
import ru.argustelecom.box.env.commodity.telephony.model.TelephonyOption;
import ru.argustelecom.box.env.contract.model.ContractType;
import ru.argustelecom.box.env.idsequence.IdSequenceService;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.inf.service.Repository;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Repository
public class UsageInvoiceRepository implements Serializable {

	private static final long serialVersionUID = 6272893673780762923L;

	@PersistenceContext
	private transient EntityManager em;

	@Inject
	private IdSequenceService idSequence;

	@Inject
	private SubscriptionRepository subscriptionRp;

	@Inject
	private UsageInvoiceSettingsAppService usageInvoiceSettingsAs;

	@Inject
	private UsageInvoiceEntryRepository usageInvoiceEntryRp;

	/**
	 * Создает инвойс для тарификации указанной услуги. При создании инвойса используется опция телефонии. Дата начала =
	 * дата активации опции = дата создания опции (так как опция создается сразу в ACTIVE) Дата окончания = дата
	 * окончания периода списания подписки Поставшика берем из типа договора на опцию withoutContract = true если опция
	 * телефонии совпадает с опцией, заданной в правилах тарификации телефонных вызовов, в таком случае создается
	 * "Особый" инвойс.
	 * 
	 * @param service
	 *            услуга
	 * @param option
	 *            опция телефонии
	 *
	 * @return коллекцию инвойсов в состоянии ACTIVE.
	 *
	 */
	public List<UsageInvoice> createInvoice(Service service, Option<?, ?> option, Date startDate) {
		UsageInvoice regularInvoice = createInvoice(service, option, startDate, null);

		UsageInvoiceSettings settings = usageInvoiceSettingsAs.find();
		boolean sameOptionType = Objects.equals(settings.getTelephonyOptionType(), option.getType());
		boolean srvContractEqOptContract = option.getSubject().getContract().equals(service.getSubject().getContract());
		boolean withoutContract = srvContractEqOptContract && sameOptionType;

		UsageInvoiceEntry entries = usageInvoiceEntryRp.create(new UsageInvoiceEntryContainer(new ArrayList<>()));

		if (withoutContract) {
			// @formatter:off
			UsageInvoice specialInvoice = UsageInvoice.builder()
					.id(idSequence.nextValue(UsageInvoice.class))
					.personalAccount(regularInvoice.getPersonalAccount())
					.service(service)
					.option(option)
					.provider(regularInvoice.getProvider())
					.startDate(startDate)
					.endDate(regularInvoice.getEndDate())
					.withoutContract(true)
					.entries(entries)
					.build();
			// @formatter:on
			em.persist(specialInvoice);

			return Arrays.asList(regularInvoice, specialInvoice);
		}

		return Collections.singletonList(regularInvoice);
	}

	public UsageInvoice copyInvoice(UsageInvoice invoice) {
		return createInvoice(invoice.getService(), invoice.getOption(), invoice.getStartDate(), invoice.getEndDate());
	}

	private UsageInvoice createInvoice(Service service, Option<?, ?> option, Date startDate, Date endDate) {
		checkNotNull(service);
		checkNotNull(option);
		checkNotNull(startDate);

		Subscription subscription = subscriptionRp.findSubscription(service.getSubject());

		InvoicePlannerConfig invoicePlannerConfig = new InvoicePlannerConfig();
		invoicePlannerConfig.setSubscription(subscription);

		PersonalAccount personalAccount = service.getSubject().getPersonalAccount();
		PartyRole provider = ((ContractType) option.getSubject().getContract().getType()).getProvider();

		if (endDate == null) {
			endDate = invoicePlannerConfig.chargingPeriod(startDate).endDate();
		}

		UsageInvoiceEntry entries = usageInvoiceEntryRp.create(new UsageInvoiceEntryContainer(new ArrayList<>()));

		// @formatter:off
		UsageInvoice regularInvoice = UsageInvoice.builder()
				.id(idSequence.nextValue(UsageInvoice.class))
				.personalAccount(personalAccount)
				.service(service)
				.option(option)
				.provider(provider)
				.startDate(startDate)
				.endDate(endDate)
				.withoutContract(false)
				.entries(entries)
				.build();
		// @formatter:on
		em.persist(regularInvoice);

		return regularInvoice;
	}

	/**
	 * Создаёт все недостающие инвойсы по услуге до переданной даты. Для этого:
	 * <ul>
	 * <li>Находятся все последние действующие инвойсы по каждой опции услуги.</li>
	 * <li>Для них определяется период списания.</li>
	 * <li>Если следующий период списания меньше даты интереса или включает её, то создаётся новый инвойс.</li>
	 * </ul>
	 *
	 * @param service
	 *            услуга, по которой нужно создать инвойсы.
	 * @param poi
	 *            точка интереса, до которой должны быть созданы все необходимые инвойсы.
	 * @return список всех созданных инвойсов.
	 */
	public List<UsageInvoice> createMissedUsageInvoices(Service service, LocalDateTime poi) {

		List<UsageInvoice> lastInvoices = findLastActiveInvoices(service);
		// удаляем инвойсы-помойки, чтобы не создавать по ним новые. Они будут созданы по основным инвойсам, по
		// необходимости
		lastInvoices.removeIf(UsageInvoice::isWithoutContract);

		Subscription subscription = subscriptionRp.findSubscription(service.getSubject());

		List<UsageInvoice> newInvoices = new ArrayList<>();

		// создание всех нужных инвойсов до текущей даты
		for (UsageInvoice invoice : lastInvoices) {
			TelephonyOption telephonyOption = (TelephonyOption) initializeAndUnproxy(invoice.getOption());
			boolean activeOption = ACTIVE.equals(telephonyOption.getState());
			if (!activeOption) {
				continue;
			}

			// начинаем со следующего периода списания
			ChargingPeriod chargingPrd = chargingOf(subscription, invoice.getEndDate()).next();
			newInvoices.addAll(createMissedInvoices(chargingPrd, poi, invoice));
		}

		return newInvoices;
	}

	public List<UsageInvoice> createMissedInvoices(Option option, LocalDateTime poi) {

		Subscription subscription = subscriptionRp.findSubscription(option.getService().getSubject());
		UsageInvoice lastActiveInvoice = findLastActiveInvoice(option);

		// начинаем со следующего периода списания
		ChargingPeriod chargingPrd = chargingOf(subscription, lastActiveInvoice.getEndDate()).next();
		List<UsageInvoice> missedInvoices = createMissedInvoices(chargingPrd, poi, lastActiveInvoice);
		em.flush();
		return missedInvoices;
	}

	/**
	 * Создаёт все недостающие инвойсы до точки интереса.
	 * 
	 * @param chargingPrd
	 *            период списания, с которого надо начать создание.
	 * @param poi
	 *            точка интереса.
	 * @param prevInvoice
	 *            последний активный инвойс.
	 */
	private List<UsageInvoice> createMissedInvoices(ChargingPeriod chargingPrd, LocalDateTime poi,
			UsageInvoice prevInvoice) {

		List<UsageInvoice> newInvoices = new ArrayList<>();

		boolean beforeNow = poi.isAfter(chargingPrd.endDateTime());
		boolean includeNow = poi.isAfter(chargingPrd.startDateTime()) && poi.isBefore(chargingPrd.endDateTime());

		while (beforeNow || includeNow) {
			newInvoices
					.addAll(createInvoice(prevInvoice.getService(), prevInvoice.getOption(), chargingPrd.startDate()));

			chargingPrd = chargingPrd.next();
			beforeNow = poi.isAfter(chargingPrd.endDateTime());
			includeNow = poi.isAfter(chargingPrd.startDateTime()) && poi.isBefore(chargingPrd.endDateTime());
		}
		return newInvoices;
	}

	private static final String QN_LAST_ACTIVE_INVOICES_BY_SERVICE_OPTIONS = "UsageInvoiceRepository.findLastActiveInvoices";

	//@formatter:off
	@NamedNativeQuery(name = QN_LAST_ACTIVE_INVOICES_BY_SERVICE_OPTIONS, query
		= "SELECT last_active_invoice.id "
		+ "FROM "
		+ "  ( "
		+ "    SELECT "
		+ "      c.id AS option_id, "
		+ "      c.service_id "
		+ "    FROM system.commodity c "
		+ "    WHERE c.service_id = :service_id "
		+ "  ) options, "
		+ "  LATERAL ( "
		+ "  SELECT "
		+ "    service_id, "
		+ "    option_id, "
		+ "    end_date "
		+ "  FROM system.invoice "
		+ "  WHERE service_id = options.service_id "
		+ "        AND option_id = options.option_id "
		+ "  ORDER BY service_id, option_id, end_date DESC "
		+ "  LIMIT 1 "
		+ "  ) last_invoice_ends, "
		+ "  LATERAL ( "
		+ "  SELECT i.id "
		+ "  FROM system.invoice i "
		+ "  WHERE i.service_id = last_invoice_ends.service_id "
		+ "        AND i.option_id = last_invoice_ends.option_id "
		+ "        AND i.end_date = last_invoice_ends.end_date "
		+ "        AND i.state = 'ACTIVE' "
		+ "  ORDER BY i.subscription_id, i.end_date DESC "
		+ "  ) last_active_invoice"
	)
	//@formatter:on
	public List<UsageInvoice> findLastActiveInvoices(Service service) {
		checkNotNull(service);

		Query query = em.createNamedQuery(QN_LAST_ACTIVE_INVOICES_BY_SERVICE_OPTIONS);
		query.setParameter("service_id", service.getId());

		@SuppressWarnings("unchecked")
		List<BigInteger> rawResult = query.getResultList();
		List<Long> invoiceIds = rawResult.stream().map(BigInteger::longValue).collect(toList());

		return EntityManagerUtils.findList(em, UsageInvoice.class, invoiceIds);
	}

	/**
	 * Возвращает последний активный инвойс по опции. При этом данный инвойс не может быть инвойсом без договора.
	 * 
	 * @param option
	 *            опция, для которой надо найти инвойс.
	 */
	public UsageInvoice findLastActiveInvoice(Option<?, ?> option) {
		UsageInvoiceQuery query = new UsageInvoiceQuery();

		//@formatter:off
		query.and(
			query.option().equal(option),
			query.state().equal(InvoiceState.ACTIVE),
			query.withoutContract().isFalse()
		);
		//@formatter:on
		query.orderBy(query.criteriaBuilder().desc(query.root().get(UsageInvoice_.endDate)));

		return query.getFirstResult(em);
	}

	/**
	 * Находит все инвойсы на указанном лицевом счете с указанными состояниями
	 *
	 * @param personalAccount
	 *            - лицевой счет, для которого хотим найти инвойсы
	 * @param states
	 *            - список состояний инвойса
	 *
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<UsageInvoice> findInvoices(PersonalAccount personalAccount, List<InvoiceState> states) {
		checkNotNull(personalAccount);

		UsageInvoiceQuery query = new UsageInvoiceQuery();

		if (states != null && !states.isEmpty()) {
			query.or(states.stream().map(s -> query.state().equal(s)).collect(toList()).toArray(new Predicate[] {}));
		}

		query.and(query.personalAccount().equal(personalAccount));

		return query.getResultList(em);
	}

	/**
	 * Находит все инвойсы во всех состояниях для указанной подписки
	 *
	 * @param personalAccount
	 *            - лицевой счет, для которого хотим найти инвойсы
	 *
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<UsageInvoice> findInvoices(PersonalAccount personalAccount) {
		checkNotNull(personalAccount);

		UsageInvoiceQuery query = new UsageInvoiceQuery();
		query.and(query.personalAccount().equal(personalAccount));

		return query.getResultList(em);
	}

	/**
	 * Находит все инвойсы во всех состояниях для указанной услуги
	 *
	 * @param service
	 *            - услуга, для которой хотим найти инвойсы
	 *
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<UsageInvoice> findInvoices(Service service) {
		checkNotNull(service);

		UsageInvoiceQuery query = new UsageInvoiceQuery();
		query.and(query.service().equal(service));

		return query.getResultList(em);
	}

	/**
	 * Находит все инфорсы, во всех состояниямх. Для услуги, которые входят в переданный период.
	 * 
	 * @param service
	 *            услуга, для которой ищем инвойсы.
	 * @param from
	 *            начальная дата периода.
	 * @param to
	 *            конечная дата периода.
	 */
	public List<UsageInvoice> findInvoices(Service service, Date from, Date to) {
		checkNotNull(service);
		checkNotNull(from);
		checkNotNull(to);
		checkState(to.compareTo(from) >= 0);

		UsageInvoiceQuery query = new UsageInvoiceQuery();

		query.and(query.service().equal(service));
		query.and(query.startDate().lessOrEqualTo(to));
		query.and(query.endDate().greaterOrEqualTo(from));
		/*
		 * Запрос написан верно. Да, i.startDate <= :to и да i.endDate >= :from. Таким образом получится пересечение
		 * инвойсов с указанным периодом [:from .. :to]
		 */

		return query.getResultList(em);
	}

	/**
	 * Находит все действуещие инвойсы для указанной услуги
	 *
	 * @param service
	 *            - услуга, для которой хотим найти инвойсы
	 *
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<UsageInvoice> findActiveInvoices(Service service) {
		checkNotNull(service);

		UsageInvoiceQuery query = new UsageInvoiceQuery();
		query.and(query.service().equal(service));
		query.and(query.state().equal(InvoiceState.ACTIVE));

		return query.getResultList(em);
	}

	/**
	 * Находит все инвойсы во всех состояниях для указанной услуги
	 *
	 * @param option
	 *            - опция, для которого хотим найти инвойсы
	 *
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<UsageInvoice> findInvoices(Option<?, ?> option) {
		checkNotNull(option);

		UsageInvoiceQuery query = new UsageInvoiceQuery();
		query.and(query.option().equal(option));

		return query.getResultList(em);
	}

	/**
	 * Находит все инвойсы в указанных состояниях для указанной услуги
	 *
	 * @param option
	 *            опция, для которого хотим найти инвойсы
	 * @param states
	 *            список состояний инвойса
	 *
	 * @return список найденных инвойсов или пустую коллекцию, если по условиям поиска ничего не нашлось
	 */
	public List<UsageInvoice> findInvoices(Option<?, ?> option, List<InvoiceState> states, Date poi) {
		checkNotNull(option);

		UsageInvoiceQuery query = new UsageInvoiceQuery();

		if (states != null && !states.isEmpty()) {
			query.or(states.stream().map(s -> query.state().equal(s)).collect(toList()).toArray(new Predicate[] {}));
		}

		query.and(query.option().equal(option), query.endDate().greaterOrEqualTo(poi),
				query.startDate().lessOrEqualTo(poi));

		return query.getResultList(em);
	}
}
