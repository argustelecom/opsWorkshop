package ru.argustelecom.box.env.billing.bill.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface BillMessagesBundle {

	@Message(value = "Не указан шаблон счёта")
	String billDoesNotHaveTemplate();

	@Message(value = "Не удалось отправить счёт")
	String couldNotSendBill();

	@Message(value = "Счёт отправлен")
	String billSent();

	@Message(value = "Счёт '%s' отправлен, на адрес: '%s'")
	String billSuccessfullySent(String billNumber, String email);

	@Message(value = "Созданы счета: ")
	String billsSuccessfulCreated();

	@Message("Договор")
	String billGroupingMethodContract();

	@Message("Лицевой счёт")
	String billGroupingMethodPersonalAccount();

	@Message("Счет с таким объектом группировки, периодом и схемой расчета уже существует")
	String billUniqueConditionsViolation();

	@Message("Счет с постоплатой может быть создан только за прошедний период")
	String billPostpaymentPeriodViolation();

	@Message("Некорректный период выставления счета")
	String billInvalidPeriodViolation();

	@Message("Дата не может быть меньше текущей")
	String dayCannotBeBeforeToday();

	@Message("Счета без e-mail")
	String billsWithoutEmail();

	@Message("Не указан e-mail для корреспонденции")
	String billDoesNotHaveEmail();
	
	@Message("Не подходящие для отправки счета")
	String unsuitableBillsToSend();

	@Message("#Номера счетов")
	String billNumbers();

	@Message("Счета с нарушением уникальности")
	String nonUniqueBills();

	@Message("#Список счетов с нарушением уникальности")
	String nonUniqueBillsToSend();

	@Message("Невозможно создать счёт, т.к. не удалось найти начисления по указанным параметрам")
	String noChargesFoundForCreationBill();

	// periods
	@Message("Первое полугодие")
	String semesterFirst();

	@Message("Второе полугодие")
	String semesterSecond();

	@Message("I квартал")
	String quarterFirst();

	@Message("II квартал")
	String quarterSecond();

	@Message("III квартал")
	String quarterThird();

	@Message("IV квартал")
	String quarterFourth();

	@Message("Январь")
	String monthJanuary();

	@Message("Февраль")
	String monthFebruary();

	@Message("Март")
	String monthMarch();

	@Message("Апрель")
	String monthApril();

	@Message("Май")
	String monthMay();

	@Message("Июнь")
	String monthJune();

	@Message("Июль")
	String monthJuly();

	@Message("Август")
	String monthAugust();

	@Message("Сентябрь")
	String monthSeptember();

	@Message("Октябрь")
	String monthOctober();

	@Message("Ноябрь")
	String monthNovember();

	@Message("Декабрь")
	String monthDecember();

	@Message("г.")
	String yearShortName();

}