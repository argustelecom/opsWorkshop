package ru.argustelecom.box.env.billing.invoice.model;

import static ru.argustelecom.box.inf.utils.Preconditions.checkRequiredArgument;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.billing.transaction.model.Transaction;
import ru.argustelecom.box.env.lifecycle.api.LifecycleObject;
import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuery;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryDateFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQuerySimpleFilter;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "invoice")
public abstract class AbstractInvoice extends BusinessObject implements LifecycleObject<InvoiceState> {

	private static final long serialVersionUID = 4830452429285827580L;

	@Getter
	@Temporal(TemporalType.TIMESTAMP)
	@Column(nullable = false, updatable = false)
	private Date creationDate;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY, optional = false)
	private PersonalAccount personalAccount;

	@Getter
	@OneToOne(fetch = FetchType.LAZY)
	private Transaction transaction;

	@Getter
	@Setter
	@Enumerated(EnumType.STRING)
	private InvoiceState state;

	@Getter
	@Setter(AccessLevel.PROTECTED)
	@Temporal(TemporalType.TIMESTAMP)
	private Date closingDate;

	// ***************************************************************************************************************

	protected AbstractInvoice() {
	}

	protected AbstractInvoice(Long id, PersonalAccount personalAccount) {
		super(id);
		this.creationDate = new Date();
		this.personalAccount = checkRequiredArgument(personalAccount, "personalAccount");
		this.state = InvoiceState.CREATED;
	}

	// ***************************************************************************************************************

	public void joinTransaction(Transaction transaction) {
		this.transaction = checkRequiredArgument(transaction, "transaction");
	}

	// ***************************************************************************************************************

	public abstract Money getTotalPrice();

	// ***************************************************************************************************************

	public static class InvoiceQuery<T extends AbstractInvoice> extends EntityQuery<T> {

		private EntityQuerySimpleFilter<T, InvoiceState> state;
		private EntityQueryEntityFilter<T, PersonalAccount> personalAccount;
		private EntityQueryDateFilter<T> closingDate;

		public InvoiceQuery(Class<T> entityClass) {
			super(entityClass);
			state = createFilter(AbstractInvoice_.state);
			personalAccount = createEntityFilter(AbstractInvoice_.personalAccount);
			closingDate = createDateFilter(AbstractInvoice_.closingDate);
		}

		public EntityQueryEntityFilter<T, PersonalAccount> personalAccount() {
			return personalAccount;
		}

		public EntityQueryDateFilter<T> closingDate() {
			return closingDate;
		}

		public EntityQuerySimpleFilter<T, InvoiceState> state() {
			return state;
		}
	}

}