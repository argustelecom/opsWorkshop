package ru.argustelecom.box.env.mediation.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface MediationMessagesBundle {

	//Error messages

	@Message("Отсутствие обязательных данных в исходном CDR")
	String requiredDataInSDRError();

	@Message("Невозможно определить направление")
	String canNotDetermineDirectionError();

	@Message("Невозможно применить дополнительные правила конвертации")
	String impossibleApplyAdditionalConversionRulesError();

	@Message("Невозможно идентифицировать клиента (услугу) и его тариф")
	String impossibleToIdentifyCustomerError();

	@Message("Невозможно однозначно определить тариф для расчёта")
	String impossibleToIdentifyTariffError();

	@Message("Не удалось найти подходящее направление (Класс трафика) для тарификации")
	String directionForTariffEntryNotFoundError();

	@Message("Существует несколько подходящих направлений (классов трафика) для тарификации")
	String severalDirectionsError();

	@Message("Ошибка groovy")
	String groovyError();

	@Message("Неизвестная ошибка")
	String unknownError();

	// Report result

	@Message("Выполнено")
	String resultOk();

	@Message("Выполнено с ошибками")
	String resultWarn();

	@Message("Не выполнено")
	String resultError();

	@Message("Изменение состояния (%s - %s)")
	String changeState(String fromState, String toState);

	//Report body

	@Message("Результат: %s")
	String resultMsg(String result);

	@Message("Обработано всего: %s")
	String totalProcessed(String count);

	@Message("Обработано успешно: %s")
	String successProcessed(String count);

	@Message("Непригодные данные:")
	String unsuitableData();

	@Message("Стадия: %s")
	String unsuitableStage(String unsuitableStage);

	@Message("Количество: %s")
	String unsuitableCount(String count);

	//Report stage

	@Message("Конвертация")
	String stageOfConvertation();

	@Message("Анализ направления")
	String stageOfAnalysis();

	@Message("Идентификация")
	String stageOfIdentification();

	@Message("Тарификация")
	String stageOfCharging();

	//Release code

	@Message("Ответ")
	String answeredCode();

	@Message("Занято")
	String busyCode();

	@Message("Нет ответа")
	String noAnswer();

	@Message("Неудачный")
	String failCode();
}
