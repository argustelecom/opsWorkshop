package ru.argustelecom.box.env.billing.bill.queue;

import javax.inject.Inject;
import javax.inject.Named;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.commons.lang.BooleanUtils;

import ru.argustelecom.box.env.billing.bill.BillSendingAppService;
import ru.argustelecom.box.env.billing.bill.model.BillSendingInfo;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;

@QueueHandlerBean
@Named(value = BillSendHandler.HANDLER_NAME)
public class BillSendHandler implements QueueHandler {

	private static final long serialVersionUID = 648726912175399521L;

	public static final String HANDLER_NAME = "billSendHandler";

	@PersistenceContext
	private EntityManager em;

	@Inject
	private BillSendingAppService billSendingAppService;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		BillSendContext context = event.getContext(BillSendContext.class);
		if (BooleanUtils.isTrue(context.getForcedSending())
				|| em.find(BillSendingInfo.class, context.getBillId()) == null) {
			billSendingAppService.send(context.getBillId(), context.getSenderName(), context.getEmail());
		}
		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		return QueueErrorResult.RETRY_LATER;
	}

}