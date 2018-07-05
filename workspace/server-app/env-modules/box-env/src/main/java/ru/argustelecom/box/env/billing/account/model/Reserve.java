package ru.argustelecom.box.env.billing.account.model;

import java.util.Currency;
import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import ru.argustelecom.box.env.stl.Money;
import ru.argustelecom.box.inf.modelbase.BusinessObject;
import ru.argustelecom.box.publang.billing.model.IReserve;
import ru.argustelecom.box.publang.base.wrapper.EntityWrapperDef;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system")
@EntityWrapperDef(name = IReserve.WRAPPER_NAME)
public class Reserve extends BusinessObject {

	private static final long serialVersionUID = 8092425875820500252L;

	@ManyToOne(optional = false)
	private PersonalAccount personalAccount;

	@Embedded
	private Money amount;

	@Temporal(TemporalType.TIMESTAMP)
	private Date reserveDate;

	protected Reserve() {
	}

	public Reserve(Long id, Money amount, PersonalAccount personalAccount) {
		super(id);
		this.amount = amount;
		this.personalAccount = personalAccount;
		this.reserveDate = new Date();
	}

	public void updateAmount(Money value) {
		amount = value;
		reserveDate = new Date();
	}

	public Money getAmount() {
		return amount;
	}

	public Currency getCurrency() {
		return personalAccount.getCurrency();
	}

	public PersonalAccount getPersonalAccount() {
		return personalAccount;
	}

	public Date getReserveDate() {
		return reserveDate;
	}
}
