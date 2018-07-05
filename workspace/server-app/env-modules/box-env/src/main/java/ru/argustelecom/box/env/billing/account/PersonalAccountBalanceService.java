package ru.argustelecom.box.env.billing.account;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution.ALLOWED;
import static ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution.ALLOWED_WITH_DEBT;
import static ru.argustelecom.box.env.billing.account.PersonalAccountBalanceService.BalanceCheckingResolution.DISALLOWED;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.LockModeType;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.jboss.logging.Logger;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import lombok.val;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.billing.subscription.model.SubscriptionLifecycleQualifier;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.service.DomainService;
import ru.argustelecom.system.inf.dataaccess.namedquery.NamedNativeQuery;

/**
 * Сервис для выполения операций с балансом {@link PersonalAccount Лицевого счета}
 */
@Named
@DomainService
public class PersonalAccountBalanceService implements Serializable {

	private static final Logger log = Logger.getLogger(PersonalAccountBalanceService.class);

	private static final long serialVersionUID = -7788877942239050575L;

	private static final String QN_GET_BALANCE = "PersonalAccountBalanceService.getBalance";
	private static final String QN_GET_BALANCE_DELTA = "PersonalAccountBalanceService.getBalanceDelta";
	private static final String QN_GET_BALANCE_BEFORE = "PersonalAccountBalanceService.getBalanceBefore";
	private static final String QN_GET_AVAILABLE_BALANCE = "PersonalAccountBalanceService.getAvailableBalance";

	@PersistenceContext
	private EntityManager em;

	/**
	 * Расчитывает текущие остатки на указанном лицевом счете (Все остатки без учета зарезервированных средств).
	 * Неблокирующая операция
	 * 
	 * @param account
	 *            - лицевой счет
	 * 
	 * @return состояние лицевого счета. Всегда не null
	 */
	@NamedNativeQuery(name = QN_GET_BALANCE, query = "SELECT system.get_balance(:account_id)")
	public Money getBalance(PersonalAccount account) {
		return queryBalance(account, QN_GET_BALANCE);
	}

	/**
	 * Расчитывает доступный текущий баланс указанного лицевого счета (Все остатки с учетом зарезервированных средств).
	 * Неблокирующая операция
	 *
	 * @param account
	 *            - лицевой счет
	 *
	 * @return доступный баланс лицевого счета
	 */
	@NamedNativeQuery(name = QN_GET_AVAILABLE_BALANCE, query = "SELECT system.get_available_balance(:account_id)")
	public Money getAvailableBalance(PersonalAccount account) {
		return queryBalance(account, QN_GET_AVAILABLE_BALANCE);
	}

	private Money queryBalance(PersonalAccount account, String queryName) {
		checkRequiredArgument(account, "account");

		Query query = em.createNamedQuery(queryName);
		query.setParameter("account_id", account.getId());
		BigDecimal rawResult = (BigDecimal) query.getSingleResult();

		return new Money(rawResult);
	}

	/**
	 * Расчитывает изменение баланса лицевого счета между двумя указанными датами. Результат представлен в виде
	 * начислений между двумя датами (debet), списаний между двумя датами (credit) и разницы (delta). При расчете
	 * игнорируются зарезервированные средства. Т.е. расчет выполняется по фактическим совершенным начислениям и
	 * списаниям. Неблокирующая операция
	 * 
	 * @param account
	 *            - лицевой счет
	 * @param dateInclusive
	 *            - начальное датовремя. Транзакции, выполненные в это датовремя включаются в выборку
	 * @param dateExclusive
	 *            - конечное датовремя. Транзакции, выполненные в это датовремя (с точностью до миллисекунд) исключаются
	 *            из выборки
	 * 
	 * @return изменения лицевого счета. Всегда не null
	 */
	@NamedNativeQuery(name = QN_GET_BALANCE_DELTA, query = "SELECT * FROM system.get_balance_delta(:account_id, :date_inclusive, :date_exclusive)")
	public BalanceDelta getBalanceDelta(PersonalAccount account, Date dateInclusive, Date dateExclusive) {
		checkRequiredArgument(account, "account");
		checkRequiredArgument(dateInclusive, "dateInclusive");
		checkRequiredArgument(dateExclusive, "dateExclusive");

		Query query = em.createNamedQuery(QN_GET_BALANCE_DELTA);
		query.setParameter("account_id", account.getId());
		query.setParameter("date_inclusive", dateInclusive);
		query.setParameter("date_exclusive", dateExclusive);
		Object rawResult = query.getSingleResult();

		return BalanceDelta.fromQueryRawResult(rawResult);
	}

