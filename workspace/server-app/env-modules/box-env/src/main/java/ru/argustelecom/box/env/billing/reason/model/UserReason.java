package ru.argustelecom.box.env.billing.reason.model;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

import ru.argustelecom.box.env.billing.reason.nls.ReasonMessagesBundle;
import ru.argustelecom.box.env.billing.transaction.model.TransactionReason;
import ru.argustelecom.box.inf.nls.LocaleUtils;

@Entity
@Access(AccessType.FIELD)
public class UserReason extends TransactionReason {

	private static final long serialVersionUID = -8654837492425995916L;

	@ManyToOne(fetch = FetchType.LAZY)
	private UserReasonType userReasonType;

	protected UserReason() {
	}

	public UserReason(Long id, UserReasonType userReasonType, String reasonNumber) {
		super(id);
		this.userReasonType = userReasonType;
		this.reasonNumber = reasonNumber;
	}

	@Override
	public String getDescription() {
		ReasonMessagesBundle messages = LocaleUtils.getMessages(ReasonMessagesBundle.class);
		return messages.reason(userReasonType.getName(), getReasonNumber());
	}

	@Override
	public String getReasonType() {
		return userReasonType.getName();
	}

	public UserReasonType getUserReasonType() {
		return userReasonType;
	}
}
