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
import ru.argustelecom.box.env.billing.subscription.model.Subscription;
import ru.argustelecom.box.env.privilege.nls.PrivilegeMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.dataaccess.entityquery.EntityQueryEntityFilter;

@Entity
@Access(AccessType.FIELD)
public class SubscriptionPrivilege extends Privilege {

	private static final int PRIORITY = 1;

	@Getter
	@ManyToOne(fetch = FetchType.LAZY, targetEntity = Subscription.class)
	@JoinColumn(name = "subscription_id", updatable = false)
	private Subscription subscription;

	protected SubscriptionPrivilege() {
	}

	public SubscriptionPrivilege(Long id, PrivilegeType type, Date validFrom, Date validTo, Subscription subscription) {
		super(id, type, validFrom, validTo);

		checkNotNull(subscription, "Subscription is required for subscription privilege");
		this.subscription = subscription;
	}

	@Override
	public int getPriority() {
		return PRIORITY;
	}

	@Override
	public String getObjectName() {
		PrivilegeMessagesBundle messages = LocaleUtils.getMessages(PrivilegeMessagesBundle.class);
		return messages.privilegeForSubscription(super.getObjectName(), subscription.getObjectName());
	}

	public static class SubscriptionPrivilegeQuery extends PrivilegeQuery<SubscriptionPrivilege> {

		private EntityQueryEntityFilter<SubscriptionPrivilege, Subscription> subscription;

		public SubscriptionPrivilegeQuery() {
			super(SubscriptionPrivilege.class);
			subscription = createEntityFilter(SubscriptionPrivilege_.subscription);
		}

		public EntityQueryEntityFilter<SubscriptionPrivilege, Subscription> subscription() {
			return subscription;
		}

	}

	private static final long serialVersionUID = -5379420039124055248L;

}