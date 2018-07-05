package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkArgument;
import static java.math.BigDecimal.ZERO;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;
import static org.apache.commons.collections.CollectionUtils.isEmpty;
import static ru.argustelecom.box.env.billing.bill.IncomesQueryResult.INCOMES_QUERY_RESULT_MAPPER;
import static ru.argustelecom.box.env.billing.bill.model.AnalyticTypeError.START_PERIOD_DATE_AFTER_END_DATE;
import static ru.argustelecom.box.env.billing.bill.model.BillAnalyticType.AnalyticCategory.BALANCE;
import static ru.argustelecom.box.env.billing.bill.model.BillAnalyticType.AnalyticCategory.INCOME;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.bill.model.Analytic;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType.AnalyticCategory;
import ru.argustelecom.box.env.billing.bill.model.BillDateGetter.BillPeriodDate;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.env.billing.bill.model.BillType;
import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;
import ru.argustelecom.box.env.billing.bill.model.IncomesRaw;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.DtoTranslator;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;
import ru.argustelecom.system.inf.exception.SystemException;

/**
 * Сервис для расчёта сырых данных по поступлениям и остаткам.
 */
@DtoTranslator
public class IncomesService extends RawDataService implements Serializable {

	private static final List<AnalyticCategory> ANALYTIC_CATEGORIES = Lists.newArrayList(INCOME, BALANCE);

	@Inject
	private PersonalAccountBalanceService personalAccountBalanceSvc;

	@Inject
	private BillAnalyticTypeRepository analyticTypeRp;

	@Inject
	private BillDateUtils billDateUtils;

	/**
	 * Собирает информацию по поступлениям и остаткам, в сыром не агрегированном виде. Данные собираются только по
	 * {@linkplain ru.argustelecom.box.env.billing.bill.model.AbstractBillAnalyticType аналитикам}, которые выбраны для
	 * {@linkplain BillType#analytics спецификации счёта}.
	 */
	public List<IncomesRaw> initRawData(BillData billData) {
		BillPeriodDate billDates = initBillDates(billData);

		Map<AnalyticCategory, Set<BillAnalyticType>> analyticMap = groupAnalyticsByCategories(billData);
		List<Analytic> incomeAnalytics = createAnalytics(analyticMap.get(INCOME), billDates);
		List<Analytic> balanceAnalytics = createAnalytics(analyticMap.get(BALANCE), billDates);

		List<IncomesRaw> incomesRawList = new ArrayList<>();

		Set<Long> personalAccountIds = new HashSet<>();
		if (!isEmpty(incomeAnalytics) || !isEmpty(balanceAnalytics)) {
			personalAccountIds = findPersonalAccountIds(billData);
		}

		if (!isEmpty(incomeAnalytics)) {
			incomesRawList.addAll(createRawByTransactions(incomeAnalytics, personalAccountIds));
		}

		if (!isEmpty(balanceAnalytics)) {
			incomesRawList.addAll(createRawByBalance(balanceAnalytics, personalAccountIds));
		}

		incomesRawList.sort(IncomesRaw.incomesRawComparator());

		return incomesRawList;
	}

	/**
	 * Объединяет все выбранные аналитики спецификации счёта ({@link BillData#getBillType()}) по категориям. Исключая
	 * категории отличные от {@link #ANALYTIC_CATEGORIES} и фильтрует типы аналитик по типу периода.
	 * 
	 * @see #selectAvailableAnalyticTypes(BillPeriod, Set)
	 */
	private Map<AnalyticCategory, Set<BillAnalyticType>> groupAnalyticsByCategories(BillData billData) {
		return analyticTypeRp.find(billData.getBillType(), billData.getPeriod(), ANALYTIC_CATEGORIES).stream()
				.collect(groupingBy(BillAnalyticType::getAnalyticCategory, toSet()));
	}

