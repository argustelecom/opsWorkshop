package ru.argustelecom.box.env.billing.account;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.system.inf.page.CurrentEntity;
import ru.argustelecom.system.inf.page.PresentationState;

@PresentationState
public class CurrentPersonalAccount extends CurrentEntity<PersonalAccount> {

	private static final long serialVersionUID = -6910942959760216280L;

	@Getter
	@AllArgsConstructor
	public static class NewTransactionCreatedEvent {
		private Transaction newTransaction;
	}

}
