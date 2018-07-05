package ru.argustelecom.box.env.billing.subscription.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Access(AccessType.FIELD)
@Table(schema = "system", name = "subscription_subject_cause")
public abstract class SubscriptionSubjectCause extends SubscriptionCause {

	protected SubscriptionSubjectCause() {
	}

	public SubscriptionSubjectCause(Long id) {
		super(id);
	}

	@Override
	@Access(AccessType.PROPERTY)
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "subjectCause", optional = false)
	public Subscription getSubscription() {
		return super.getSubscription();
	}

	private static final long serialVersionUID = -3459746198769439794L;

}