	/**
	 * Создаёт сырые данные по поступлениям. Все транзакции для расчёта аналитик вытаскиваются одной, общей, пачкой. Для
	 * этого:
	 * <ul>
	 * <li>определяются минимальная и максимальная даты начала и окончания формирования счёта соответственно </lo>
	 * <li>выбираются все положительные транзакции за этот временной интервал для необходимых подписок</lo>
	 * </ul>
	 * <br/>
	 * Логика формирования сырых данных:
	 * <ul>
	 * <li>если у аналитики валидные даты, то по ней будет производиться расчёт. В противном случае будут добавлены
	 * нулевые данные и проставлена {@linkplain ru.argustelecom.box.env.billing.bill.model.AnalyticTypeError
	 * ошибка}</li>
	 * <li>если для аналитики нет исходных данных, то она будет добавлена с нулевым значением</li>
	 * </ul>
	 *
	 * @see Analytic
	 */
	private List<IncomesRaw> createRawByTransactions(List<Analytic> analytics, Set<Long> personalAccountIds) {
		Date minStartDate = billDateUtils.findMinStartDate(analytics);
		Date maxEndDate = billDateUtils.findMaxEndDate(analytics);

		List<IncomesQueryResult> results = findIncomeTransactions(minStartDate, maxEndDate, personalAccountIds);
		List<IncomesRaw> incomesRawList = new ArrayList<>();

		analytics.forEach(analytic -> {
			if (analytic.isValidBoundaries()) {
				results.forEach(raw -> {

					// если дата транзакции входит в период аналитики, то создаём по ней сырые данные
					if (analytic.getBoundaries().contains(raw.getDate())) {
						//@formatter:off
						incomesRawList.add(IncomesRaw.builder()
							.analyticTypeId(analytic.getType().getId())
							.personalAccountId(raw.getPersonalAccountId())
							.transactionId(raw.getTransactionId())
							.date(raw.getDate())
							.sum(raw.getAmount())
						.build());
						//@formatter:on
					}
				});

				// если границы периода не валидны, то создаём запись с ошибкой и нулевым значением
			} else {
				//@formatter:off
				incomesRawList.add(IncomesRaw.builder()
					.analyticTypeId(analytic.getType().getId())
					.sum(ZERO)
					.error(START_PERIOD_DATE_AFTER_END_DATE)
				.build());
				//@formatter:on
			}
		});

		// аналитики, по которым не нашлось данных надо добавить с нулевыми значениями
		List<Long> emptyAnalyticIds = analytics.stream().map(analytic -> analytic.getType().getId()).collect(toList());
		List<Long> notEmptyAnalyticIds = incomesRawList.stream().map(IncomesRaw::getAnalyticTypeId).collect(toList());
		emptyAnalyticIds.removeAll(notEmptyAnalyticIds);

		emptyAnalyticIds.forEach(emptyAnalyticId -> {
			//@formatter:off
			incomesRawList.add(IncomesRaw.builder()
				.analyticTypeId(emptyAnalyticId)
				.sum(ZERO)
				.build());
			//@formatter:on
		});

		return incomesRawList;
	}

	private List<IncomesRaw> createRawByBalance(List<Analytic> analytics, Set<Long> personalAccountIds) {
		List<IncomesRaw> incomesRawList = new ArrayList<>();
		analytics.forEach(analytic -> personalAccountIds.forEach(accountId -> {
			if (analytic.getDate() != null) {
				Money amount = personalAccountBalanceSvc
						.getBalanceBefore(em.getReference(PersonalAccount.class, accountId), analytic.getDate());
				//@formatter:off
				incomesRawList.add(IncomesRaw.builder()
						.analyticTypeId(analytic.getType().getId())
						.personalAccountId(accountId)
						.date(analytic.getDate())
						.sum(amount.getAmount())
						.build());
				//@formatter:on
			} else {
				throw new SystemException(String.format("Для аналитики '%s' не задана дата формирования", analytic));
			}
		}));

		return incomesRawList;
	}

