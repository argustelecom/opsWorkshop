package ru.argustelecom.box.env.contract.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface ContractMessagesBundle {

	// lc states
	@Message("Оформление")
	String stateRegistration();

	@Message("Действует")
	String stateInforce();

	@Message("Закрыт(o)")
	String stateTerminated();

	@Message("Аннулирован(o)")
	String stateCancelled();

	// lc routes
	@Message("Активировать")
	String routeActivate();

	@Message("Аннулировать")
	String routeCancel();

	@Message("Закрыть")
	String routeTerminate();

	// lc validators
	@Message("Необходимо указать позиции договора")
	String contractEntriesAreNotSpecified();

	@Message("Необходимо указать добавляемые и / или исключаемые позиции дополнительного соглашения")
	String contractExtensionEntriesAreNotSpecified();

	@Message("В договоре есть зависимые позиции")
	String contractHasEntries();

	@Message("В дополнительном соглашении есть зависимые позиции")
	String contractExtensionHasEntries();

	@Message("Необходимо указать условие оплаты")
	String paymentConditionIsNotSpecified();

	@Message("Не для всех позиций указаны подписки. Необходимо указать для следующих позиций: %s")
	String subscriptionsAreNotSpecified(String entriesWithoutSubscription);

	@Message("Срок действия договора / дополнительного соглашения истек %s")
	String contractExpired(String validTo);

	@Message("Закрытие договора повлечет за собой закрытие связанных дополнительных соглашений: %s")
	String contractClosureWillCauseContractExtensionClosure(String contractExtensions);

	@Message("Закрытие договора / дополнительного соглашения повлечет за собой закрытие связанных подписок на следующие продукты: %s")
	String contractClosureWillCauseSubscriptionClosure(String products);

	@Message("Нельзя активировать договор, так как договор %s на услугу не действует")
	String contractHasNotActiveAgencyContract(String contract);

	// payment
	@Message("Постоплата")
	String paymentConditionPostpayment();

	@Message("Предоплата")
	String paymentConditionPrepayment();

	//
	@Message("Невозможно добавить позицию")
	String cannotAddEntry();

	@Message("Нет ни одного позиции, которую можно было бы исключить")
	String noEntryToExclude();

	@Message("Невозможно сгенерировать печатную форму договора")
	String cannotGenerateReport();

	@Message("Нет ни одного активного прайс-листа, подходящего для данного клиента")
	String noActivePriceList();

	@Message("Комментарий")
	String lifecycleVariableComment();

	@Message("Укажите причину изменения заявки")
	String lifecycleVariableCommentHint();

	// contract category
	@Message("Двусторонний договор с клиентом")
	String bilateralCategory();

	@Message("Агентский договор с клиентом")
	String agencyCategory();

}
