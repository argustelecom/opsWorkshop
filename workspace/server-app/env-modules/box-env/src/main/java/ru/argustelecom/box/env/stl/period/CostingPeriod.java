package ru.argustelecom.box.env.stl.period;

import ru.argustelecom.box.env.stl.Money;

/**
 * Период, обладающий стоимостью
 */
public interface CostingPeriod {

	/**
	 * Базовая стоимость периода, равна количеству базовых единиц, умноженному на стоимость одной базовой единицы
	 */
	Money cost();

	/**
	 * Стоимость одной базовой единицы в этом периоде
	 */
	Money baseUnitCost();

	/**
	 * Количество базовых единиц этого периода
	 */
	long baseUnitCount();

}
