package ru.argustelecom.box.env.privilege.model;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Date;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Getter;
import ru.argustelecom.box.env.billing.account.model.PersonalAccount;
import ru.argustelecom.box.env.privilege.nls.PrivilegeMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
public class PersonalAccountPrivilege extends Privilege {

	private static final int PRIORITY = 2;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = PersonalAccount.class)
	@JoinColumn(name = "personal_account_id", updatable = false)
	private PersonalAccount personalAccount;

	protected PersonalAccountPrivilege() {
	}

	public PersonalAccountPrivilege(Long id, Date validFrom, Date validTo, PersonalAccount personalAccount) {
		super(id, PrivilegeType.TRUST_PERIOD, validFrom, validTo);

		checkNotNull(personalAccount, "Personal account is required for personal account privilege");
		this.personalAccount = personalAccount;
	}

	@Override
	public int getPriority() {
		return PRIORITY;
	}

	@Override
	public String getObjectName() {
		PrivilegeMessagesBundle messages = LocaleUtils.getMessages(PrivilegeMessagesBundle.class);
		return messages.privilegeForPersonalAccount(super.getObjectName(), personalAccount.getObjectName());
	}

	public static class PersonalAccountPrivilegeQuery extends PrivilegeQuery<PersonalAccountPrivilege> {

		private EntityQueryEntityFilter<PersonalAccountPrivilege, PersonalAccount> personalAccount;

		public PersonalAccountPrivilegeQuery() {
			super(PersonalAccountPrivilege.class);
			personalAccount = createEntityFilter(PersonalAccountPrivilege_.personalAccount);
		}

		public EntityQueryEntityFilter<PersonalAccountPrivilege, PersonalAccount> personalAccount() {
			return personalAccount;
		}

	}

	private static final long serialVersionUID = 5372084510624969067L;

}