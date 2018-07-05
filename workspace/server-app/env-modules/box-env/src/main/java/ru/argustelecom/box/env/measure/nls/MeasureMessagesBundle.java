package ru.argustelecom.box.env.measure.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface MeasureMessagesBundle {

	@Message("Создана единица измерения %s")
	String measureCreated(String name);

	@Message("Поле 'Название' обязательное")
	String nameRequired();

	@Message("Обозначение единицы измерения должно быть уникальным")
	String nameShouldBeUnique();

	@Message("Единица измерения '%s' успешно удалена")
	String measureDeleted(String name);

	@Message("Единицу измерения невозможно удалить")
	String measureCannotBeDeleted();

	@Message("Не задана группа")
	String groupNotSpecified();

	@Message("Не задан коэффициент пересчёта")
	String coefficientNotSpecified();

	@Message("Действующие")
	String active();

	@Message("Не заполнены")
	String notSpecified();

}
