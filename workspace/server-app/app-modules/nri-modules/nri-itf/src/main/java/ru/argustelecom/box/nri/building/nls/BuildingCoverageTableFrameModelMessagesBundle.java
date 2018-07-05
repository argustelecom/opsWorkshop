package ru.argustelecom.box.nri.building.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

/**
 * Интерфес локализации
 */
@MessageBundle(projectCode = "")
public interface BuildingCoverageTableFrameModelMessagesBundle {


	/**
	 * Возвращает Изменение
	 * @return
	 */
	@Message("Изменение")
	String changes();
	/**
	 * Возвращает Точка монтирования устройства изменена
	 * @return
	 */
	@Message("Точка монтирования устройства изменена")
	String mountPointIsChanged();
	/**
	 * Возвращает не смогли найти ноду c элементом строения по id
	 * @return
	 */
	@Message("не смогли найти ноду c элементом строения по id")
	String couldNotFindNodeWithElementOfBuildingById();

}
