package ru.argustelecom.box.env.billing.subscription.accounting.impl;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkState;
import static java.time.temporal.ChronoUnit.MILLIS;
import static java.time.temporal.ChronoUnit.NANOS;
import static java.util.Comparator.comparing;
import static java.util.stream.Collectors.toCollection;
import static ru.argustelecom.box.env.billing.period.PeriodBuilderService.chargingOf;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Predicate;

import com.google.common.collect.Range;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlan;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPeriodModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanModifier.InvoicePlanPriceModifier;
import ru.argustelecom.box.env.billing.subscription.accounting.InvoicePlanTimeline;
import ru.argustelecom.box.env.billing.subscription.accounting.impl.InvoicePlanImpl.InvoicePlannedPeriod;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.env.stl.period.PeriodType;
import ru.argustelecom.box.inf.chrono.ChronoUtils;

/**
 * Основной класс для расчета списаний. В нем сосредоточена вся работа с периодами тарификации, применением скидок,
 * расчетами. Результатом работы этого класса является InvoicePlan - специализированная структура для представления
 * одного факта расчета. Однозначно маппится на сущность Invoice.
 */
@NoArgsConstructor
public class InvoicePlanBuilder {

	private static final String INTERNAL_STATE_CORRUPTED = "Internal state is corrupted. See BOX-2273";

	private ChargingPeriod chargingPeriod;
	private RoundingPolicy roundingPolicy;
	private InvoicePlanTimeline timeline;
	private LocalDateTime plannedStart;
	private LocalDateTime plannedEnd;
	private Long invoiceId;

	private InvoicePlanPeriodModifier periodModifier;
	private LinkedList<InvoicePlanPriceModifier> priceModifiers = new LinkedList<>();

	private InvoicePlan previousPlan;

	/**
	 * Выполняет непосредственное построение плана списания с учетом InvoicePlanPriceModifiers. Ожидает, что
	 * plannedStart и plannedEnd указаны в соответствии с InvoicePlanPeriodModifier, для этого выполняет соответствующую
	 * проверку в самом начале, перед построением плана. Кроме этой проверки InvoicePlanPeriodModifier нигде больше в
	 * этом классе не участвует. plannedStart и plannedEnd должны быть именно планируемыми датами, а не датами
	 * тарификации.
	 * 
	 * <p>
	 * Алгоритм построения плана следующий:
	 * <ul>
	 * <li>Проверка текущего состояния билдера. Выполняется в {@link #validateInput()}
	 * <li>Определение эталонных дат тарификации для указанных плановых дат. Выполняется в {@link #roundBoundaries()}
	 * <li>Создание детализирующих периодов тарификации в соответствии с модификаторами стоимости. Выполняется в
	 * {@link #createDetails(Range)}
	 * <li>Итоговое суммирование и проверки соответствия детализированного периода с эталонным. Выполняется в
	 * {@link #createSummary(Range, List)}
	 * <li>Создание плана по расчитанным периодам. Выполняется в {@link #createPlan(InvoicePlanPeriodImpl, List)}
	 * <li>Проверка результата. На текущий момент примитивная. Выполняется в {@link #validateResult(InvoicePlanImpl)}
	 * </ul>
	 * 
	 * @return InvoicePlan
	 */
	public InvoicePlan build() {
		// ш1. Проверка текущего состояния билдера
		validateInput();

		// ш2. Определение границ тарификации (выравнивание по границам базовых единиц)
		Range<LocalDateTime> billingPeriodBoundaries = roundBoundaries();

		// ш3. Создание вложенных периодов тарификации, разбитых в соответствии с priceModifiers
		List<InvoicePlanPeriodImpl> details = createDetails(billingPeriodBoundaries);

		// ш4. Создание основного периода для плана (сумма всех вложенных периодов)
		InvoicePlanPeriodImpl summary = createSummary(billingPeriodBoundaries, details);

		// ш5. Непосредственное создание плана
		InvoicePlanImpl invoicePlan = createPlan(summary, details);

		// ш6. Проверка итогового результата
		return validateResult(invoicePlan);
	}

	/**
	 * Валидирует состояние билдера, проверяет применимость указанной конфигурации к текущему расчету. В основном
	 * проверяет, все ли обязательные для расчета параметры указаны
	 */
	void validateInput() {
		checkRequiredArgument(chargingPeriod, "chargingPeriod");
		checkRequiredArgument(roundingPolicy, "roundingPolicy");
		checkRequiredArgument(plannedStart, "plannedStart");
		checkRequiredArgument(plannedEnd, "plannedEnd");
		checkRequiredArgument(timeline, "timeline");

		checkArgument(chargingPeriod.contains(plannedStart), "Start date must be within %s", chargingPeriod);
		checkArgument(chargingPeriod.contains(plannedEnd), "End date must be within %s", chargingPeriod);

		// Не уверен, нужно ли делать такую проверку и всегда ли она будет допустимой
		if (periodModifier != null) {
			LocalDateTime modifierStart = toLocalDateTime(periodModifier.getValidFrom());
			LocalDateTime modifierEnd = toLocalDateTime(periodModifier.getValidTo());

			boolean plannedStartValid = plannedStart.isAfter(modifierStart) || plannedStart.isEqual(modifierStart);
			boolean plannedEndValid = plannedEnd.isBefore(modifierEnd) || plannedEnd.isEqual(modifierEnd);
			boolean modifierValid = plannedStartValid && plannedEndValid;

			checkArgument(modifierValid, "Invoice period must be within boundaries of period modifier");
		}
	}

