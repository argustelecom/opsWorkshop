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
import ru.argustelecom.box.env.party.model.role.Customer;
import ru.argustelecom.box.env.privilege.nls.PrivilegeMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
public class CustomerPrivilege extends Privilege {

	private static final int PRIORITY = 3;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Customer.class)
	@JoinColumn(name = "customer_id", updatable = false)
	private Customer customer;

	public CustomerPrivilege() {
	}

	public CustomerPrivilege(Long id, Date validFrom, Date validTo, Customer customer) {
		super(id, PrivilegeType.TRUST_PERIOD, validFrom, validTo);

		checkNotNull(customer, "Customer is required for customer privilege");
		this.customer = customer;
	}

	@Override
	public int getPriority() {
		return PRIORITY;
	}

	@Override
	public String getObjectName() {
		PrivilegeMessagesBundle messages = LocaleUtils.getMessages(PrivilegeMessagesBundle.class);
		return messages.privilegeForCustomer(super.getObjectName(), customer.getObjectName());
	}

	public static class CustomerPrivilegeQuery extends PrivilegeQuery<CustomerPrivilege> {

		private EntityQueryEntityFilter<CustomerPrivilege, Customer> customer;

		public CustomerPrivilegeQuery() {
			super(CustomerPrivilege.class);
			customer = createEntityFilter(CustomerPrivilege_.customer);
		}

		public EntityQueryEntityFilter<CustomerPrivilege, Customer> customer() {
			return customer;
		}

	}

	private static final long serialVersionUID = -1965596027590347363L;

}