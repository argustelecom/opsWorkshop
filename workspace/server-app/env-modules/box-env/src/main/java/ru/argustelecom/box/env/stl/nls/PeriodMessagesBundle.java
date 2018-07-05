package ru.argustelecom.box.env.stl.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface PeriodMessagesBundle {

    @Message("Период расчета не содержит периода списания для указанной даты: %s")
    String accountingPeriodNotContainDate(String date);

    @Message("Дата %s не принадлежит периоду: %s - %s")
    String dateIsNotInPeriod(String date, String lowBoundary, String highBoundary);

    // Period type
    @Message("Произвольный")
    String periodTypeCustom();

    @Message("Календарный")
    String periodTypeCalendarian();

    @Message("Отладочный")
    String periodTypeDebug();

    // Period unit
	@Message("Секунда")
	String periodUnitSecond();

    @Message("Минута")
    String periodUnitMinute();

    @Message("Час")
    String periodUnitHour();

    @Message("День")
    String periodUnitDay();

    @Message("Неделя")
    String periodUnitWeek();

    @Message("Месяц")
    String periodUnitMonth();

    @Message("Квартал")
    String periodUnitQuarter();

    @Message("Полугодие")
    String periodUnitSemester();

    @Message("Год")
    String periodUnitYear();
}