	/**
	 * 
	 * @return
	 */
	Range<LocalDateTime> roundBoundaries() {
		PeriodType periodType = chargingPeriod.getType();
		LocalDateTime startOfInterest = chargingPeriod.accountingPeriod().startOfInterest();

		// Определение, является ли период предыдущего плана отрезком "нулевой длины" слева, т.е. периодом, у которого
		// датовремя начала равно датовремени конца и при этом датовремя совпадает с левой границей базовой единицы
		// Левые отрезки "нулевой длины" обычно заползают на следующую базовую единицу, которая в большинстве случаев
		// принадлежит следующему периоду списания. Почему так получается, см. комментарии ниже.
		boolean zeroOnLeft = false;
		if (previousPlan != null && previousPlan.summary().isZeroLength()) {
			LocalDateTime previousPlanStart = previousPlan.summary().startDateTime();
			Range<LocalDateTime> baseUnit = periodType.calculateBaseUnitBoundaries(startOfInterest, previousPlanStart);
			zeroOnLeft = Objects.equals(baseUnit.lowerEndpoint(), previousPlanStart);
		}

		LocalDateTime actualStart = plannedStart;
		LocalDateTime actualEnd = plannedEnd;

		// Если указан предыдущий план, то верно утверждение, что правая граница этого плана равна какой-либо из границ
		// правой базовой единицы этого плана. Если правая граница предыдущего плана превышает левую границу текущего
		// плана, то возникает необходимость сдвинуть левую границу текущего плана таким образом, чтобы избежать
		// наложений. Для этого рассмотрим два случая
		if (previousPlan != null && actualStart.compareTo(previousPlan.summary().endDateTime()) <= 0) {
			LocalDateTime previousPlanEnd = previousPlan.summary().endDateTime();

			// 1. Правая граница текущего плана превышает правую границу предыдущего плана, значит новый план однозначно
			// захватывает хотя бы одну базовую единицу тарификации и отрезок, полученный после сдвига левой границы
			// текущего плана, нужно будет округлять в соответствии с указанной политикой
			if (actualEnd.compareTo(previousPlanEnd) > 0) {
				// При определении новой актуальной границы текущего плана также необходимо рассмотреть два случая
				// 1.1. Предыдущий план был представлен временнЫм "отрезком нулевой длины" в начале базовой единицы. В
				// этом случае отрезок нулевой длины не должен рассматриваться как значимый (потому что у него нулевая
				// длина и он как бы занимает начало интересующей нас базовой единицы), т.е. этот отрезок не должен
				// приводить к смещению новой актуальной даты на 1мс от окончания правой границы предыдущего плана. Если
				// этим пренебречь, то новый план не начнется в начале базовой единицы, т.е. начнется со смещением на
				// 1мс, что может в итоге поломать все расчеты и привести к печальным последствиям. Поэтому отрезки
				// нулевой длины расположенные слева игнорируются. "Отрезок нулевой длины" в начале интересующей нас
				// базовой единицы может получиться только посредством округления вниз в случае, если и начало нового
				// инвойса и его окончание приходится на одну базовую единицу (далее по коду это станет понятным)
				// 1.2. Предыдущий план был представлен временнЫм "отрезком нулевой длины" в конце предыдущей базовой
				// единицы или был представлен нормальным временнЫм отрезком (с длиной один или более базовых единиц). В
				// этом случае, предыдущая базовая единица должна считаться полностью занятой и новый план необходимо
				// начать со следующей базовой единицы, т.е. к окончанию занятой базовой единицы небходимо прибавить
				// одну миллисекунду. Ожидается, что эта операция переместит аткуальное начало следующего периода четко
				// в начало следующей базовой единицы, при этом не будет никаких пересечений и наложений тарифицируемых
				// временнЫх отрезков.
				actualStart = zeroOnLeft ? previousPlanEnd : previousPlanEnd.plus(1, MILLIS);
			} else {
				// 2. Правая граница текущего плана не превышает либо равна правой границе предыдущего плана (не важно,
				// был ли это "отрезок нулевой длины" или нормальный временнОй отрезок с длиной больше либо равной одной
				// базовой единице. В этом случае мы должны считать, что текущий план начинается и заканчивается в одной
				// и той же базовой единице, которая уже тарифицирована ранее. А раз так, то мы должны в текущем плане
				// указать нулевую сумму (потому что "фактически" деньги уже были списаны за эту единицу). Однако, нам
				// необходимо сделать это таким образом, чтобы следующий план, если он будет, мог опираться на текущий в
				// аналогичных расчетах. Поэтому мы породим временнОй отрезок нулевой длины, только в правой границе
				// базовой единицы.
				// Если обобщить поведение временнЫх "отрезков с нулевой длиной", то
				// * Отрезок, совпадающий с левой границей базовой единицы, не компенсируется добавлением одной
				// миллисекунды к началу текущего периода
				// * Отрезок, совпадающий с правой границей базовой единицы, должен быть компенсирован добавлением одной
				// миллисекунды к началу текущего периода
				return Range.closed(previousPlanEnd, previousPlanEnd);
			}
		}

		// Если мы попали сюда, значит после устранения возможного наложения временнЫх периодов потенциальный период
		// тарификации может быть равным либо превышать одну базовую единицу. По утвержденному алгоритму округления
		// необходимо сдвинуть весь потенциальный временнОй отрезок влево на расстояние между началом базовой единицы и
		// началом отрезка, т.е. выровнять левую границу временнОго отрезка текущего плана по границе базовой единицы.
		// Следует обратить внимание, что если левая граница текущего плана и так совмещена с левой границей базовой
		// единицы, то расстояние будет равно нулю и никакого смещения фактически не произойдет (т.е. сдвига не будет
		// в 90% случаев формирования планов).
		Range<LocalDateTime> actualStartBu = periodType.calculateBaseUnitBoundaries(startOfInterest, actualStart);

		// Дельту будем вычислять в наносекундах для точности
		long delta = NANOS.between(actualStartBu.lowerEndpoint(), actualStart);
		if (delta > 0) {
			// Применять сдвиг целесообразно только в случае, если дельта существует
			actualStart = actualStartBu.lowerEndpoint();
			actualEnd = actualEnd.minusNanos(delta);
		}

		// Для округления безусловно расчитаем гранцы базовой единицы окончания текущего плана
		Range<LocalDateTime> actualEndBu = periodType.calculateBaseUnitBoundaries(startOfInterest, actualEnd);
		if (roundingPolicy == RoundingPolicy.UP) {
			// Если округляем вверх, то необходимо взять правую границу базовой единицы (для округления базовых единиц в
			// большую сторону). Даже если правая граница плана и так соответствовала правой границе базовой единицы.
			actualEnd = actualEndBu.upperEndpoint();
		} else {
			// При округлении вниз нам необходимо переместиться на правую границу предыдущей базовой единицы, но это
			// целесообразно делать только в том случае, если правая граница плана не совпадает с правой границей
			// базовой единицы (если совпадает, то округление не требуется)
			if (actualEnd.isBefore(actualEndBu.upperEndpoint())) {
				// В случае, если округляем вниз, и при этом начало и окончание плана приходится на одну базовую
				// единицу, то переход к окончанию предыдущей базовой единицы может привести к тому, что левая граница
				// плана будет превышать правую границу плана, что является некорректной ситуацией. При таком раскладе
				// нам необходимо породить отрезок нулевой длины с началом в текущей базовой единице. (см. комент выше)
				// Порождать отрезок нулевой длины в предыдущей базовой единице в этом случае некорректно, потому что
				// предыдущая базовая единица может находиться в предыдущем периоде списания или вообще в предыдущем
				// периоде расчета (если мы обрабатывали случай на границе двух периодов списания). При любом раскладе
				// мы должны остаться в текущем периоде списания "cp"
				actualEnd = ChronoUtils.max(actualEndBu.lowerEndpoint().minus(1, MILLIS), actualStart);
			}
		}

		return Range.closed(actualStart, actualEnd);
	}

