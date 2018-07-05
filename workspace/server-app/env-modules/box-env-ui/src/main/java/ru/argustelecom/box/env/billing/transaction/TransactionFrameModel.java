package ru.argustelecom.box.env.billing.transaction;

import static ru.argustelecom.box.env.billing.transaction.TransactionFilterState.TransactionFilter.PERSONAL_ACCOUNT;

import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import lombok.Getter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice;
import ru.argustelecom.box.env.billing.reason.nls.ReasonMessagesBundle;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.util.Callback;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class TransactionFrameModel implements Serializable {

	private static final int STATIC_CONTENT_HEIGHT = 180;

	@PersistenceContext
	private EntityManager em;

	@Inject
	@Getter
	private TransactionLazyDataModel lazyDM;

	@Inject
	private TransactionFilterState transactionFilterState;

	@Getter
	private PersonalAccount personalAccount;

	@Getter
	private int frameHeight;

	@Getter
	private boolean filtered;

	public void preRender(PersonalAccount personalAccount, Integer frameHeight) {
		if (!Objects.equals(transactionFilterState.getPersonalAccount(), personalAccount)) {
			this.personalAccount = personalAccount;
			transactionFilterState.setPersonalAccount(personalAccount);
		}
		this.frameHeight = frameHeight;
		this.filtered = false;
	}

	public Callback<Transaction> getTransactionCreationCallback() {
		return (tr -> lazyDM.reloadData());
	}

	public void applyFilter() {
		lazyDM.reloadData();
		this.filtered = true;
	}

	public void clearFilters() {
		if (filtered) {
			this.filtered = false;
			lazyDM.reloadData();
			transactionFilterState.clearParams(PERSONAL_ACCOUNT);
		}
	}

	/**
	 * Ищет инвойс по переданному названию основания
	 *
	 * @param reasonType
	 * 				- тип основания
	 * @param reasonNumber
	 * 				- название основания, если тип основания "Инвойс", то это всегда будет его Id
	 *
	 * @return инвойс, при условии, что переданный тип основания является инвойсом, в противном случае вернет null
	 */
	public AbstractInvoice findInvoiceByReasonNumber(String reasonType, String reasonNumber) {
		ReasonMessagesBundle messages = LocaleUtils.getMessages(ReasonMessagesBundle.class);

		if (!Objects.equals(reasonType, messages.invoice())) {
			return null;
		}

		try {
			return em.find(AbstractInvoice.class, Long.parseLong(reasonNumber));
		} catch (NumberFormatException ex) {
			return null;
		}
	}

	public int getScrollHeight() {
		return frameHeight - STATIC_CONTENT_HEIGHT;
	}

	private static final long serialVersionUID = -1701237206403575664L;
}