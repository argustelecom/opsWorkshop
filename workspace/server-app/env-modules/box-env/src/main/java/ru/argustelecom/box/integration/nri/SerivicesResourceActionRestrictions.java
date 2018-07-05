package ru.argustelecom.box.integration.nri;

/**
 * Интерфейс для передачи ограничений на действия над ресурсами услуги
 * Created by s.kolyada on 25.12.2017.
 */
public interface SerivicesResourceActionRestrictions {

	/**
	 * Можно ли бронировать ресурсы для услуги
	 * @return истина если можно, иначе ложь
	 */
	default boolean canBeBooked() {return false;}

	/**
	 * Можно ли снимать бронирования с ресурсов для услуги
	 * @return истина если можно, иначе ложь
	 */
	default boolean canBeUnbooked() {return false;}

	/**
	 * Можно ли создавать нагрузку на ресурсы для услуги
	 * @return истина если можно, иначе ложь
	 */
	default boolean canBeLoaded() {return false;}

	/**
	 * Можно ли снимать нагрузку с ресурсов услуги
	 * @return истина если можно, иначе ложь
	 */
	default boolean canBeUnloaded() {return false;}
}