	/**
	 * 
	 * @param boundaries
	 * @return
	 */
	List<InvoicePlanPeriodImpl> createDetails(Range<LocalDateTime> boundaries) {

		// Для начала необходимо определиться с модификаторами. Если модификаторы имеются, т.е. после всего
		// что мы с ними сделали для округления по границам базовых единиц, образания хвостов, "поглощения"
		// нулевым модификатором, выталкивания за границы нулевого модификатора...
		// Если после всего этого есть какой-то модификатор, то мы будем применять более сложный алгоритм
		// для расчета детализирующих периодов
		List<PriceModifierWrapper> modifierWrappers = createModifierWrappers(boundaries);

		if (modifierWrappers.isEmpty()) {
			// Если модификаторы не выжили, то будет применен простой вариант расчета по уже имеющемуся периоду
			// Также сюда мы попадем в том случае, если boundaries - "отрезок нулевой длины". Тогда просто не будет
			// никаких модификаторов, даже если они потенциально имеются
			InvoicePlanPeriodImpl period = createInvoicePeriod(boundaries, null);
			return Collections.singletonList(period);
		}

		List<InvoicePlanPeriodImpl> result = new ArrayList<>();

		// Получив некоторую коллекцию упорядоченных модификаторов необходимо корректно сформировать
		// периоды расчета. Модификаторы могут закрыть не все периоды, между модификаторами могуть быть "дырки"
		// Поэтому начинать формирование периодов мы будем с самой начальной даты текущего плана
		LocalDateTime start = boundaries.lowerEndpoint();
		for (PriceModifierWrapper modifier : modifierWrappers) {
			if (modifier.getLowerBound().isAfter(start)) {
				// Если модификатор начинается позже начала текущего плана (есть как минимум одна базовая единица в
				// этом диапазоне), то мы создадим период без модификатора от start, до конца предыдущей базовой
				// единицы периода модификатора
				result.add(createInvoicePeriod(Range.closed(start, modifier.getLowerBound().minus(1, MILLIS)), null));
			}
			// При этом сам период модификатора уже будет выровнен и мы создадим период InvoicePlan с учетом
			// модификатора
			result.add(createInvoicePeriod(modifier.toRange(), modifier.getModifier()));

			// Т.к. между модификаторами тоже могут быть промежутки, нам нужно не потерять этот период и посчитать его
			// тоже, для этого в качестве начальной даты для следующей итерации возьмем начало следующей базовой
			// единицы правой границы периода модификатора
			start = modifier.getUpperBound().plus(1, MILLIS);
		}

		if (start.isBefore(boundaries.upperEndpoint())) {
			// Мог быть единственный модификатор где-то посередине временного отрезка boundaries. Соответственно,
			// начиная с последнего start и до конца boundaries необходимо сформировать последний период InvoicePlan
			result.add(createInvoicePeriod(Range.closed(start, boundaries.upperEndpoint()), null));
		}

		return result;
	}

