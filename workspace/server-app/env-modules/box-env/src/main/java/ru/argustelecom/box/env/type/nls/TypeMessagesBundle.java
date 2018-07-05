package ru.argustelecom.box.env.type.nls;

import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

import ru.argustelecom.system.inf.exception.BusinessException;

@MessageBundle(projectCode = "")
public interface TypeMessagesBundle {

	@Message(value = "Невозможно установить значение категории '%s' для свойства с объявленной категорией '%s'")
	String lookupEntryCategoryMismatchWithDefinition(String definedCategory, String realCategory);

	@Message(value = "Значение текстового свойства '%s' должно соответствовать шаблону '%s'")
	String textValueMismatchWithPattern(String propertyName, String pattern);

	@Message(value = "Некорректный синтаксис регулярного выражения '%s'")
	BusinessException textValuePatternCompileException(String pattern, @Cause Throwable cause);

	@Message(value = "Некорректный синтаксис формата даты-времени '%s'")
	BusinessException dateValuePatternCompileException(String pattern, @Cause Throwable cause);

	@Message(value = "Невозможно изменить значение заблокированного свойства '%s'")
	BusinessException unableToChangeValueInLockedPropertyException(String pattern);

	@Message(value = "Значение числового параметра должно быть в интервале от %s до %s")
	String numericValueMinMaxRangeIssue(String min, String max);

	@Message(value = "Значение числового параметра должно превышать %s")
	String numericValueMinRangeIssue(String min);

	@Message(value = "Значение числового параметра не должно превышать %s")
	String numericValueMaxRangeIssue(String min);

	@Message(value = "Невозможно конвертировать значения единиц измерения, принадлежащих разным группам. Передано '%s', ожидается '%s'")
	String measuredValueCategoryMismatch(String passed, String expected);

	@Message(value = "Невозможно удалить группу")
	String unableToRemoveTypePropertyGroupSummary();

	@Message(value = "Группа должна быть пуста")
	String unableToRemoveTypePropertyGroupDetail();

	@Message(value = "Список идентификаторов %s с повторяющимися значениями для свойства %s: %s")
	BusinessException duplicateTypeInstancePropertyValues(String instanceName, String propertyId, String duplicateIds);

	@Message(value = "Ошибка валидации")
	String isPropertyValueUniqueSummary();

	@Message(value = "Значение свойства '%s' должно быть уникально")
	String isPropertyValueUniqueDetail(String propertyName);

	// properties
	@Message("Дата")
	String dateProperty();

	@Message("Интервал дат")
	String dateIntervalProperty();

	@Message("Измеряемая величина")
	String measuredProperty();

	@Message("Интервал измеряемых величин")
	String measuredIntervalProperty();

	@Message("Логический")
	String logicalProperty();

	@Message("Числовой(Целочисленный)")
	String longProperty();

	@Message("Числовой(Дробный)")
	String doubleProperty();

	@Message("Перечисление")
	String lookupProperty();

	@Message("Перечисление(Массив)")
	String lookupArrayProperty();

	@Message("Текстовый")
	String textProperty();

	@Message("Текстовый(Массив)")
	String textArrayProperty();

	@Message("Да")
	String unique();

	@Message("Нет")
	String notUnique();

	@Message("Не поддерживает")
	String notSupportUnique();

}