	/**
	 * Расчитывает баланс лицевого счета на указанную дату, не включая ее. При расчете игнорируются зарезервированные
	 * средства. Т.е. расчет выполняется по фактическим совершенным начислениям и списаниям. Неблокирующая операция
	 * 
	 * @param account
	 *            - лицевой счет
	 * @param dateExclusive
	 *            - конечное датовремя. Транзакции, выполненные в это датовремя (с точностью до миллисекунд) исключаются
	 *            из выборки
	 * 
	 * @return баланс лицевого счета на начало указанного момента времени. Всегда не null
	 */
	@NamedNativeQuery(name = QN_GET_BALANCE_BEFORE, query = "SELECT system.get_balance_before(:account_id, :date_exclusive)")
	public Money getBalanceBefore(PersonalAccount account, Date dateExclusive) {
		checkRequiredArgument(account, "account");
		checkRequiredArgument(dateExclusive, "dateExclusive");

		Query query = em.createNamedQuery(QN_GET_BALANCE_BEFORE);
		query.setParameter("account_id", account.getId());
		query.setParameter("date_exclusive", dateExclusive);
		BigDecimal rawResult = (BigDecimal) query.getSingleResult();

		return new Money(rawResult);
	}

	/**
	 * Выполняет проверку баланса для указанной подписки по указанному плану. При проверке баланса вызывающий может
	 * потребовать эксклюзивный доступ к лицевому счету. Это означает, что до конца транзакции, в которой выполняется
	 * проверка баланса еще раз проверить баланс будет невозможно. Данное условие необходимо для гарантии, что
	 * расчитанный баланс при положительной проверки не изменится до тех пор, пока не будет создан инвойс.
	 * <p>
	 * Показалось, что проверка баланса как раз отлично ложится в текущий сервис по работе с балансом лицевого счета,
	 * поэтому разместил этот метод здесь, не смотря на то, что это создало зависимость к подписке и планам. Возможно, в
	 * будущем, нужно будет вынести куда-то еще.
	 * 
	 * @param subscription
	 *            - подписка
	 * @param plan
	 *            - план следующего инвойса для указанной подписки
	 * @param exclusiveAccess
	 *            - если true, то будет обеспечен эксклюзивный доступ к лицевому счету до конца транзакции
	 * 
	 * @return результат проверки баланса
	 */
	public BalanceCheckingResult checkBalance(Subscription subscription, InvoicePlan plan, boolean exclusiveAccess) {
		return checkBalance(subscription, null, plan, exclusiveAccess);
	}

	/**
	 * Выполняет проверку баланса для указанной подписки по указанному новому плану, предназначенному для обновления
	 * текущего плана. При проверке баланса вызывающий также может потребовать эксклюзивный доступ к лицевому счету. Это
	 * означает, что до конца транзакции, в которой выполняется проверка баланса еще раз проверить баланс будет
	 * невозможно. Данное условие необходимо для гарантии, что расчитанный баланс при положительной проверки не
	 * изменится до тех пор, пока не будет создан инвойс.
	 * <p>
	 * Показалось, что проверка баланса как раз отлично ложится в текущий сервис по работе с балансом лицевого счета,
	 * поэтому разместил этот метод здесь, не смотря на то, что это создало зависимость к подписке и планам. Возможно, в
	 * будущем, нужно будет вынести куда-то еще.
	 * 
	 * @param subscription
	 *            подписка
	 * @param oldPlan
	 *            план текущего, действующего инвойса для указанной подписки
	 * @param newPlan
	 *            план нового, пересчитанного инвойса для указанной подписки
	 * @param exclusiveAccess
	 *            если true, то будет обеспечен эксклюзивный доступ к лицевому счету до конца транзакции
	 * 
	 * @return результат проверки баланса
	 */
	public BalanceCheckingResult checkBalance(Subscription subscription, InvoicePlan oldPlan, InvoicePlan newPlan,
			boolean exclusiveAccess) {

		PersonalAccount account = subscription.getPersonalAccount();
		if (exclusiveAccess) {
			log.debugv("Required exclusive access to personal account {0}", account);
			lock(account);
		}

		return checkBalance(account, getAvailableBalance(account), createConfig(subscription, oldPlan, newPlan));
	}

