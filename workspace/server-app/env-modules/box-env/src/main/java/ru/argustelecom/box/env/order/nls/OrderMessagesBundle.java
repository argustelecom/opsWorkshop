package ru.argustelecom.box.env.order.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface OrderMessagesBundle {

	@Message("Не подключен")
	String notCovered();

	// lc states
	@Message("Оформление")
	String stateFormalization();

	@Message("В работе")
	String stateInProgress();

	@Message("Отложена")
	String statePostponed();

	@Message("Закрыта")
	String stateArchive();

	// lc routes
	@Message("В работу")
	String routeStartProgress();

	@Message("Закрыть")
	String routeClose();

	@Message("Отложить")
	String routePostpone();

	// lc validators
	@Message("Необходимо указать адрес подключения, в формализованном или в текстовом виде")
	String provisionAddressNotSpecified();

	@Message("В заявке не указан клиент")
	String customerNotSpecified();

	@Message("Необходимо указать контактную информацию клиента")
	String contactInfoNotSpecified();

	@Message("Договор %s в состоянии оформления")
	String contractInRegistration(String name);

	// priority
	@Message("Низкий")
	String priorityLow();

	@Message("Средний")
	String priorityMiddle();

	@Message("Высокий")
	String priorityHigh();

	@Message("Комментарий")
	String lifecycleVariableComment();

	@Message("Укажите причину изменения заявки")
	String lifecycleVariableCommentHint();

	@Message("Создание заявки")
	String creation();

	@Message("Создание заявки для")
	String creationFor();

	@Message("Создание заявки для организации")
	String creationForCompany();

	@Message("Создание заявки для персоны")
	String creationForPerson();

}
