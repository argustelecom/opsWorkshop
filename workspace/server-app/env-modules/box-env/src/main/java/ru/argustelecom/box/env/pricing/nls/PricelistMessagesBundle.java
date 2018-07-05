package ru.argustelecom.box.env.pricing.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface PricelistMessagesBundle {

	@Message("Все")
	String all();

	@Message("Публичный")
	String common();

	@Message("Индивидуальный")
	String custom();

	// lc validators
	@Message("Нельзя удалить прайс-лист: прайс-лист содержит позиции (%s позиций)")
	String containsEntries(String entryCount);

	@Message("Нельзя активировать прайс-лист: прайс-лист не содержит позиций")
	String notContainsEntries();

	@Message("Нельзя активировать прайс-лист: окончание действия уже наступило")
	String pricelistExpired();

	@Message("После закрытия прайс-листа указанные в нем предложения будут недоступны для новых клиентов / новых подключений")
	String closingWarn();

	// lc routes
	@Message("Закрыть")
	String routeClose();

	@Message("Активировать")
	String routeActivate();

	@Message("Аннулировать")
	String routeCancel();

	//
	@Message("Дата начала действия договора больше даты окончания")
	String validFromAfterValidTo();

	@Message("Дата окончания действия договора меньше даты начала")
	String validToBeforeValidFrom();

	// lc states
	@Message("Оформление")
	String stateCreated();

	@Message("Действует")
	String stateInforce();

	@Message("Закрыт")
	String stateClosed();

	@Message("Аннулирован")
	String stateCancelled();

	@Message("Комментарий")
	String lifecycleVariableComment();

	@Message("Укажите причину изменения заявки")
	String lifecycleVariableCommentHint();

}