package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static com.google.common.base.Preconditions.checkState;
import static java.util.stream.Collectors.groupingBy;
import static ru.argustelecom.box.env.billing.period.PeriodBuilderService.chargingOf;
import static ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.ascendingOrder;
import static ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.containing;
import static ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPeriodModifier.ascendingByPriority;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.max;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;
import static ru.argustelecom.system.inf.chrono.DateUtils.before;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import org.jboss.logging.Logger;

import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPeriodModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPriceModifier;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.env.stl.period.UnboundedPeriod;

@NoArgsConstructor
public class InvoicePlannerConfig {
	private static final Logger log = Logger.getLogger(InvoicePlannerConfig.class);

	private Subscription subscription;

	private UnboundedPeriod boundaries = UnboundedPeriod.INFINITE;
	private LocalDateTime renewalDate;
	private InvoicePlan lastPlan;
	private LinkedList<InvoicePlanPeriodModifier> periodModifiers = new LinkedList<>();
	private LinkedList<InvoicePlanPriceModifier> priceModifiers = new LinkedList<>();
	private boolean allowPrimaryActivation;
	private boolean requireResult = true;

	/**
	 * Подписка, для которой должно быть выполнено планирование начислений
	 */
	public Subscription subscription() {
		return subscription;
	}

	/**
	 * Шоткат для доступа к дате начала подписки. Не может быть null
	 */
	public LocalDateTime subscriptionStart() {
		return toLocalDateTime(subscription.getValidFrom());
	}

	/**
	 * Шоткат для доступа к дате окончания подписки. Актуально только для срочных подписок, поэтому может быть null
	 */
	public LocalDateTime subscriptionEnd() {
		return subscription.getValidTo() != null ? toLocalDateTime(subscription.getValidTo()) : null;
	}

	/**
	 * Границы планирования. Если при конфигурировании планировщика период не указан или указан период с нулевыми
	 * границами, то будет использован период, ассоциированный с полным временнЫм отрезком
	 */
	public UnboundedPeriod boundaries() {
		return boundaries;
	}

	/**
	 * Планируемая дата возобновления подписки, на тот случай, если она приостановлена. Т.е. если подписка была
	 * приостановлена неважно по какой причине, то будет запланровано ее возобновление с этой даты. Если не указана, то
	 * кейс возобновления тарификации будет не достижим. По умолчанию не указана
	 */
	public LocalDateTime renewalDate() {
		return renewalDate;
	}

	/**
	 * Позволяет рассматривать подписку в состоянии "Оформление" как допустимую к первичной активации. По умолчанию
	 * первичная активация запрещена
	 */
	public boolean allowPrimaryActivation() {
		return allowPrimaryActivation;
	}

	/**
	 * Если true, то принимающий очень рассчитывает получить результат и если мы не смогли его сгенерировать, то
	 * планирование пошло не так. Такая ситуация возможна в следующих случаях:
	 * <ul>
	 * <li>Не указан предыдущий план для подписки, состояние которой предполагает наличие этого предыдущего плана
	 * <li>Не указана дата возобновления тарификации, в то время, как подписка находится в состоянии приостановки
	 * <li>Подписка в состоянии приостановки имеет открытый инвойс
	 * </ul>
	 */
	public boolean requireResult() {
		return requireResult;
	}

	/**
	 * Список модификаторов периода планируемых инвойсов. Если не указаны модификаторы периода, то планировщик будет
	 * использовать их для разделения инвойсов на несколько по этим модификаторам. Временные границы модификаторов
	 * периодов не могут пересекаться, поэтому планировщик перед началом планирования обязательно выполнит проверку.
	 * <p>
	 * По умолчанию не указаны
	 */
	public List<InvoicePlanPeriodModifier> periodModifiers() {
		return periodModifiers;
	}

	/**
	 * Определяет модификатор периода, в который попадает указанная poi.
	 * 
	 * @see InvoicePlanModifier#containing(Date)
	 */
	public InvoicePlanPeriodModifier periodModifier(LocalDateTime poi) {
		// @formatter:off
		return !periodModifiers.isEmpty() 
			? periodModifiers.stream().filter(containing(fromLocalDateTime(poi))).findFirst().orElse(null)
			: null;		
		// @formatter:on
	}