	/**
	 * 
	 * @param boundaries
	 * @param details
	 * @return
	 */
	InvoicePlanPeriodImpl createSummary(Range<LocalDateTime> boundaries, List<InvoicePlanPeriodImpl> details) {
		checkArgument(!details.isEmpty(), "Inbound periods can not be empty");

		PeriodType periodType = chargingPeriod.getType();
		Money baseUnitCost = chargingPeriod.baseUnitCost();

		// Как минимум один элемент должен был быть. Это гарантируется исходной проверкой аргумента
		ListIterator<InvoicePlanPeriodImpl> it = details.listIterator();
		InvoicePlanPeriodImpl period = it.next();

		// В случае, если скидки не было или скидка распространилась на весь период инвойса, то суммировать ничего
		// не нужно, так же как и проверять, что сумма сошлась. Поэтому просто скопируем уже расчитанные значения
		// из единственного детализирующего периода и задействуем их в дальнейших шагах по созданию плана
		Money cost = period.cost();
		Money totalCost = period.totalCost();
		Money deltaCost = period.deltaCost();
		long baseUnitCount = period.baseUnitCount();

		// Однако, если скидки были, то исходный период плана был разбит на несколько детализирующих (каждый для своего
		// модификатора суммы, без пересечений) и для того, чтобы посчитать итоговую сумму инвойса необходимо
		// просуммировать все эти детализирующие периоды
		// Ожидается, что результат этого суммирования в cost и baseUnitCount будет соответствовать расчетному значению
		// т.к. при разбиении всего периода тарификации плана на подпериоды не должно было получиться лишних
		// базовых единиц и расчитанная стандартным способом стоимость плана должна соответствовать стоимости,
		// полученной благодаря суммированию всех детализирующих периодов с учетом возможной погрешности округления
		// см. ниже в блоке проверки
		while (it.hasNext()) {
			period = it.next();

			cost = cost.add(period.cost());
			totalCost = totalCost.add(period.totalCost());
			deltaCost = deltaCost.add(period.deltaCost());
			baseUnitCount += period.baseUnitCount();
		}

		// Если мы что-то суммировали, т.е. детализирующих периодов было больше одного, то необходимо выполнить
		// проверку, что суммированное значение и расчитанное по стандартным правилам не отличаются в пределах
		// допустимых погрешностей и, что самое главное, не появилось вдруг из ниоткуда лишних базовых единиц (или
		// наоборот, не убавилось)
		if (details.size() > 1) {
			LocalDateTime billingStart = boundaries.lowerEndpoint();
			LocalDateTime billingEnd = boundaries.upperEndpoint();

			// Расчитаем количество единиц и стоимость плана стандартным способом для проверки
			long baseUnitCountCheck = periodType.getBaseUnit().between(billingStart, billingEnd.plus(1, MILLIS));
			Money costCheck = baseUnitCost.multiply(baseUnitCount);

			// Результат проверки никогда не должен быть отрицательным. В противном случае, поломался один из
			// предыдущих шагов расчета
			checkState(baseUnitCountCheck == baseUnitCount, INTERNAL_STATE_CORRUPTED);
			checkState(costCheck.compareRounded(cost) == 0, INTERNAL_STATE_CORRUPTED);
		}

		return new InvoicePlanPeriodImpl(boundaries, baseUnitCount, baseUnitCost, cost, periodModifier, totalCost,
				deltaCost);
	}

	/**
	 * 
	 * @param summary
	 * @param details
	 * @return
	 */
	InvoicePlanImpl createPlan(InvoicePlanPeriodImpl summary, List<InvoicePlanPeriodImpl> details) {
		return new InvoicePlanImpl(chargingPeriod, roundingPolicy,
				new InvoicePlannedPeriod(Range.closed(plannedStart, plannedEnd)), invoiceId, timeline, summary,
				details);
	}

