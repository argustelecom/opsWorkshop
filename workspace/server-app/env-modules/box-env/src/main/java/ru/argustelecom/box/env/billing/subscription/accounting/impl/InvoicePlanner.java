package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.MILLIS;
import static ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlannerCase.IMPLICIT_PROHIBITION;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.minDate;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import org.jboss.logging.Logger;

import lombok.NoArgsConstructor;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPeriodModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanTimeline;
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;

/**
 * Планировщик будущих списаний (ТОЛЬКО БУДУЩИХ!).
 */
@NoArgsConstructor
public class InvoicePlanner {
	private static final Logger log = Logger.getLogger(InvoicePlanner.class);

	private InvoicePlannerConfig config;
	private InvoicePlanBuilder builder;

	public InvoicePlanner(InvoicePlannerConfig config) {
		if (config != null) {
			updateConfiguration(config);
		}
	}

	/**
	 * Обновляет текущую конфигурацию планировщика. Предназначено для облегчения повторного использования при массовом
	 * планировании нескольких подписок (планирование будущих списаний при построении счета)
	 */
	public void updateConfiguration(InvoicePlannerConfig config) {
		checkRequiredArgument(config, "InvoicePlannerConfiguration");
		this.config = config;
		this.config.prepare();
		this.builder = initializeBuilder();
	}

	/**
	 * Определяет {@linkplain InvoicePlannerCase сценарий планирования} и расчитывает на основании этого сценария дату
	 * начала планирования. Если сценарий не определен ({@link InvoicePlannerCase#IMPLICIT_PROHIBITION}), то проверяет
	 * допустимость такого состояния на основании флага {@linkplain InvoicePlannerConfig#requireResult()} и в случае
	 * недопустимости бросает соответствующее исключение.
	 * <p>
	 * В случае сценария {@linkplain InvoicePlannerCase#EXPLICIT_PROHIBITION} метод вернет null и этот null должен в
	 * дальнейшем обрабатываться как корректное значение, сигнализирующее, что планирование не требуется. Аналогичный
	 * результат можно ожидать, если сценарий {@linkplain InvoicePlannerCase#IMPLICIT_PROHIBITION} и это явно разрешено
	 * конфигурацией планировщика. В данном случае считается, что вызывающий понимает, что и зачем он делает и готов
	 * нести ответственность за свои действия
	 * 
	 * @return дату, с которой необходимо начинать планирование списаний или null, если дальнейшее планирование не имеет
	 *         смысла
	 */
	public LocalDateTime plannedStartDate() {
		checkState(config != null);

		// Определяем кейс планирования
		InvoicePlannerCase planningCase = getPlanningCase();
		checkState(planningCase != null);

		log.debugv("Defined planning case is: {0}", planningCase);

		if (planningCase.isProhibited()) {
			if (planningCase == IMPLICIT_PROHIBITION && config.requireResult()) {
				config.outputDebugInfoOnError();
				throw new IllegalStateException("Could not identify the case of planning when result is required");
			}

			// Кейс не предполагает планирования вообще (явный или не явный запрет на планирование). Попали сюда,
			// значит результат правомерный, можно смело возвращать null
			return null;
		}

		// Если попали сюда, значит кейс планирования определился и мы можем расчитать дату начала первого плана
		// в текущем планировании. Важно, эта дата может быть произвольной, т.е. не выровненной по границам БЕ
		// Выравнивание и округление будет выполнено позднее при планировании.
		// Если определили кейс планирования, то должны гарантировать плановую дату начала
		LocalDateTime result = planningCase.calculatePlannedStartDate(config);
		checkState(result != null);

		log.debugv("Defined planning start date: {0}", result);
		return result;
	}

	/**
	 * Определяет {@linkplain #plannedStartDate() дату планирования} и делегирует расчет
	 * {@linkplain #createFuturePlans(LocalDateTime)}. Шоткат, объединяющий эти два метода. Дополнительно способен
	 * быстро вернуть пустой результат в случае, если дальнейшее планирование не предполагается.
	 */
	public List<InvoicePlan> createFuturePlans() {
		LocalDateTime plannedStart = plannedStartDate();
		if (plannedStart == null) {
			// null может вернуться только в случае запрета на планирование. Если запрет явный, то все нормально и
			// отсутствие результата допустимо. Если кейс не явный, то что-то пошло не так, однако, извне нам сказали,
			// что на отсутствие результата можно забить. В противном случае (забить нельзя) мы отвалимся раньше на
			// этапе определения плановой даты начала.
			// Если резюмировать, то ожидания следующие:
			// * метод вернул null, значит это допустимо и результат от нас не обязателен
			// * если результат обязателен, но не смогли определить плановую дату начала, то ошибка будет
			// спровоцирована ранее
			log.debug("Planning is not available");
			return Collections.emptyList();
		}

		return createFuturePlans(plannedStart);
	}

