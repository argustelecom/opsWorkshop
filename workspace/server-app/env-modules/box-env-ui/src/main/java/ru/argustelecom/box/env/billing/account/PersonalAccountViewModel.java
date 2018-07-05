package ru.argustelecom.box.env.billing.account;

import javax.annotation.PostConstruct;
import javax.enterprise.event.Observes;
import javax.enterprise.event.Reception;
import javax.enterprise.event.TransactionPhase;
import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.CurrentPersonalAccount.NewTransactionCreatedEvent;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectDto;
import ru.argustelecom.box.env.billing.privilege.PrivilegeSubjectDtoTranslator;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.page.ViewModel;

@PresentationModel
public class PersonalAccountViewModel extends ViewModel {

	private static final long serialVersionUID = -581583432954464398L;

	@Inject
	private PrivilegeSubjectDtoTranslator privilegeSubjectDtoTranslator;

	@Inject
	private PersonalAccountBalanceService pabs;

	@Inject
	private CurrentPersonalAccount personalAccount;

	private PersonalAccount account;

	private Money balance;
	private Money availableBalance;

	@PostConstruct
	@Override
	public void postConstruct() {
		super.postConstruct();
		account = personalAccount.getValue();
		unitOfWork.makePermaLong();
	}

	public PersonalAccount getAccount() {
		return account;
	}

	public Money getBalance() {
		if (balance == null)
			balance = pabs.getBalance(account);
		return balance;
	}

	public Money getAvailableBalance() {
		if (availableBalance == null)
			availableBalance = pabs.getAvailableBalance(account);
		return availableBalance;
	}

	public void onNewTransactionCreation(
			@Observes(during = TransactionPhase.BEFORE_COMPLETION, notifyObserver = Reception.IF_EXISTS) NewTransactionCreatedEvent event) {
		balance = null;
		availableBalance = null;
	}

	public PrivilegeSubjectDto getPrivilegeSubject() {
		return privilegeSubjectDtoTranslator.translate(account);
	}

	public Money getThreshold() {
		return account.getThreshold();
	}

	public void setThreshold(Money threshold) {
		account.setThreshold(threshold);
	}

}