	/**
	 * Список модификаторов стоимости планируемых инвойсов. Непосредственно планировщиком не анализируется, вся работа с
	 * этими модификаторами происходит уже внутри InvoicePlanBuilder
	 * <p>
	 * По умолчанию не указаны
	 */
	public List<InvoicePlanPriceModifier> priceModifiers() {
		return priceModifiers;
	}

	/**
	 * Шоткат для доступа к политике округления, указанной в условиях предоставления текущей подписки. Если подписка не
	 * указана, то вернется значение по умолчанию, равное UP
	 */
	public RoundingPolicy roundingPolicy() {
		return subscription.getProvisionTerms().getRoundingPolicy();
	}

	/**
	 * Позволяет получить период списания текущей подписки для указанной даты интереса
	 */
	public ChargingPeriod chargingPeriod(Date poi) {
		return chargingOf(subscription, poi);
	}

	/**
	 * Позволяет получить период списания текущей подписки для указанной даты интереса
	 */
	public ChargingPeriod chargingPeriod(LocalDateTime poi) {
		return chargingOf(subscription, poi);
	}

	/**
	 * Возвращает самый последний план из списка предыдущих планов текущей подписки. Последний план из списка планов
	 * обязательно должен быть действительно последним планом подписки, даже если этот план не попадает в указанный
	 * интервал планирования
	 */
	public InvoicePlan lastPlan() {
		return lastPlan;
	}

	/**
	 * Смотри {@linkplain #maxOfChargingStartAndPoi(LocalDateTime)}
	 * 
	 * @see #maxOfChargingStartAndPoi(LocalDateTime)
	 */
	public LocalDateTime maxOfChargingStartAndPoi(Date poi) {
		checkRequiredArgument(poi, "poi");
		return maxOfChargingStartAndPoi(toLocalDateTime(poi));
	}

	/**
	 * Если период планирования ограничен слева, т.е. по нижней границе временного периода, то нашему планировщику явно
	 * сказано, что вызывающему планирование на текущий момент безразлично, что было до этой даты. Следовательно,
	 * планировщик и не должен рассматривать планы до этой даты (левая или нижняя граница планирования). Но эта граница
	 * планирования может приходиться на середину периода списания, поэтому план, который пересекается с этой границей
	 * потенциально интересен. Для того, чтобы определить с какой даты реально необходимо начать планирование необходимо
	 * выбрать максимальную из дат: 1) нижняя граница периода списания, на который приходится нижняя граница
	 * планирования и 2) переданная в качестве аргумента poi
	 * <p>
	 * Если poi не указана, то будет спровоцировано исключение.
	 */
	public LocalDateTime maxOfChargingStartAndPoi(LocalDateTime poi) {
		checkRequiredArgument(poi, "poi");
		if (boundaries.hasLowerBound() && boundaries.beforeLowerBound(poi)) {
			return max(poi, chargingPeriod(boundaries.lowerBound()).startDateTime());
		}
		return poi;
	}

	public boolean beforeSubscriptionEnd(LocalDateTime poi) {
		LocalDateTime subscriptionEnd = subscriptionEnd();
		return subscriptionEnd != null ? poi.isBefore(subscriptionEnd) : true;
	}

	public void cleanModifiers() {
		this.periodModifiers.clear();
		this.priceModifiers.clear();
	}

	/**
	 * Выполняет проверку конфигурации планирования и подготавливает внутреннее состояние для дальнейшей работы. В
	 * частности, выполняет сортировку модификаторов периода по возрастанию.
	 * <p>
	 * На момент написания этого кода действовало соглашение, что модификаторы периода не могут пересекаться. В этом
	 * методе проверяется это утверждение (в качестве осуществления политики FailFast)
	 */
	public void prepare() {
		checkRequiredArgument(subscription, "subscription");
		checkRequiredArgument(subscription.getValidFrom(), "subscription.validFrom");

		if (!periodModifiers.isEmpty()) {
			checkModifiersBoundaries(periodModifiers.stream());

			periodModifiers.sort(ascendingByPriority().thenComparing(ascendingOrder()));
			checkIntersections(periodModifiers);
		}

		if (!priceModifiers.isEmpty()) {
			checkModifiersBoundaries(priceModifiers.stream());
		}
	}

