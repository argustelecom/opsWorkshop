package ru.argustelecom.box.env.billing.transaction;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.account.CurrentPersonalAccount;
import ru.argustelecom.box.env.billing.account.CurrentPersonalAccount.NewTransactionCreatedEvent;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.reason.UserReasonTypeRepository;
import ru.argustelecom.box.env.billing.reason.model.UserReasonType;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;
import ru.argustelecom.system.inf.utils.CDIHelper;

@PresentationModel
public class TransactionCreationDialogModel implements Serializable {

	private static final long serialVersionUID = 283797334081376143L;

	@Inject
	private TransactionRepository transactionRepo;

	@Inject
	private UserReasonTypeRepository userReasonTypeRepo;

	private PersonalAccount personalAccount;
	private Money amount;
	private UserReasonType reasonType;
	private String reasonNumber;

	private List<UserReasonType> reasonTypes;

	private Callback<Transaction> transactionCreationCallback;

	public void confirm() {
		Transaction transaction = transactionRepo.createUserTransaction(personalAccount, amount, reasonType,
				reasonNumber);
		transactionCreationCallback.execute(transaction);
		NewTransactionCreatedEvent event = new NewTransactionCreatedEvent(transaction);
		CDIHelper.fireEvent(event);
		reset();
	}

	public void cancel() {
		reset();
	}

	@SuppressWarnings("Duplicates")
	private void reset() {
		personalAccount = null;
		amount = null;
		reasonType = null;
		reasonNumber = null;
	}

	public PersonalAccount getPersonalAccount() {
		return personalAccount;
	}

	public void setPersonalAccount(PersonalAccount personalAccount) {
		this.personalAccount = personalAccount;
	}

	public Money getAmount() {
		return amount;
	}

	public void setAmount(Money amount) {
		this.amount = amount;
	}

	public UserReasonType getReasonType() {
		return reasonType;
	}

	public void setReasonType(UserReasonType reasonType) {
		this.reasonType = reasonType;
	}

	public String getReasonNumber() {
		return reasonNumber;
	}

	public void setReasonNumber(String reasonNumber) {
		this.reasonNumber = reasonNumber;
	}

	public List<UserReasonType> getReasonTypes() {
		if (reasonTypes == null) {
			reasonTypes = userReasonTypeRepo.getAllUserReasonTypes();
		}

		return reasonTypes;
	}

	public Callback<Transaction> getTransactionCreationCallback() {
		return transactionCreationCallback;
	}

	public void setTransactionCreationCallback(Callback<Transaction> transactionCreationCallback) {
		this.transactionCreationCallback = transactionCreationCallback;
	}
}