	/**
	 * 
	 * @param invoicePlan
	 */
	InvoicePlanImpl validateResult(InvoicePlanImpl invoicePlan) {
		checkState(invoicePlan != null);
		return invoicePlan;
	}

	// ***********************************************************************************************
	// Utils
	// ***********************************************************************************************

	/**
	 * 
	 * @param boundaries
	 * @param priceModifier
	 * @return
	 */
	InvoicePlanPeriodImpl createInvoicePeriod(Range<LocalDateTime> boundaries, InvoicePlanPriceModifier priceModifier) {
		LocalDateTime billingStart = boundaries.lowerEndpoint();
		LocalDateTime billingEnd = boundaries.upperEndpoint();

		PeriodType periodType = chargingPeriod.getType();
		Money baseUnitCost = chargingPeriod.baseUnitCost();

		long baseUnitCount = periodType.getBaseUnit().between(billingStart, billingEnd.plus(1, MILLIS));
		Money cost = baseUnitCost.multiply(baseUnitCount);

		Money totalCost = priceModifier != null ? cost.multiply(priceModifier.getPriceFactor()) : cost;
		Money deltaCost = priceModifier != null ? totalCost.subtract(cost) : Money.ZERO;

		return new InvoicePlanPeriodImpl(boundaries, baseUnitCount, baseUnitCost, cost, priceModifier, totalCost,
				deltaCost);
	}

	/**
	 * 
	 * @param boundaries
	 * @return
	 */
	List<PriceModifierWrapper> createModifierWrappers(Range<LocalDateTime> boundaries) {
		if (priceModifiers.isEmpty() || isZeroLenghtPeriod(boundaries)) {
			return Collections.emptyList();
		}
		// @formatter:off
		
		// Мы не знаем, какие модификаторы напихали в билдер. Кроме того нам необходимо завраппить модификаторы для 
		// того, чтобы появилась возможность менять границы (в переделах алгоритма, естественно).
		// 1) смапим модификаторы на враппер, 
		// 2) отфильтруем те врапперы, которые нам нужны для дальнейших расчетов. Это:
		//    * модификаторы, периоды которых пересекаются с или содержатся в периоде InvoicePlan
		//    * "полезные" модификаторы, т.е. такие, фактор которых не равен 1 (т.к. не влияет на стоимость)
		// 3) упорядочиваем полученные модификаторы по возрастанию их периодов и величины фактора
		LinkedList<PriceModifierWrapper> wrappers = priceModifiers.stream()
			.map(PriceModifierWrapper::wrap)
			.filter(PriceModifierWrapper.intersectsWith(boundaries))
			.sorted(PriceModifierWrapper.ascendingOrder())
			.collect(toCollection(LinkedList::new));
		
		// 4) Периоды модификаторов могут "вылезать" за период InvoicePlan, т.е. иметь "хвосты". Их нужно обрезать
		// 5) Периоды модификаторов могут начинаться и заканчиваться не в началах/окончаниях базовых единиц, что 
		//    может непредсказуемо повлиять на расчеты, поэтому для дальнейшей работы необходимо выровнять периоды 
		//    модификаторов по границам базовых единиц
		cutTailsAndRound(wrappers, boundaries);
		
		// 6) Полученные в итоге врапперы могут иметь пересечения. На текущий момент пересечения модификаторов 
		//    стоимости запрещены, однако есть исключение: модификатор, фактор которого равен 0. Такой модификатор
		//    может "поглотить" пересекающиеся с ним модификаторы с любым фактором. Ситуация возможна, если с тестовым
		//    периодом пересекается скидка, что вполне себе валидная ситуация. При этом тестовый период играет две роли:
		//    * модификатор периода
		//    * модификатор стоимости
		//    Доверительный период не рассматривается, т.к. мы его отфильтровали еще в процессе изготовления врапперов
		//    (доверительный период имеет фактор == 1)
		normalizeWrappers(wrappers);

		// @formatter:on
		return wrappers;
	}

