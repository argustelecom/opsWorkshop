package ru.argustelecom.box.env.billing.subscription.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;

import ru.argustelecom.box.inf.modelbase.BusinessObject;

@MappedSuperclass
@Access(AccessType.FIELD)
public abstract class SubscriptionCause extends BusinessObject {

	@Transient
	private Subscription subscription;

	private String note;

	protected SubscriptionCause() {
	}

	public SubscriptionCause(Long id) {
		super(id);
	}

	public Subscription getSubscription() {
		return subscription;
	}

	public void setSubscription(Subscription subscription) {
		this.subscription = subscription;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	private static final long serialVersionUID = -146888610572702274L;

}