	/**
	 * Выполняет проверку баланса для указанных подписок одного лицевого счета (одинаковый лицевой счет для всех
	 * указанных подписок гарантируется проверкой {@link #checkSubscriptionsAccount(List) checkSubscriptionsAccount}).
	 * Текущий метод проверки баланса предполагается использовать для обработки таких случаев, как массовые
	 * автоматические включения при пополнении счетов абонентов или любых аналогичных задач.
	 * <p>
	 * Общая логика метода: для каждой подписки из переданного списка при помощи указанной функции расчета плана будет
	 * произведен стандартный {@link #checkBalance(PersonalAccount, Money, BalanceCheckingConfig) расчет баланса}. Если
	 * функция расчета плана ближайшего списания вернет для какой либо подписки null, то эта подписка исключается и не
	 * рассматривается как значимая для проверки. Перед началом проверки расчитывается доступный баланс лицевого счета,
	 * после каждой обработанной подписки закэшированный ранее баланс доступных средств на лицевом счете уменьшается на
	 * требуемую сумму {@link BalanceCheckingResult из результата проверки}. Примечательно, что требуемая сумма может
	 * равняться нулю, при этом план будет положительным. Этот случай корректен и обусловлен схемой списания без
	 * резервирования, когда требуемая сумма на лицевом счете не проверяется в начале периода.
	 * <p>
	 * Полученный результат проверки баланса требует дальнейшего анализа, в частности, какие-то подписки на лицевом
	 * счете могут быть протарифицированы, а какие-то нет. Или, например, нужно включить какие-то подписки в
	 * соответствии с приоритетом. Логика анализа этих результатов лежит за скоупом проверки баланса и этого сервиса
	 * 
	 * @param subscriptions
	 *            перечень подписок на одном лицевом счете, для которых нужно проверить, хватит ли текущих доступных
	 *            средств на этом лицевом счете для их тарификации или нет
	 * @param planningFunc
	 *            функция получения актуального плана для каждой подписки. Введена для устранения зависимости сервиса
	 *            проверки баланса от сервиса расчета планов тарификации
	 * @param exclusiveAccess
	 *            если true, то будет обеспечен эксклюзивный доступ к лицевому счету до конца транзакции
	 *
	 * @return аггрегированный результат проверки баланса лицевого счета для указанных подписок
	 */
	public Map<Subscription, BalanceCheckingResult> checkBalance(List<Subscription> subscriptions,
			Function<Subscription, InvoicePlan> planningFunc, boolean exclusiveAccess) {

		checkRequiredArgument(subscriptions, "subscriptions");
		checkRequiredArgument(planningFunc, "planningFunc");
		checkArgument(!subscriptions.isEmpty());

		PersonalAccount account = checkSubscriptionsAccount(subscriptions);
		if (exclusiveAccess) {
			log.debugv("Required exclusive access to personal account {0}", account);
			lock(account);
		}

		Money available = getAvailableBalance(account);
		Map<Subscription, BalanceCheckingResult> result = new HashMap<>();

		for (val subscription : subscriptions) {
			InvoicePlan plan = planningFunc.apply(subscription);
			if (plan != null) {
				log.debugv("Checking balance for subscription {0}", subscription);

				BalanceCheckingConfig checkingConfig = createConfig(subscription, null, plan);
				BalanceCheckingResult checkingResult = checkBalance(account, available, checkingConfig);
				available = available.subtract(checkingResult.getRequired());

				result.put(subscription, checkingResult);
			} else {
				log.debugv("Subscription {0} has no accounting plan on nearest future and was skipped", subscription);
			}
		}

		return result;
	}

	/**
	 * Гарантирует, что обязательно будет найден лицевой счет указанных подписок и этот счет будет общим для всех
	 * подписок. Если список подписок пуст, то будет брошено исключение. Если среди указанных подписок хотя бы одна
	 * будет принадлежать другому лицевому счету, то будет брошено исключение.
	 * 
	 * @param subscriptions
	 *            подписки, для которых необходимо проверить, что все они принадлежат одному лицевому счету
	 *
	 * @return общий лицевой счет для указанных подписок
	 */
	private PersonalAccount checkSubscriptionsAccount(List<Subscription> subscriptions) {
		PersonalAccount account = null;
		for (val subscription : subscriptions) {
			if (account == null) {
				account = subscription.getPersonalAccount();
			} else {
				checkArgument(Objects.equals(account, subscription.getPersonalAccount()));
			}
		}
		checkState(account != null);
		return account;
	}

