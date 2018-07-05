package ru.argustelecom.box.env.numerationpattern.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface NumerationMessagesBundle {

	@Message("Невалидный символ в строке начиная с %s")
	String invalidSymbol(String position);

	@Message("Невалидная переменная в строке начиная с %s")
	String invalidVariable(String position);

	@Message("Невалидная последовательностьс %s")
	String invalidSequence(String name);

	@Message("Не закрыты угловые скобки возле %s")
	String notEnclosedAngleBracket(String position);

	@Message("Не указана последовательность")
	String sequenceIsNotSpecified();

	@Message("Заданное значение больше допустимого")
	String valueIsTooLarge();

	@Message("Превышение допустимой длины номера: %d(%d)")
	String numberIsTooLarge(Integer actual, Integer max);

	@Message("Последовательность с таким именем уже существует")
	String sequenceAlreadyExists();

	@Message("Название последовательности не должно содержать пробелы")
	String sequenceShouldNotContainWhitespaces();

	@Message("Последовательность с именем %s не существует")
	String sequenceNotExist(String name);

	@Message("Переменной с именем %s не существует")
	String variableNotExist(String name);

	@Message("Для переменной %s не может быть задан формат")
	String variableIsNotAvailable(String name);

	@Message("Для переменной %s, формат %s неприменим")
	String patternIsNotApplicable(String variable, String pattern);

	@Message("Для переменной %s должен быть задан формат")
	String patternIsNotSpecified(String variable);

	@Message("Неизвестный тип лексемы %s")
	String unsupportedLexemeType(String name);

	@Message("Невозможно присвоить номер объекту. Задайте правила нумерации")
	String numerationPatternIsNotSpecified();

	@Message("Неизвестная инструкция %s")
	String unsupportedInstruction(String name);

	// periods
	@Message("")
	String numerationPeriodNone();

	@Message("День")
	String numerationPeriodDay();

	@Message("Месяц")
	String numerationPeriodMonth();

	@Message("Квартал")
	String numerationPeriodQuarter();

	@Message("Год")
	String numerationPeriodYear();

	// lexeme types
	@Message("Литерал")
	String lexemeTypeLiteral();

	@Message("Последовательность")
	String lexemeTypeSequence();

	@Message("Переменная")
	String lexemeTypeVariable();

	// Numerable objects
	@Message("Договор")
	String contract();

	@Message("Дополнительное соглашение")
	String contractExtension();

	@Message("Счёт")
	String bill();

	@Message("Лицевой счёт")
	String personalAccount();

	@Message("Задача")
	String task();

	@Message("Заявка")
	String order();

}
