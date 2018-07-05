package ru.argustelecom.box.env.billing.transaction.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import ru.argustelecom.box.inf.modelbase.BusinessObject;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "transaction_reason")
public abstract class TransactionReason extends BusinessObject {

	@OneToOne(fetch = FetchType.LAZY, mappedBy = "reason")
	private Transaction transaction;

	@Column(length = 128)
	protected String reasonNumber;

	protected TransactionReason() {
	}

	public abstract String getDescription();

	protected TransactionReason(Long id) {
		this.id = id;
	}

	public Transaction getTransaction() {
		return transaction;
	}

	protected void setTransaction(Transaction transaction) {
		this.transaction = transaction;
	}

	public abstract String getReasonType();

	public String getReasonNumber() {
		return reasonNumber;
	}

	private static final long serialVersionUID = 3172439990396379858L;
}