	/**
	 * 
	 * @param wrappers
	 * @param boundaries
	 */
	private void cutTailsAndRound(LinkedList<PriceModifierWrapper> wrappers, Range<LocalDateTime> boundaries) {
		// На любом из предыдущих шагов коллекция модификаторов может внезапно опустеть
		// поэтому не стоит делать заведомо ненужную работу
		if (wrappers.isEmpty()) {
			return;
		}

		PeriodType periodType = chargingPeriod.getType();
		LocalDateTime startOfInterest = chargingPeriod.accountingPeriod().startOfInterest();
		LocalDateTime boundariesLower = boundaries.lowerEndpoint();
		LocalDateTime boundariesUpper = boundaries.upperEndpoint();

		// Обход по итератору потому, что есть вероятность, что нам понадобится удалять невалидные модификаторы
		ListIterator<PriceModifierWrapper> it = wrappers.listIterator();
		while (it.hasNext()) {
			PriceModifierWrapper wrapper = it.next();

			LocalDateTime wrapperLower = wrapper.getLowerBound();
			LocalDateTime wrapperUpper = wrapper.getUpperBound();

			if (wrapperLower.isBefore(boundariesLower)) {
				// Если модификатор пришел к нам из прошлых периодов (т.е. "длинный модификатор"), то нам необходимо
				// отрезать его "хвост" из прошлых периодов, т.к. в текущем контексте мы должны быть строго ограничены
				// периодом будущего плана
				wrapper.setLowerBound(boundariesLower);
			} else {
				// Модификатор начался в периоде текущего плана. Поэтому нам необходимо проверить датовремя начала
				// периода модификатора для выравнивания по границам базовых единиц.
				// Для этого расчитываем границы базовой единицы и проверяем, совпадает ли начало базовой единицы
				// с началом периода модификтора. Если совпадает, то делать ничего больше не нужно
				Range<LocalDateTime> baseUnit = periodType.calculateBaseUnitBoundaries(startOfInterest, wrapperLower);
				if (wrapperLower.isAfter(baseUnit.lowerEndpoint())) {
					// Однако, если период модификатора начинается в середине базовой единицы, то как бы применяем к
					// ней политику округления DOWN, т.е. перескакиваем на начало следующей базовой единицы
					// Таким образом реализуется правило полного вхождения базовых единиц в период модификатора, т.к.
					// базовая единица должна полностью входить в этот период, а не пересекаться с ним. В будущем,
					// возможно, появится настройка округления периода модификатора (как сейчас - полное вхождение
					// базовой единицы в период модификатора (DOWN), или пересечение периода модификатора с базовой
					// единицей (UP))
					wrapper.setLowerBound(baseUnit.upperEndpoint().plus(1, MILLIS));
				}
			}

			if (wrapperUpper.isAfter(boundariesUpper)) {
				// Аналогично обработке начальной точки периода модификатора обрабатывается и конечная точка.
				// Необходимо отрезать возможный лишний "хвост"
				wrapper.setUpperBound(boundariesUpper);
			} else {
				// Действие модификатора заканчивается в текущем плане. Поэтому нам необходимо проверить датовремя
				// окончания периода модификатора для выравнивания по границам базовых единиц. Для этого расчитаем
				// границы базовой единицы и проверим, совпадает ли окончание базовой единицы с окончанием периода
				// модификатора. Если совпадает, то делать больше ничего не нужно
				Range<LocalDateTime> baseUnit = periodType.calculateBaseUnitBoundaries(startOfInterest, wrapperUpper);
				if (wrapperUpper.isBefore(baseUnit.upperEndpoint())) {
					// Однако, если период модификатора заканчивается в середине базовой единицы, то, аналогично
					// округлению начала периода модификатора как бы применим политику округления DOWN, т.е.
					// перескакиваем на окончание предыдущей базовой единицы
					wrapper.setUpperBound(baseUnit.lowerEndpoint().minus(1, MILLIS));
				}
			}

			// После всяких округлений, обрезаний хвостов и прочего могли получить невалидный период модификатора
			// если это произошло, то скорее всего либо период модификатора начинался и заканчивался в одной базовой
			// единице (не на границах, а в середине), либо период плана представлен "отрезком единичной длины"
			// (но в этом случае, вообще не должны были попасть в эти методы)
			if (wrapper.getLowerBound().compareTo(wrapper.getUpperBound()) >= 0) {
				// такой невалидный в текущем контексте модификатор мы просто выкинем и не будем его рассматривать
				it.remove();
			} else {
				// Если же после выравниваний и округлений получили нормальный период модификатора, то необходимо
				// убедиться лишний раз, что мы не напортачили и период модификатора не выходит за границы периода
				// InvoicePlan
				checkState(boundaries.contains(wrapper.getLowerBound()), INTERNAL_STATE_CORRUPTED);
				checkState(boundaries.contains(wrapper.getUpperBound()), INTERNAL_STATE_CORRUPTED);
			}
		}

		// После обрезания хвостов и выравнивания по границам базовой единицы может нарушиться порядок следования
		// периодов модификатора (т.к. фактически границы меняются). Необходимо повторно отсортировать периоды
		// по возрастанию с учетом фактора (от меньшего к большему)
		// В противном случае может нарушиться логика нормализации
		wrappers.sort(PriceModifierWrapper.ascendingOrder());
	}