	private void checkModifiersBoundaries(Stream<? extends InvoicePlanModifier> modifiers) {
		// @formatter:off
		Optional<? extends InvoicePlanModifier> modifierOpt = modifiers
				.filter(m -> m.getValidFrom() == null || m.getValidTo() == null)
				.findFirst();
		// @formatter:on

		if (modifierOpt.isPresent()) {
			checkState(false, "Modifier '%s' has one or both empty bounds", modifierOpt.get());
		}
	}

	private void checkIntersections(List<InvoicePlanPeriodModifier> modifiers) {
		Map<Integer, List<InvoicePlanPeriodModifier>> modifiersByPriority = modifiers.stream()
				.collect(groupingBy(InvoicePlanPeriodModifier::getPriority));

		modifiersByPriority.values().stream().forEach(this::checkIntersectionsWithinGroup);
	}

	private void checkIntersectionsWithinGroup(List<InvoicePlanPeriodModifier> groupedModifiers) {
		groupedModifiers.sort(ascendingOrder());
		ListIterator<InvoicePlanPeriodModifier> it = groupedModifiers.listIterator();
		while (it.hasNext()) {
			InvoicePlanPeriodModifier cursor = it.next();
			if (it.nextIndex() < groupedModifiers.size()) {
				checkNextIntersections(cursor, groupedModifiers.listIterator(it.nextIndex()));
			}
		}
	}

	private void checkNextIntersections(InvoicePlanPeriodModifier cursor, Iterator<InvoicePlanPeriodModifier> nextIt) {
		while (nextIt.hasNext()) {
			InvoicePlanModifier checked = nextIt.next();
			boolean intersects = before(checked.getValidFrom(), cursor.getValidTo());
			checkState(!intersects, "Intersection of Period modifiers are forbidded: %s ~ %s", cursor, checked);
		}
	}

	protected void outputDebugInfoOnError() {
		log.errorv("InvoicePlanner.subscription      : {0}", subscription);
		log.errorv("InvoicePlanner.subscriptionState : {0}", subscription != null ? subscription.getState() : null);
		log.errorv("InvoicePlanner.boundaries        : {0}", boundaries);
		log.errorv("InvoicePlanner.renewalDate       : {0}", renewalDate);
		log.errorv("InvoicePlanner.lastPlan          : {0}", lastPlan);
		log.errorv("InvoicePlanner.periodModifiers   : {0}", periodModifiers);
		log.errorv("InvoicePlanner.priceModifiers    : {0}", priceModifiers);
		log.errorv("InvoicePlanner.allowActivation   : {0}", allowPrimaryActivation);
		log.errorv("InvoicePlanner.requiredResult    : {0}", requireResult);
	}

	// ************************************************************************************************************
	// Configuration Boilerplate
	// ************************************************************************************************************

	/**
	 * Устанавливает подписку, для которой необходимо выполнить планирование списаний
	 */
	public InvoicePlannerConfig setSubscription(Subscription subscription) {
		this.subscription = subscription;
		return this;
	}

	/**
	 * Устанавливает границы планирования. Если не указаны, то будет восстановлено значение по умолчанию, т.е.
	 * "бесконечность" или неограниченный период
	 */
	public InvoicePlannerConfig setBoundaries(UnboundedPeriod boundaries) {
		this.boundaries = boundaries != null ? boundaries : UnboundedPeriod.INFINITE;
		return this;
	}

	/**
	 * Устанавливает границы планирования. Если не указаны, то будет восстановлено значение по умолчанию, т.е.
	 * "бесконечность" или неограниченный период
	 */
	public InvoicePlannerConfig setBoundaries(Date lowerBound, Date upperBound) {
		this.boundaries = UnboundedPeriod.of(lowerBound, upperBound);
		return this;
	}

	/**
	 * Устанавливает границы планирования. Если не указаны, то будет восстановлено значение по умолчанию, т.е.
	 * "бесконечность" или неограниченный период
	 */
	public InvoicePlannerConfig setBoundaries(LocalDateTime lowerBound, LocalDateTime upperBound) {
		this.boundaries = UnboundedPeriod.of(lowerBound, upperBound);
		return this;
	}

