package ru.argustelecom.box.env.billing.transaction.model;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.CascadeType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.StringUtils;

import lombok.Getter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.reason.model.InvoiceReason;
import ru.argustelecom.box.env.billing.reason.model.UserReason;
import ru.argustelecom.box.env.billing.transaction.nls.TransactionMessagesBundle;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.utils.EntityManagerUtils;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "transactions")
public class Transaction extends BusinessObject {

	private static final long serialVersionUID = -2742256120686762387L;

	@ManyToOne(fetch = FetchType.LAZY)
	private PersonalAccount personalAccount;

	@MapsId
	@JoinColumn(name = "id")
	@OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL, optional = false)
	private TransactionReason reason;

	@Temporal(TemporalType.TIMESTAMP)
	private Date transactionDate;

	/**
	 * Дата показывающая, к какому имено периоду относится транзакция. В данном случае информация и
	 * {@link #transactionDate} не показательна, так как фактически она может отличатся (быть позднее периода, за
	 * который в данной транзакции беруться деньги). Равняется
	 * {@linkplain ru.argustelecom.box.env.billing.invoice.model.AbstractInvoice#endDate дате окончания} инвойса.
	 */
	@Getter
	@Temporal(TemporalType.TIMESTAMP)
	private Date businessDate;

	@Embedded
	private Money amount;

	protected Transaction() {
	}

	public Transaction(PersonalAccount personalAccount, TransactionReason reason, Money amount, Date businessDate) {
		this.personalAccount = personalAccount;
		this.transactionDate = new Date();
		this.amount = amount;
		this.businessDate = businessDate != null ? businessDate : transactionDate;

		this.reason = reason;
		reason.setTransaction(this);
	}

	public String getDescription() {
		reason = EntityManagerUtils.initializeAndUnproxy(reason);

		TransactionMessagesBundle messages = LocaleUtils.getMessages(TransactionMessagesBundle.class);

		String transactionType = amount.isNonNegative() ? messages.replenishment() : messages.charge();
		if (reason instanceof UserReason)
			return String.format("%s. %s", transactionType, reason.getDescription());
		if (reason instanceof InvoiceReason)
			return String.format("%s %s", transactionType, reason.getDescription());
		return StringUtils.EMPTY;
	}

	public PersonalAccount getPersonalAccount() {
		return personalAccount;
	}

	public TransactionReason getReason() {
		return reason;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public Money getAmount() {
		return amount;
	}

	public static class TransactionQuery extends EntityQuery<Transaction> {

		private EntityQueryEntityFilter<Transaction, PersonalAccount> personalAccount = createEntityFilter(
				Transaction_.personalAccount);
		private EntityQueryEntityFilter<Transaction, TransactionReason> reason = createEntityFilter(
				Transaction_.reason);
		private EntityQueryDateFilter<Transaction> transactionDate = createDateFilter(Transaction_.transactionDate);

		public TransactionQuery() {
			super(Transaction.class);
		}

		public EntityQueryEntityFilter<Transaction, PersonalAccount> personalAccount() {
			return personalAccount;
		}

		public EntityQueryEntityFilter<Transaction, TransactionReason> reason() {
			return reason;
		}

		public EntityQueryDateFilter<Transaction> transactionDate() {
			return transactionDate;
		}
	}
}
