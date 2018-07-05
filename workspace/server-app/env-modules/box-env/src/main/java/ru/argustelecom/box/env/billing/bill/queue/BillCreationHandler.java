package ru.argustelecom.box.env.billing.bill.queue;

import static java.lang.String.format;
import static ru.argustelecom.box.env.billing.bill.model.BillPeriod.of;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.toLocalDateTime;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.billing.bill.BillCreationService;
import ru.argustelecom.box.env.billing.bill.BillData;
import ru.argustelecom.box.env.billing.bill.BillDataLoader;
import ru.argustelecom.box.env.billing.bill.model.BillPeriod;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;
import ru.argustelecom.system.inf.exception.SystemException;

@QueueHandlerBean
@Named(value = BillCreationHandler.HANDLER_NAME)
public class BillCreationHandler implements QueueHandler {

	private static final long serialVersionUID = -2068494316135316703L;

	public static final String HANDLER_NAME = "billCreationHandler";

	@Inject
	private BillDataLoader dataLoader;

	@Inject
	private BillCreationService billCreationService;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		BillCreationContext context = event.getContext(BillCreationContext.class);
		BillData billData = dataLoader.load(context.getId(), context.getNumber(), context.getBillGroup(),
				context.getBillTypeId(), context.getTemplateId(), context.getPaymentCondition(),
				context.getBillCreationDate(), context.getBillDate(), createPeriod(context));
		billCreationService.create(billData);
		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		return QueueErrorResult.RETRY_LATER;
	}

	private BillPeriod createPeriod(BillCreationContext context) {
		switch (context.getPeriodType()) {
		case CALENDARIAN:
			return of(context.getPeriodUnit(), toLocalDateTime(context.getPeriodStartDate()));
		case CUSTOM:
			return of(toLocalDateTime(context.getPeriodStartDate()), toLocalDateTime(context.getPeriodEndDate()));
		default:
			throw new SystemException(format("Unsupported bill period type: '%s'", context.getPeriodType()));
		}
	}

}
