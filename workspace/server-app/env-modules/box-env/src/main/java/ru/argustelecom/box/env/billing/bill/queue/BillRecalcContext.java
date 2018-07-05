package ru.argustelecom.box.env.billing.bill.queue;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.argustelecom.box.inf.queue.api.context.Context;
import ru.argustelecom.box.inf.queue.impl.model.QueueEventImpl;

@Getter
@AllArgsConstructor
public class BillRecalcContext extends Context {

	private static final long serialVersionUID = 1586965008200196404L;

	private Long billId;
	private Date billDate;
	private Long employeeId;
	private boolean needSend;
	private String senderName;
	private Date sendDate;

	public BillRecalcContext(QueueEventImpl event) {
		super(event);
	}

}
