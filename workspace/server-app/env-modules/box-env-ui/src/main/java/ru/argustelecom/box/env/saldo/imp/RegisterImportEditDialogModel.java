package ru.argustelecom.box.env.saldo.imp;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.primefaces.context.RequestContext;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.account.PersonalAccountRepository;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.transaction.TransactionRepository;
import ru.argustelecom.box.env.overall.nls.OverallMessagesBundle;
import ru.argustelecom.box.env.saldo.nls.SaldoImportMessagesBundle;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "registerImportEditDM")
@PresentationModel
public class RegisterImportEditDialogModel implements Serializable {

	private static final long serialVersionUID = 6076287471191953702L;

	@PersistenceContext
	private EntityManager em;

	@Inject
	private TransactionRepository tr;

	@Inject
	private PersonalAccountRepository par;

	private RegisterImportViewModel.RegisterItemAdapter editableItemAdapter;

	private Callback<RegisterImportViewModel.RegisterItemAdapter> editCallback;

	private List<PersonalAccount> accounts;

	private String error;
	private PersonalAccount newAccount;
	private Money newSum;
	private String newPaymentNumber;
	private Date newPaymentDate;

	public void onDialogOpen() {
		RequestContext.getCurrentInstance().update("import_edit_data_form");
		RequestContext.getCurrentInstance().execute("PF('importEditDataDlgVar').show();");
	}

	public void save() {
		String paymentDocId = tr.generatePaymentDocId(newAccount.getId(), newPaymentNumber,
				newPaymentDate, newSum);

		if (!tr.findSameTransactions(Lists.newArrayList(paymentDocId)).isEmpty()) {
			OverallMessagesBundle overallMessages = LocaleUtils.getMessages(OverallMessagesBundle.class);
			SaldoImportMessagesBundle saldoImportMessages = LocaleUtils.getMessages(SaldoImportMessagesBundle.class);

			Notification.error(
					overallMessages.error(),
					saldoImportMessages.transactionAlreadyExists()
			);
			return;
		}

		editableItemAdapter.getValue().setPaymentDocId(paymentDocId);
		editableItemAdapter.getValue().setAccountNumber(newAccount.getNumber());
		editableItemAdapter.getValue().setAccountId(newAccount.getId());
		editableItemAdapter.getValue().setSum(newSum);
		editableItemAdapter.getValue().setPaymentDocNumber(newPaymentNumber);
		editableItemAdapter.getValue().setPaymentDocDate(newPaymentDate);

		editCallback.execute(editableItemAdapter);
		cancel();
	}

	@SuppressWarnings("Duplicates")
	public void cancel() {
		newAccount = null;
		newSum = null;
		newPaymentNumber = null;
		newPaymentDate = null;
		editableItemAdapter = null;
		editCallback = null;
	}

	public List<PersonalAccount> getAccounts() {
		if (accounts == null)
			accounts = par.findAll();
		return accounts;
	}

	public void setEditableItemAdapter(RegisterImportViewModel.RegisterItemAdapter editableItemAdapter) {
		this.editableItemAdapter = editableItemAdapter;
		error = editableItemAdapter.getError();
		if (editableItemAdapter.getValue().getAccountId() != null)
			newAccount = em.find(PersonalAccount.class, editableItemAdapter.getValue().getAccountId());
		newSum = editableItemAdapter.getValue().getSum();
		newPaymentNumber = editableItemAdapter.getValue().getPaymentDocNumber();
		newPaymentDate = editableItemAdapter.getValue().getPaymentDocDate();
	}

	// *****************************************************************************************************************
	// Simple getters and setters
	// *****************************************************************************************************************

	public RegisterImportViewModel.RegisterItemAdapter getEditableItemAdapter() {
		return editableItemAdapter;
	}

	public void setEditCallback(Callback<RegisterImportViewModel.RegisterItemAdapter> editCallback) {
		this.editCallback = editCallback;
	}

	public String getError() {
		return error;
	}

	public PersonalAccount getNewAccount() {
		return newAccount;
	}

	public void setNewAccount(PersonalAccount newAccount) {
		this.newAccount = newAccount;
	}

	public Money getNewSum() {
		return newSum;
	}

	public void setNewSum(Money newSum) {
		this.newSum = newSum;
	}

	public String getNewPaymentNumber() {
		return newPaymentNumber;
	}

	public void setNewPaymentNumber(String newPaymentNumber) {
		this.newPaymentNumber = newPaymentNumber;
	}

	public Date getNewPaymentDate() {
		return newPaymentDate;
	}

	public void setNewPaymentDate(Date newPaymentDate) {
		this.newPaymentDate = newPaymentDate;
	}

}