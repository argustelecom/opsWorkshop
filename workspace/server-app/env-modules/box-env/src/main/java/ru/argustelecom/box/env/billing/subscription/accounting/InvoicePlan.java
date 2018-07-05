package ru.argustelecom.box.env.billing.subscription.accounting;

import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;
import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

import javax.persistence.EntityManager;

import ru.argustelecom.box.env.billing.invoice.model.LongTermInvoice;
import ru.argustelecom.box.env.billing.provision.model.RoundingPolicy;
import ru.argustelecom.box.env.stl.period.ChargingPeriod;
import ru.argustelecom.box.env.stl.period.Period;

/**
 * План списания
 * <ul>
 * <li>PAST или исторического, уже зафиксированного и неизменного
 * <li>PRESENT или настоящего, действующего на текущий момент
 * <li>FUTURE или возможного будущего, расчитанного на основании текущего состояния подписки и ее окружения
 * </ul>
 * 
 * Является прототипом для {@linkplain LongTermInvoice инвойса}, единственный цивилизованный способ инициализировать
 * инвойс, внести изменения в него, например, для пересчета и т.д.
 */
public interface InvoicePlan extends Iterable<InvoicePlanPeriod> {

	/**
	 * Период списания, в который действовал или будет действовать текущий план. Всегда не null
	 */
	ChargingPeriod chargingPeriod();

	/**
	 * Политика округления, с которой был создан текущий план. Всегда не null
	 */
	RoundingPolicy roundingPolicy();

	/**
	 * Период, описывающий плановые даты начала и окончания инвойса. По этому периоду в будущем будут определены границы
	 * тарификации. Всегда не null
	 */
	Period plannedPeriod();

	/**
	 * Идентификатор инвойса, по которому был восстановлен этот план. Заполняется только для планов, восстановленных по
	 * историческим данным (past и present), т.к. для future инвойсов еще нет
	 */
	Long invoiceId();

	/**
	 * Временная шкала текущего плана. Всегда не null, однако верить ей следует только в момент расчета и сразу после
	 * него, до тех пор, пока по текущему плану не создан инвойс. Когда инвойс создается по плану этот параметр не
	 * меняется до пересчета
	 */
	InvoicePlanTimeline timeline();

	/**
	 * Итоговое значение расчета для текущего плана. Всегда не null. С этим параметром будет ассоциирован модификатор
	 * периода (Привилегия), если она определена.
	 */
	InvoicePlanPeriod summary();

	/**
	 * Детализированное представления для текущего плана. Всегда не null. Всегда есть хотя бы одна запись, (если одна,
	 * то стопроцентно соответствующая summary, если несколько, то summary представляет собой просуммированное значение
	 * всех details). С каждым элементом details может быть ассоциирован модификатор стоимости (Скидка или Привилегия)
	 */
	List<InvoicePlanPeriod> details();

	/**
	 * true - если текущий план исторический и восстановлен по закрытому инвойсу
	 */
	default boolean isPast() {
		return timeline() == InvoicePlanTimeline.PAST;
	}

	/**
	 * true - если текущий план настоящий и восстановлен по действующему инвойсу
	 */
	default boolean isPresent() {
		return timeline() == InvoicePlanTimeline.PRESENT;
	}

	/**
	 * true - если текущий план будущий и является прототипом для создания инвойса в будущем
	 */
	default boolean isFuture() {
		return timeline() == InvoicePlanTimeline.FUTURE;
	}

	/**
	 * true - если текущий план восстановлен по инвойсу (все равно какому)
	 */
	default boolean isCreatedFromInvoice() {
		return invoiceId() != null;
	}

	/**
	 * true - если текущий план восстановлен по конкретному указанному инвойсу
	 */
	default boolean isCreatedFromInvoice(LongTermInvoice invoice) {
		return Objects.equals(invoice.getId(), invoiceId());
	}

	/**
	 * Возвращает ссылку на инвойс, по которому был восстановлен этот план. Имеет смысл только для исторических и
	 * текущих планов. В случае вызова для будущего плана вернет null
	 */
	default LongTermInvoice getInvoice(EntityManager em) {
		checkRequiredArgument(em, "EntityManager");
		return invoiceId() != null ? em.find(LongTermInvoice.class, invoiceId()) : null;
	}

	/**
	 * Создает компаратор для упорядочивания планов по времени, начиная с самого раннего и заканчивая самым поздним
	 * (прямой или натуральный порядок)
	 */
	static Comparator<InvoicePlan> ascendingOrder() {
		return (p1, p2) -> p1.summary().startDateTime().compareTo(p2.summary().startDateTime());
	}

	/**
	 * Создает компаратор для упорядочивания планов по времени, начиная с самого позднего и заканчивая самым ранним
	 * (обратный порядок)
	 */
	static Comparator<InvoicePlan> descendingOrder() {
		return ascendingOrder().reversed();
	}

	/**
	 * Создает предикат для проверки пересечения планов с указанным периодом
	 */
	static Predicate<InvoicePlan> intersectsWith(Date lowerBound, Date upperBound) {
		final LocalDateTime lowerBoundLocal = toLocalDateTime(lowerBound);
		final LocalDateTime upperBoundLocal = toLocalDateTime(upperBound);
		return intersectsWith(lowerBoundLocal, upperBoundLocal);
	}

	/**
	 * Создает предикат для проверки пересечения планов с указанным периодом
	 */
	static Predicate<InvoicePlan> intersectsWith(Period period) {
		return intersectsWith(period.startDateTime(), period.endDateTime());
	}

	/**
	 * Создает предикат для проверки пересечения планов с указанным периодом
	 */
	static Predicate<InvoicePlan> intersectsWith(LocalDateTime lowerBound, LocalDateTime upperBound) {
		return p -> {
			LocalDateTime startDate = p.summary().startDateTime();
			LocalDateTime endDate = p.summary().endDateTime();
			return startDate.isBefore(upperBound) && endDate.isAfter(lowerBound);
		};
	}
}