	/**
	 * Выполняет планирование будущих списаний в пределах конфигурации. Будет порождено столько планов, сколько
	 * требуется для закрытия всего интервала планирования, определенного параметром
	 * {@linkplain InvoicePlannerConfig#boundaries()}. Однако, если не указана правая граница планирования (т.е. плюс
	 * бесконечность), то планирование может выродиться в бесконечный цикл. Поэтому если правая граница не указана, то
	 * будет порожден только один ближайший план.
	 * 
	 * @param plannedStart
	 *            - дата начала планирования, должна быть получена из соответствующего сценария планирования. Если эта
	 *            дата расчитана каким либо другим способом, то вся ответственность за корректность начала планирования
	 *            лежит на вызывающем.
	 */
	public List<InvoicePlan> createFuturePlans(LocalDateTime plannedStart) {
		checkRequiredArgument(plannedStart, "plannedStart");

		if (!config.beforeSubscriptionEnd(plannedStart)) {
			// Во время определения кейса планирования никак не рассматривалась дата subscription.validTo, потому что
			// она должна рассматриваться относительно какой-то конкретной даты (даты начала первого плана в текущей
			// итерации), которая получается как раз благодаря кейсу. В итоге мы могли получить дату начала первого
			// плана, превышающую дату окончания срочной подписки. В этом случае планирование вообще не имеет смысла,
			// поэтому его необходимо прекратить
			log.debugv("Subscription is over: {0} > {1}", plannedStart, config.subscriptionEnd());
			return Collections.emptyList();
		}

		if (config.boundaries().afterUpperBound(plannedStart)) {
			// Кейс планирования определился, однако планировать просто некуда, потому что период планирования ограничен
			// и план, который мы получим будет выходить за его правую границу. В этом случае должны также вернуть
			// пустую коллекцию. Такое может произойти, например, при выставлении счета: укажут период выставления
			// счета и дату выставления счета превышающую правую границу этого периода.
			log.debugv("Planning period is over: {0} > {1}", plannedStart, config.boundaries().upperBound());
			return Collections.emptyList();
		}

		// Если попали сюда, то у нас будет хотя бы один результат
		LinkedList<InvoicePlan> result = new LinkedList<>();

		// Определяются параметры для первого плана в текущей итерации планирования
		InvoicePlan previousPlan = config.lastPlan();
		ChargingPeriod chargingPeriod = config.chargingPeriod(plannedStart);
		InvoicePlanPeriodModifier periodModifier = config.periodModifier(plannedStart);
		LocalDateTime plannedEnd = nextPlannedEnd(chargingPeriod, config.subscription(), periodModifier);

		do {

			builder.setChargingPeriod(chargingPeriod);
			builder.setPeriodModifier(periodModifier);
			builder.setPreviousPlan(previousPlan);
			builder.setPlannedStart(plannedStart);
			builder.setPlannedEnd(plannedEnd);

			result.add(builder.build());

			// Следующее план будет начинаться через одну миллисекунду после окончания текущего плана и на самом деле
			// планирование может не состояться, т.к. мы выйдем за границы нашего интереса
			// см. #canPlanNext
			plannedStart = plannedEnd.plus(1, MILLIS);
			previousPlan = result.getLast();
			chargingPeriod = config.chargingPeriod(plannedStart);
			periodModifier = config.periodModifier(plannedStart);
			plannedEnd = nextPlannedEnd(chargingPeriod, config.subscription(), periodModifier);

			log.debugv("Invoice plan created: {0}", previousPlan);

		} while (canPlanNext(plannedStart));

		return result;
	}

	/**
	 * Создает билдер инвойсов и инициализирует его общими параметрами, неизменными для всех планирований, которые
	 * потенциально будут выполнены текущим планировщиком по текущей конфигурации
	 */
	private InvoicePlanBuilder initializeBuilder() {
		// @formatter:off
		return new InvoicePlanBuilder()
			.setTimeline(InvoicePlanTimeline.FUTURE)
			.setRoundingPolicy(config.roundingPolicy())
			.addPriceModifier(config.priceModifiers());
		// @formatter:on
	}

	/**
	 * Определяет сценарий планирования. Обязательно должен определиться хотя бы один сценарий. Метод никогда не вернет
	 * null
	 */
	private InvoicePlannerCase getPlanningCase() {
		for (InvoicePlannerCase planningCase : InvoicePlannerCase.values()) {
			if (planningCase.isComplying(config)) {
				return planningCase;
			}
		}
		throw new IllegalStateException("InvoicePlannerCase has no default scenario");
	}

	/**
	 * Определяет планируемую дату окончания следующего инвойса. Выбирается минимальная дата из
	 * <ul>
	 * <li>окончания текущего периода списания
	 * <li>окончания подписки (если подписка срочная)
	 * <li>окончания модификатора периода (если он определен для текущего интервала планирования)
	 * </ul>
	 */
	public static LocalDateTime nextPlannedEnd(ChargingPeriod chargingPeriod, Subscription subscription,
			InvoicePlanPeriodModifier periodModifier) {

		Date cpEnd = chargingPeriod.endDate();
		Date subscriptionEnd = subscription.getValidTo() != null ? subscription.getValidTo() : cpEnd;
		Date modifierEnd = periodModifier != null ? periodModifier.getValidTo() : cpEnd;

		return toLocalDateTime(minDate(cpEnd, subscriptionEnd, modifierEnd));
	}

	/**
	 * Определяет возможность продолжения планирования. Продолжать планирование возможно если:
	 * <ul>
	 * <li>планировщик имеет ограничение для планирования по правой границе (гарантирует, что не уйдем в бесконечный
	 * цикл, пока на сервере не закончится память)
	 * <li>дата начала следующего плана не превышает эту правую границу планирования (если превышает, то мы
	 * запланировали все, что от нас просили и нам необходимо остановиться)
	 * <li>дата начала следующего плана не превышает дату окончания подписки (если она срочная)
	 * </ul>
	 */
	private boolean canPlanNext(LocalDateTime nextPlannedStart) {
		checkRequiredArgument(nextPlannedStart, "nextPlannedStart");
		if (config.boundaries().hasUpperBound() && config.boundaries().beforeUpperBound(nextPlannedStart)) {
			return config.beforeSubscriptionEnd(nextPlannedStart);
		}
		return false;
	}

}