	/**
	 * 
	 * @param wrappers
	 */
	private void normalizeWrappers(LinkedList<PriceModifierWrapper> wrappers) {
		// На любом из предыдущих шагов коллекция модификаторов может внезапно опустеть
		// поэтому не стоит делать заведомо ненужную работу
		if (wrappers.isEmpty()) {
			return;
		}

		// Здесь нам необходимо проверить периоды модификаторов на возможные пересечения (на текущий момент они
		// запрещены). Предполагается, что периоды модификаторов, переданные в этот метод прошли предварительные
		// преобразования, т.е. их границы четко выровнены по границам базовых единиц текущего периода списания
		// Обход по итератору, потому что нам нужны и элементы и индексы, при этом городить индексированный for
		// не хочется из-за лишних действий
		ListIterator<PriceModifierWrapper> it = wrappers.listIterator();
		while (it.hasNext()) {
			// Для каждого периода модификатора мы будем выполнять проверку на возможное пересечение со следующими
			// периодами модификаторов
			PriceModifierWrapper testedWrapper = it.next();
			if (testedWrapper.isNeedToRemove()) {
				// Однако же есть вероятность, что мы сейчас будем проверять уже мертвый период. В данном случае его
				// нужно исключить из проверки. Причины, по которым период модификатора может стать мертвым
				// см. в normalizeNextWrappers
				continue;
			}

			if (it.nextIndex() < wrappers.size()) {
				// Если текущий проверямый период не последний, то мы можем проверить последующие периоды на возможное
				// пересечение. Для этого породим еще один итератор, начинающийся со следующего элемента в упорядоченной
				// коллекции модификаторов (она должна была упорядочиться на одном из предыдущих шагов)
				normalizeNextWrappers(testedWrapper, wrappers.listIterator(it.nextIndex()));
			}
		}

		// В методе #normalizeNextWrappers невалидные периоды не удаляются, а помечаются на удаление
		// окончательно их выкинуть из текущего расчета необходимо отдельным проходом по коллекции модификаторов
		it = wrappers.listIterator();
		while (it.hasNext()) {
			if (it.next().isNeedToRemove()) {
				it.remove();
			}
		}
	}

	/**
	 * 
	 * @param cursor
	 * @param nextIt
	 */
	private void normalizeNextWrappers(PriceModifierWrapper cursor, ListIterator<PriceModifierWrapper> nextIt) {
		while (nextIt.hasNext()) {
			PriceModifierWrapper current = nextIt.next();
			if (current.isNeedToRemove()) {
				// Здесь также есть вероятность, наткнуться на мертвый период и его также необходимо исключить
				// из проверки (на самом деле больше перестраховка, чем реальная необходимость)
				continue;
			}

			if (cursor.intersects(current.getLowerBound(), current.getUpperBound())) {
				// Если cursor имеет пересечение с current, то cursor должен приводить к нулевой стоимости, т.е.
				// иметь фактор 0, что дает ему возможность "поглотить" либо полностью, либо частично, любой другой
				// модификатор, в том числе тоже нулевой. В противном случае получим пересечение двух не нулевых
				// модификаторов и что делать в этом случае пока что никак никем не регламентировано и мы это
				// официально не поддерживаем на текущий момент
				checkState(cursor.hasAbsoluteFactor(), "Intersections of the price modifier periods are forbidden");

				// Опять же, все должно быть отсортировано, поэтому current не может начаться раньше, чем cursor
				// в этом случае они бы просто поменялись местами и логика метода была бы соблюдена
				checkState(!current.getLowerBound().isBefore(cursor.getLowerBound()), INTERNAL_STATE_CORRUPTED);

				if (current.getUpperBound().isAfter(cursor.getUpperBound())) {
					// Если current только пересекается, т.е. не "поглощается" полностью, то нам необходимо определить
					// определить новое датовремя начала для current. Для этого возьмем правую границу периода cursor
					// и увеличим ее на одну миллисекунду, попав тем самым на самое начала новой базовой единицы
					LocalDateTime newLowerBound = cursor.getUpperBound().plus(1, MILLIS);
					if (newLowerBound.compareTo(current.getUpperBound()) >= 0) {
						// Однако, если модификатор current начинался в последней базовой единице, а cursor ее по
						// какой-то причине тоже захватил (тестовый период cursor + скидка current), то новая дата
						// может вылезти за границу периода InvoicePlan. В любом случае, это период будет невалидным.
						current.setNeedToRemove(true);
					} else {
						// Удалось вытолкнуть модификатор current за границы cursor, обновим датовремя cursor
						current.setLowerBound(newLowerBound);
					}
				} else {
					// Если current полностью содержится в "поглощающем" cursor, то его можно просто выкинуть и забыть
					current.setNeedToRemove(true);
				}
			} else {
				// Если текущий проверяемый модификатор cursor не пересекается со следующим модификатором current
				// то он, очевидно, не пересекается ни с каким другим модификатором, потому что модификаторы должны
				// быть строго упорядочены по возрастанию
				return;
			}
		}
	}

	private boolean isZeroLenghtPeriod(Range<LocalDateTime> boundaries) {
		return isZeroLenghtPeriod(boundaries.lowerEndpoint(), boundaries.upperEndpoint());
	}

	private boolean isZeroLenghtPeriod(LocalDateTime lowerBound, LocalDateTime upperBound) {
		return lowerBound.isEqual(upperBound);
	}

	// ***********************************************************************************************
	// Configuration Boilerplate
	// ***********************************************************************************************

	public InvoicePlanBuilder setChargingPeriod(ChargingPeriod chargingPeriod) {
		this.chargingPeriod = chargingPeriod;
		return this;
	}

	public InvoicePlanBuilder setRoundingPolicy(RoundingPolicy roundingPolicy) {
		this.roundingPolicy = roundingPolicy;
		return this;
	}

	public InvoicePlanBuilder setPlannedStart(Date plannedStartDate) {
		return setPlannedStart(toLocalDateTime(plannedStartDate));
	}

