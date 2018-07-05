package ru.argustelecom.box.integration.nri;

/**
 * Варианты представляения технической возможности
 * Created by s.kolyada on 31.08.2017.
 */
public enum TechPossibility {

	/**
	 * Недостаточно информации для определения технической возможности
	 */
	NOT_ENOUGH_DATA,

	/**
	 * Нет технической возможности
	 */
	NOT_AVAILABLE,

	/**
	 * Присутствуем в доме
	 */
	AVAILABLE_IN_BUILDING,

	/**
	 * Входит в зону покрытия ресурса
	 */
	COVERED_BY_SOME_RESOURCES,

	/**
	 * Техническая возможность есть
	 */
	FULL_AVAILABILITY
}
