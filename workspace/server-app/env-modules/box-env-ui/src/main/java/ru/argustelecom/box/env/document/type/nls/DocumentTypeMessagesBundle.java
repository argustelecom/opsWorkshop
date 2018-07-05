package ru.argustelecom.box.env.document.type.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface DocumentTypeMessagesBundle {

	@Message(value = "Создать тип договора")
	String createContract();

	@Message(value = "Создать тип дополнительного соглашения")
	String createContractExtension();

	@Message(value = "Создать тип счёта")
	String createBill();
}
