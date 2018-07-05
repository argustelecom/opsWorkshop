package ru.argustelecom.box.env.billing.subscription.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;

@Entity
@Access(AccessType.FIELD)
public class OrderSubjectCause extends SubscriptionSubjectCause {

	protected OrderSubjectCause() {
	}

	public OrderSubjectCause(Long id) {
		super(id);
	}

	@Override
	public String getObjectName() {
		return getNote();
	}

	private static final long serialVersionUID = -3330581386272171140L;

}