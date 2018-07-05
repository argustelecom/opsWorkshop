package ru.argustelecom.box.env.billing.transaction.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;

@AllArgsConstructor
public class TransactionCompletedEvent {

	@Getter
	private Transaction transaction;

}