	/**
	 * Создает конфигурацию для функции проверки баланса
	 * 
	 * @param subscription
	 *            подписка, для тарификации которой нужно проверить баланс
	 * @param oldPlan
	 *            старый план тарификации, необходимо проинициализировать этот параметр в случае, если нужно будет
	 *            проверить баланс с учетом возможного пересчета списания по подписке, например, при досрочном закрытии
	 *            или при добавлении/удалении/изменении скидки
	 * @param newPlan
	 *            новый план тарификации, для удовлетворения которого и выполняется проверка баланса
	 *
	 * @return подготовленная конфигурация для проверки баланса
	 */
	private BalanceCheckingConfig createConfig(Subscription subscription, InvoicePlan oldPlan, InvoicePlan newPlan) {
		checkRequiredArgument(subscription, "subscription");
		checkRequiredArgument(newPlan, "newPlan");

		val config = new BalanceCheckingConfig();
		config.setReservingAvailable(subscription.getProvisionTerms().isReserveFunds());
		config.setSuspensionAvailable(subscription.getLifecycleQualifier() == SubscriptionLifecycleQualifier.FULL);
		config.setRequiredFunds(newPlan.summary().totalCost());

		if (newPlan.summary().modifier() != null) {
			config.setTrustInDebt(newPlan.summary().modifier().trustOnBalanceChecking());
		}

		if (oldPlan != null) {
			config.setRequiredFundsOld(oldPlan.summary().totalCost());
		}
		return config;
	}

	/**
	 * Общий метод для проверки баланса. На текущий момент используется только для проверки баланса при тарификации
	 * подписок, поэтому сделан protected.
	 * <p>
	 * Алгоритм проверки следующий:
	 * 
	 * <p>
	 * <strong>Для схемы с резервированием.</strong> Вычисляется
	 * {@link BalanceCheckingConfig#calculateActualAvailable(Money) количество доступных средств} на лицевом счете с
	 * учетом возможной компенсации уже зарезервированных средств при пересчете (A). Требуемое количество средств для
	 * резервирования (R). Определяется порог отключения лицевого счета (T). Если A - R >= T, то результат проверки
	 * {@link BalanceCheckingResolution#ALLOWED положительный}. В противном случае проверяется, допустимо ли
	 * {@link BalanceCheckingConfig#isDebtSupported() вгонять лицевой счет в минус}. Если да, то результат проверки
	 * {@link BalanceCheckingResolution#ALLOWED_WITH_DEBT условно положительный}, иначе результат
	 * {@link BalanceCheckingResolution#ALLOWED_WITH_DEBT отрицательный}
	 * 
	 * <p>
	 * <strong>Для схемы без резервирования.</strong> Количество средств, требуемых для инвойса, в этом варианте
	 * проверки не важно (т.к. платить будем потом, с конце периода). Определяется количество доступных средств на
	 * лицевом счете (A). Определяется порог отключения лицевого счета (T). Если A >= T, то результат проверки
	 * {@link BalanceCheckingResolution#ALLOWED положительный}. В противном случае проверяется, допустимо ли
	 * {@link BalanceCheckingConfig#isDebtSupported() вгонять лицевой счет в минус}. Если да, то результат проверки
	 * {@link BalanceCheckingResolution#ALLOWED_WITH_DEBT условно положительный}, иначе результат
	 * {@link BalanceCheckingResolution#ALLOWED_WITH_DEBT отрицательный}
	 * 
	 * @param account
	 *            лицевой счет
	 * @param available
	 *            доступные средства на лицевом счете
	 * @param config
	 *            конфигурация для выполнения проверки баланса и принятия решений
	 * 
	 * @return результат проверки баланса
	 */
	private BalanceCheckingResult checkBalance(PersonalAccount account, Money available, BalanceCheckingConfig config) {
		// FIXME написать тесты для проверки баланса, т.к. одна из наиболее важдных частей биллинга
		checkRequiredArgument(account, "account");
		checkRequiredArgument(available, "available");
		checkRequiredArgument(config, "config");

		Money factor;
		Money required = Money.ZERO;
		Money threshold = account.getThreshold() != null ? account.getThreshold() : Money.ZERO;

		boolean trustInDebt = false;
		boolean reservingScheme = config.isReservingAvailable();

		BalanceCheckingResolution resolution = ALLOWED;

		if (config.isReservingAvailable()) {
			log.debug("Reservation of funds is required! Calculating real cost requirements");
			required = config.getRequiredFunds();
			available = config.calculateActualAvailable(available);
			factor = available.subtract(required);
		} else {
			log.debug("Reservation of funds is not supported. Using simple algorithm (available >= threshold)");
			factor = available;
		}

		if (factor.compareRounded(threshold) < 0) {
			if (config.isDebtSupported()) {
				resolution = ALLOWED_WITH_DEBT;
				trustInDebt = config.isTrustInDebt();
			} else {
				resolution = DISALLOWED;
			}
		}

		log.debugv("Required funds  {0}", required);
		log.debugv("Available funds {0}", available);
		log.debugv("Threshold       {0}", threshold);
		log.debugv("Trust in debt   {0}", trustInDebt);
		log.debugv("Balance checking resolution: {0}", resolution);

		return new BalanceCheckingResult(required, available, threshold, trustInDebt, reservingScheme, resolution);
	}

