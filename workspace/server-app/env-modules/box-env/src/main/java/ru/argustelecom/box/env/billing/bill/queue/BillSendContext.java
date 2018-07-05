package ru.argustelecom.box.env.billing.bill.queue;

import lombok.Getter;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

@Getter
public class BillSendContext extends Context {

	private Long billId;
	private String senderName;
	private String email;
	private Boolean forcedSending;

	public BillSendContext(QueueEventImpl event) {
		super(event);
	}

	public BillSendContext(Long billId, String senderName, String email, Boolean forcedSending) {
		this.billId = billId;
		this.senderName = senderName;
		this.email = email;
		this.forcedSending = forcedSending;
	}

	private static final long serialVersionUID = -9026389262654353941L;

}