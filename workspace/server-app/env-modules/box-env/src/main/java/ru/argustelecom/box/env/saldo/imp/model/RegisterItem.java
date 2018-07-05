package ru.argustelecom.box.env.saldo.imp.model;

import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.INCORRECT_ACCOUNT_NUMBER;
import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.INCORRECT_PAYMENT_DOC_DATE;
import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.INCORRECT_PAYMENT_NUMBER;
import static ru.argustelecom.box.env.saldo.imp.model.DefaultItemError.INCORRECT_SUM;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.stl.Money;

@Getter
@Setter
public abstract class RegisterItem {

	public String rowData;

	public Long accountId;

	public String paymentDocId;

	public abstract String getAccountNumber();

	public abstract void setAccountNumber(String accountNumber);

	public abstract Money getSum();

	public abstract void setSum(Money sum);

	public abstract String getPaymentDocNumber();

	public abstract void setPaymentDocNumber(String paymentDocNumber);

	public abstract Date getPaymentDocDate();

	public abstract void setPaymentDocDate(Date paymentDocDate);

	private Set<ItemError> errors = new HashSet<>();

	public RegisterItem(String rowData) {
		this.rowData = rowData;
	}

	public Set<ItemError> checkRawData() {
		Set<ItemError> errors = new HashSet<>();

		addError(checkAccountNumber());
		addError(checkSum());
		addError(checkPaymentDocumentNumber());
		addError(checkPaymentDocDate());

		return errors;
	}

	public void addError(ItemError error) {
		if (error != null)
			errors.add(error);
	}

	protected ItemError checkAccountNumber() {
		if (getAccountNumber() == null || getAccountNumber().isEmpty())
			return INCORRECT_ACCOUNT_NUMBER;
		return null;
	}

	protected ItemError checkSum() {
		if (getSum() == null || getSum().compareTo(Money.ZERO) < 0)
			return INCORRECT_SUM;
		return null;
	}

	protected ItemError checkPaymentDocumentNumber() {
		if (getPaymentDocNumber() == null || getPaymentDocNumber().isEmpty())
			return INCORRECT_PAYMENT_NUMBER;
		return null;
	}

	protected ItemError checkPaymentDocDate() {
		if (getPaymentDocDate() == null)
			return INCORRECT_PAYMENT_DOC_DATE;
		return null;
	}

}