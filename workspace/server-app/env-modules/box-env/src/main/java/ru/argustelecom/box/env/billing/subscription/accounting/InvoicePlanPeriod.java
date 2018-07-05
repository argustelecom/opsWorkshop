package ru.argustelecom.box.env.billing.subscription.accounting;

import static java.util.Comparator.comparing;

import java.util.Comparator;
import java.util.Objects;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.env.stl.period.CostingPeriod;
import ru.argustelecom.box.env.stl.period.Period;

/**
 * Абстракция для представления информации о плане, например, дата начала и окончания действия тарификации, количество
 * базовых единиц, базовая стоимость одной такой единицы и т.д. Используется для представления summary и details плана
 * 
 * @see Period
 * @see CostingPeriod
 */
public interface InvoicePlanPeriod extends Period, CostingPeriod {

	/**
	 * Модификатор. Для summary - InvoicePlanPeriodModifier, для details - InvoicePlanPriceModifier
	 */
	InvoicePlanModifier modifier();

	/**
	 * Итоговая стоимость плана (инвойса) с учетом всех примененных модификаторов и т.д. Всегда не null, т.е. если не
	 * было ни одного модификатора стоимости, то будет равно {@linkplain CostingPeriod#cost() базовой стоимости}
	 */
	Money totalCost();

	/**
	 * Разница между итоговой стоимостью и {@linkplain CostingPeriod#cost() базовой стоимостью}. Для скидок -
	 * отрицательное значение, для штрафов (фантазия) - положительное значение. Всегда не null, т.е. если не было ни
	 * одного модификатора стоимости, то будет {@linkplain Money#ZERO}
	 */
	Money deltaCost();

	/**
	 * true - если текущий период имеет нулевую длину, т.е. дата начала равна дате окончания (особые случаи округления)
	 */
	default boolean isZeroLength() {
		return Objects.equals(startDateTime(), endDateTime());
	}

	/**
	 * Создает компаратор для упорядочивания периодов по времени, начиная с самого раннего и заканчивая самым поздним
	 * (прямой или натуральный порядок)
	 */
	static Comparator<InvoicePlanPeriod> ascendingOrder() {
		return comparing(InvoicePlanPeriod::startDateTime);
	}

	/**
	 * Создает компаратор для упорядочивания периодов по времени, начиная с самого позднего и заканчивая самым ранним
	 * (обратный порядок)
	 */
	static Comparator<InvoicePlanPeriod> descendingOrder() {
		return ascendingOrder().reversed();
	}
}
