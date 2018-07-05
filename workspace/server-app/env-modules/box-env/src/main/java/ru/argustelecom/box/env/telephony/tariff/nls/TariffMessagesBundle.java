package ru.argustelecom.box.env.telephony.tariff.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface TariffMessagesBundle {

	@Message("Все")
	String all();

	@Message("Публичный")
	String common();

	@Message("Индивидуальный")
	String custom();

	//lc routes
	@Message("Активировать")
	String routeActivate();

	@Message("Аннулировать")
	String routeCancel();

	@Message("Закрыть")
	String routeClose();

	//lc states
	@Message("Оформление")
	String stateFormalization();

	@Message("Действует")
	String stateActive();

	@Message("Архив")
	String stateArchive();

	@Message("Аннулирован")
	String stateCancelled();

	//validation
	@Message("Дата начала действия тарифа больше даты окончания")
	String validFromAfterValidTo();

	@Message("Дата окончания действия тарифа меньше даты начала")
	String validToBeforeValidFrom();

	@Message("Тарифицируемой единицей времени должна быть минута или секунда")
	String validRatedUnit();

	@Message("Пустой тарифный план нельзя активировать")
	String notContainsEntries();

	@Message("Нельзя активировать тарифный план: окончание действия уже наступило")
	String validToBeforeCurrentDate();

	@Message("Создание объекта")
	String telephonyZoneCreation();

	@Message("Редактирование объекта")
	String telephonyZoneEditing();

	@Message("Не удалось сохранить класс трафика, так как найдены пересечения по префиксам с направлениями <%s>")
	String updateCrossingError(String name);

	@Message("Не удалось добавить класс трафика, так как найдены пересечения по префиксам с направлениями <%s>")
	String createCrossingError(String name);

	@Message("Невозможно сохранить изменения, поскольку в выбранных тарифах найдены пересечения по префиксам")
	String intersectedPrefixesExistInTariffs();

	@Message("Обнаружены повторяющиеся префиксы!")
	String inputRepeatPrefixesError();

}