	public InvoicePlanBuilder setPlannedStart(LocalDateTime plannedStartDateTime) {
		this.plannedStart = plannedStartDateTime;
		return this;
	}

	public InvoicePlanBuilder setPlannedEnd(Date plannedEndDate) {
		return setPlannedEnd(toLocalDateTime(plannedEndDate));
	}

	public InvoicePlanBuilder setPlannedEnd(LocalDateTime plannedEndDateTime) {
		this.plannedEnd = plannedEndDateTime;
		return this;
	}

	public InvoicePlanBuilder setInvoiceId(Long invoiceId) {
		this.invoiceId = invoiceId;
		return this;
	}

	public InvoicePlanBuilder setTimeline(InvoicePlanTimeline timeline) {
		this.timeline = timeline;
		return this;
	}

	public InvoicePlanBuilder setPreviousPlan(InvoicePlan previousPlan) {
		this.previousPlan = previousPlan;
		return this;
	}

	public InvoicePlanBuilder setPeriodModifier(InvoicePlanPeriodModifier periodModifier) {
		if (this.periodModifier != null && this.periodModifier instanceof InvoicePlanPriceModifier) {
			InvoicePlanPriceModifier priceModifier = (InvoicePlanPriceModifier) this.periodModifier;
			this.priceModifiers.remove(priceModifier);
		}

		this.periodModifier = periodModifier;

		if (this.periodModifier != null && this.periodModifier instanceof InvoicePlanPriceModifier) {
			InvoicePlanPriceModifier priceModifier = (InvoicePlanPriceModifier) this.periodModifier;
			this.priceModifiers.add(priceModifier);
		}

		return this;
	}

	public InvoicePlanBuilder addPriceModifier(InvoicePlanPriceModifier priceModifier) {
		this.priceModifiers.add(priceModifier);
		return this;
	}

	public InvoicePlanBuilder addPriceModifier(Collection<InvoicePlanPriceModifier> priceModifiers) {
		this.priceModifiers.addAll(priceModifiers);
		return this;
	}

	public InvoicePlanBuilder removePriceModifier(InvoicePlanPriceModifier priceModifier) {
		this.priceModifiers.remove(priceModifier);
		return this;
	}

	public InvoicePlanBuilder initFromInvoice(LongTermInvoice invoice) {
		checkRequiredArgument(invoice, "invoice");

		setChargingPeriod(chargingOf(invoice));
		setRoundingPolicy(invoice.getRoundingPolicy());
		setPlannedStart(invoice.getStartDate());
		setPlannedEnd(invoice.getEndDate());
		setInvoiceId(invoice.getId());
		setTimeline(invoice.getTimeline());
		setPeriodModifier(invoice.getPrivilege());

		invoice.getDiscounts().forEach(this::addPriceModifier);

		return this;
	}

	// ***********************************************************************************************
	// Helpers
	// ***********************************************************************************************

	@Getter
	@Setter
	@AllArgsConstructor(access = AccessLevel.PRIVATE)
	static class PriceModifierWrapper {
		private InvoicePlanPriceModifier modifier;
		private LocalDateTime lowerBound;
		private LocalDateTime upperBound;
		private boolean needToRemove;

		public boolean hasUselessFactor() {
			return Objects.equals(modifier.getPriceFactor(), BigDecimal.ONE);
		}

		public boolean hasAbsoluteFactor() {
			return Objects.equals(modifier.getPriceFactor(), BigDecimal.ZERO);
		}

		public boolean intersects(LocalDateTime lowerBound, LocalDateTime upperBound) {
			return this.lowerBound.isBefore(upperBound) && this.upperBound.isAfter(lowerBound);
		}

		public Range<LocalDateTime> toRange() {
			return Range.closed(lowerBound, upperBound);
		}

		public BigDecimal getPriceFactor() {
			return modifier.getPriceFactor();
		}

		public static PriceModifierWrapper wrap(InvoicePlanPriceModifier modifier) {
			checkRequiredArgument(modifier, "modifier");
			checkRequiredArgument(modifier.getValidFrom(), "modifier.validFrom");
			checkRequiredArgument(modifier.getValidTo(), "modifier.validTo");

			LocalDateTime lowerBound = toLocalDateTime(modifier.getValidFrom());
			LocalDateTime upperBound = toLocalDateTime(modifier.getValidTo());

			return new PriceModifierWrapper(modifier, lowerBound, upperBound, false);
		}

		public static Comparator<PriceModifierWrapper> ascendingOrder() {
			return comparing(PriceModifierWrapper::getLowerBound).thenComparing(PriceModifierWrapper::getPriceFactor);
		}

		public static Predicate<PriceModifierWrapper> intersectsWith(Range<LocalDateTime> boundaries) {
			checkRequiredArgument(boundaries, "boundaries");
			final LocalDateTime lowerBound = boundaries.lowerEndpoint();
			final LocalDateTime upperBound = boundaries.upperEndpoint();

			return w -> w.intersects(lowerBound, upperBound) && !w.hasUselessFactor();
		}
	}
}
