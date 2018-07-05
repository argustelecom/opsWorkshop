package ru.argustelecom.box.env.billing.account;

import java.io.Serializable;
import java.util.Currency;

import javax.inject.Inject;

import ru.argustelecom.box.env.party.CurrentPartyRole;
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class PersonalAccountCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 6507623878397073660L;

	@Inject
	private PersonalAccountRepository accountRepository;

	@Inject
	private CurrentPartyRole currentPartyRole;

	private String number;

	public void confirm() {
		accountRepository.createPersonalAccount((Customer) currentPartyRole.getValue(), number,
				Currency.getInstance(ru.argustelecom.box.env.stl.Currency.getDefault().name()));

		cancel();
	}

	public void cancel() {
		number = null;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}
}
