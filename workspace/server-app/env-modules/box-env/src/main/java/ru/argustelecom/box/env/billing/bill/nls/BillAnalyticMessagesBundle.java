package ru.argustelecom.box.env.billing.bill.nls;

import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageBundle;

@MessageBundle(projectCode = "")
public interface BillAnalyticMessagesBundle {

	@Message(value = "Начисления")
	String charge();

	@Message(value = "Поступления")
	String income();

	@Message(value = "Остаток")
	String balance();

}