	/**
	 * Устанавливает дату возобновления тарификации для приостановленной подписке. Эта дата будет использована в кейсе
	 * возобновления тарификации как кандидат для даты возобновления инвойса
	 */
	public InvoicePlannerConfig setRenewalDate(Date renewalDate) {
		this.renewalDate = toLocalDateTime(renewalDate);
		return this;
	}

	/**
	 * Устанавливает дату возобновления тарификации для приостановленной подписке. Эта дата будет использована в кейсе
	 * возобновления тарификации как кандидат для даты возобновления инвойса
	 */
	public InvoicePlannerConfig setRenewalDate(LocalDateTime renewalDate) {
		this.renewalDate = renewalDate;
		return this;
	}

	/**
	 * Устанавливает флаг, на основании которого планировщик сможет принять решение о создании плана для активации
	 * подписки (если true)
	 */
	public InvoicePlannerConfig setAllowPrimaryActivation(boolean allowPrimaryActivation) {
		this.allowPrimaryActivation = allowPrimaryActivation;
		return this;
	}

	/**
	 * Устанавливает флаг, на основании которого планировщик при получении отрицательного результата планирования сможет
	 * решить, правомерно это или нет. По умолчанию true, т.е. результат должен быть получен
	 */
	public InvoicePlannerConfig setRequireResult(boolean requireResult) {
		this.requireResult = requireResult;
		return this;
	}

	/**
	 * Устанавливает последний план текущей подписки. Это очень важный параметр, используемый для определения
	 * большинства кейсов планирования. Если подписка хоть когда-то тарифицировалась, то этот параметр должен быть
	 * установлен, в противном случае вероятность невозможности планирования стремится к бесконечности
	 */
	public InvoicePlannerConfig setLastPlan(InvoicePlan lastPlan) {
		this.lastPlan = lastPlan;
		return this;
	}

	/**
	 * Добавляет коллекцию модификаторов периода для будущих инвойсов. Каждый модификатор из этой коллекции будет
	 * скопирован во внутреннюю структуру конфигурации
	 */
	public InvoicePlannerConfig addPeriodModifiers(Collection<InvoicePlanPeriodModifier> periodModifiers) {
		if (periodModifiers != null) {
			periodModifiers.forEach(this::addPeriodModifier);
		}
		return this;
	}

	/**
	 * Добавляет модификатор периода для будущих инвойсов
	 */
	public InvoicePlannerConfig addPeriodModifier(InvoicePlanPeriodModifier periodModifier) {
		if (periodModifier != null && !this.periodModifiers.contains(periodModifier)) {
			this.periodModifiers.add(periodModifier);
		}
		return this;
	}

	/**
	 * Удаляет модификатор периода для будущих инвойсов
	 */
	public InvoicePlannerConfig removePeriodModifier(InvoicePlanPeriodModifier periodModifier) {
		this.periodModifiers.remove(periodModifier);
		return this;
	}

	/**
	 * Добавляет коллекцию модификаторов стоимости для будущих инвойсов. Каждый модификатор из этой коллекции будет
	 * скопирован во внутреннюю структуру конфигурации
	 */
	public InvoicePlannerConfig addPriceModifiers(Collection<InvoicePlanPriceModifier> priceModifiers) {
		if (priceModifiers != null) {
			priceModifiers.forEach(this::addPriceModifier);
		}
		return this;
	}

	/**
	 * Добавляет модификатор стоимости для будущих инвойсов
	 */
	public InvoicePlannerConfig addPriceModifier(InvoicePlanPriceModifier priceModifier) {
		if (priceModifier != null && !this.priceModifiers.contains(priceModifier)) {
			this.priceModifiers.add(priceModifier);
		}
		return this;
	}

	/**
	 * Добавляет модификатор стоимости для будущих инвойсов
	 */
	public InvoicePlannerConfig removePriceModifier(InvoicePlanPriceModifier priceModifier) {
		this.priceModifiers.remove(priceModifier);
		return this;
	}

}