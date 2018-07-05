package ru.argustelecom.box.env.billing.bill.queue;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.bill.BillAppService;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;

@QueueHandlerBean
@Named(value = BillRecalcHandler.HANDLER_NAME)
public class BillRecalcHandler implements QueueHandler {

	private static final long serialVersionUID = -2281007983277095075L;

	public static final String HANDLER_NAME = "billRecalcHandler";

	@Inject
	private BillAppService billAs;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		BillRecalcContext context = event.getContext(BillRecalcContext.class);
		billAs.recalculateBill(context.getBillId(), context.getBillDate(), context.getEmployeeId());
		if (context.isNeedSend()) {
			billAs.send(Lists.newArrayList(context.getBillId()), null, context.getSenderName(), null,
					context.getSendDate(), true);
		}

		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		return QueueErrorResult.RETRY_LATER;
	}

}