	private static final String FIND_INCOME_TRANSACTIONS = "IncomesService.findIncomeTransactions";

	/**
	 * Выбирает транзакции для списка подписок и интевала дат. Предполагается, что метод будет вызываться один раз для
	 * получения всех транзакций(за один запрос), покрывающих необходимость для расчёта ВСЕХ требующихся анналитик.
	 */
	//@formatter:off
	@NamedNativeQuery(name = FIND_INCOME_TRANSACTIONS, resultSetMapping = INCOMES_QUERY_RESULT_MAPPER,
			query = "SELECT DISTINCT\n" +
					"  t.personal_account_id AS personal_account_id,\n" +
					"  t.id                  AS transaction_id,\n" +
					"  t.business_date       AS date,\n" +
					"  t.amount              AS amount\n" +
					"FROM\n" +
					"  system.transactions t\n" +
					" INNER JOIN system.transaction_reason tr ON tr.id = t.id\n" +
					"WHERE\n" +
					"  t.personal_account_id in (:personal_account_ids)\n" +
					"  AND t.amount > 0\n" +
					"  AND tr.dtype <> 'CancelReason' \n" +
					"  AND t.business_date BETWEEN :start_date AND :end_date")
	//@formatter:on
	private List<IncomesQueryResult> findIncomeTransactions(Date startDate, Date endDate,
			Set<Long> personalAccountIds) {
		checkArgument(!isEmpty(personalAccountIds));

		return em.createNamedQuery(FIND_INCOME_TRANSACTIONS, IncomesQueryResult.class)
				.setParameter("personal_account_ids", personalAccountIds).setParameter("start_date", startDate)
				.setParameter("end_date", endDate).getResultList();
	}

	private static final String FIND_PERSONAL_ACCOUNTS_BY_INVOICES = "IncomesService.findPersonaAccountsByInvoices";

	/**
	 * Ищет уникальные лицевые счета, участвующие в счете. Если счет сгруппирован по ЛС - возвращает коллекцию из одного
	 * элемента - объекта группировки, иначе - ищет лицевые счета, связанные с подписками и инвойсами, входящими в счет
	 * 
	 * @param billData
	 * @return
	 */
	//@formatter:off
	@NamedNativeQuery(name = FIND_PERSONAL_ACCOUNTS_BY_INVOICES, 
			query = " SELECT i.personal_account_id\n" +
					" FROM system.invoice i\n" +
					" WHERE i.id IN (:invoiceIds)")
	//@formatter:on
	private Set<Long> findPersonalAccountIds(BillData billData) {
		if (billData.getGroupingMethod().equals(GroupingMethod.PERSONAL_ACCOUNT)) {
			return Sets.newHashSet(billData.getPersonalAccount().getId());
		}

		Set<Long> accountIds = new HashSet<>();
		if (!isEmpty(billData.getSubscriptions())) {
			accountIds.addAll(billData.getSubscriptions().stream()
					.map(subscription -> subscription.getPersonalAccount().getId()).collect(Collectors.toList()));
		}

		List<Long> invoiceIds = new ArrayList<>();
		if (!isEmpty(billData.getUsageInvoiceIds())) {
			invoiceIds.addAll(billData.getUsageInvoiceIds());
		}
		if (!isEmpty(billData.getShortTermInvoiceIds())) {
			invoiceIds.addAll(billData.getShortTermInvoiceIds());
		}

		if (!isEmpty(invoiceIds)) {
			@SuppressWarnings("unchecked")
			List<BigInteger> personalAccountIds = em.createNamedQuery(FIND_PERSONAL_ACCOUNTS_BY_INVOICES)
					.setParameter("invoiceIds", invoiceIds).getResultList();
			accountIds.addAll(personalAccountIds.stream().map(BigInteger::longValue).collect(Collectors.toSet()));
		}

		return accountIds;
	}

	private static final long serialVersionUID = -3577167605923057111L;

}