	/**
	 * Блокирует указанный лицевой счет по пессимистическому сценарию с таймаутом в 5 мин
	 * 
	 * @param account
	 *            - лицевой счет для блокирования
	 */
	private void lock(PersonalAccount account) {
		Map<String, Object> properties = new HashMap<>();
		properties.put("javax.persistence.lock.timeout", 5 * 60 * 1000);
		em.lock(account, LockModeType.PESSIMISTIC_WRITE, properties);
	}

	@Getter
	@ToString
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	public static class BalanceDelta {
		private Money debet;
		private Money credit;
		private Money delta;

		private static BalanceDelta fromQueryRawResult(Object rawResult) {
			checkRequiredArgument(rawResult, "rawResult");
			checkArgument(rawResult instanceof BigDecimal[]);

			BigDecimal[] result = (BigDecimal[]) rawResult;
			checkState(result.length == 3);
			return new BalanceDelta(new Money(result[0]), new Money(result[1]), new Money(result[2]));
		}
	}

	/**
	 * Инкапсулирует результат проверки баланса лицевого счета. Содержит сведения о схеме расчета (с бронированием или
	 * без), требуемые средства (актуально только для схемы с бронированием), доступные средства на лицевом счете, порог
	 * отключения и итоговое {@link BalanceCheckingResolution решение по проверке}.
	 */
	@Getter
	@AllArgsConstructor(access = AccessLevel.PROTECTED)
	public static class BalanceCheckingResult {
		private Money required;
		private Money available;
		private Money threshold;

		private boolean trustInDebt;
		private boolean reservingScheme;

		private BalanceCheckingResolution resolution;
	}

	/**
	 * Решение по проверке баланса
	 * 
	 * @see #ALLOWED
	 * @see #ALLOWED_WITH_DEBT
	 * @see #DISALLOWED
	 */
	public enum BalanceCheckingResolution {

		/**
		 * Проверка баланса пройдена успешно и тарификация позволена.
		 */
		ALLOWED,

		/**
		 * Проверка баланса в целом не пройдена, однако существуют факторы, из-за которых мы не должны приостанавливать
		 * подписку. Например, подписка создана с упрощенным ЖЦ, в котором нет приостановки или подписка создана по
		 * доверительному периоду.
		 */
		ALLOWED_WITH_DEBT,

		/**
		 * Проверка баланса не пройдена, дальнейшая тарификация невозможна
		 */
		DISALLOWED;
	}

	@Getter
	@Setter
	protected static class BalanceCheckingConfig {
		private Money requiredFunds;
		private Money requiredFundsOld;

		private boolean reservingAvailable = true;
		private boolean suspensionAvailable = true;
		private boolean trustInDebt = false;

		/**
		 * Определяет доступные средства с учетом кейса пересчета, т.е. если указали, сколько денег требовалось раньше и
		 * сколько денег требуется сейчас, то необходимо к доступным средствам на текущий момент добавить средства,
		 * которые были зарезервированы ранее и уже с полученной суммой выполнять сравнение с новой требуемой суммой.
		 * Притвориться, что раньше ничего не было зарезервировано. Работает только для схемы с резервированием.
		 * 
		 * @param currentAvailable
		 *            - текущие доступные средства на лицевом счете
		 * 
		 * @return доступные средства без учета текущего резерва
		 */
		Money calculateActualAvailable(Money currentAvailable) {
			if (reservingAvailable && requiredFundsOld != null) {
				return currentAvailable.add(requiredFundsOld);
			}
			return currentAvailable;
		}

		/**
		 * Определяет, можно ли считать отрицательный баланс положительным результатом проверки. На текущий момент, при
		 * недостатке средств проверка будет положительной в том случае, если:
		 * <ul>
		 * <li>нас заставили считать любую проверку положительной. Например, есть доверительный период
		 * <li>подписка не поддерживает приостановку (упрощенный ЖЦ). В этом случае проверка будет условно положительной
		 * </ul>
		 * 
		 * @return true если отрицательный баланс будет можно считать допустимым для дальнейшей работы подписки
		 */
		boolean isDebtSupported() {
			return trustInDebt || !suspensionAvailable;
		}
	}
}