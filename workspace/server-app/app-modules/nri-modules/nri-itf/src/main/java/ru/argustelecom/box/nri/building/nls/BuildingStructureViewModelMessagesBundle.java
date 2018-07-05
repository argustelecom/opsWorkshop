package ru.argustelecom.box.nri.building.nls;


import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфейс локализации
 */
@MessageBundle(projectCode = "")
public interface BuildingStructureViewModelMessagesBundle {

	/**
	 * Возвращает локализованное предложение
	 *
	 * @return
	 */
	@Message("Адресной информации недостаточно для отображения структуры строения")
	String thereIsLackOfAddressInformationToShowBuildingStructure();

	/**
	 * Возвращает локализованное предложение
	 *
	 * @return
	 */
	@Message("Строение")
	String building();

	/**
	 * Возвращает локализованное предложение
	 *
	 * @return
	 */
	@Message("Не удалось удалить ")
	String couldNotToDelete();

	/**
	 * Возвращает локализованное предложение
	 *
	 * @return
	 */
	@Message("В данном элементе присутствуют точки монтирования")
	String thisElementContainsMountPoint();

	/**
	 * Возвращает локализованное предложение
	 *
	 * @return
	 */
	@Message("Имя не должно быть пустым")
	String nameCanNotBeEmpty();

	/**
	 * Возвращает локализованное предложение
	 *
	 * @return
	 */
	@Message("Изменение структуры")
	String structureChanging();

	/**
	 * Возвращает локализованное предложение
	 *
	 * @return
	 */
	@Message("Структура строения изменена")
	String buildingStructureWasChanged();

	/**
	 * Возвращает локализованное предложение
	 *
	 * @return
	 */
	@Message("Не удалось изменить структуру строения")
	String couldNotChangeBuildingStructure();
}
