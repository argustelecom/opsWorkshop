package ru.argustelecom.box.env.saldo.export.queue;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.saldo.export.model.SaldoExportEventState.SUCCESSFULLY;
import static ru.argustelecom.box.env.saldo.export.model.SaldoExportEventState.UNSUCCESSFUL;
import static ru.argustelecom.box.env.saldo.export.model.SaldoExportEventType.EXPORT_DATA;
import static ru.argustelecom.box.inf.chrono.ChronoUtils.fromLocalDateTime;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.MEDIUM;

import java.time.LocalDateTime;
import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.saldo.export.SaldoExportIssueRepository;
import ru.argustelecom.box.env.saldo.export.SaldoExportParamRepository;
import ru.argustelecom.box.env.saldo.export.SaldoExportService;
import ru.argustelecom.box.env.saldo.export.model.CalculationType;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportIssue;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportParam;
import ru.argustelecom.box.env.saldo.nls.SaldoExportMessagesBundle;
import ru.argustelecom.box.env.stl.period.PeriodUnit;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;

@QueueHandlerBean
@Named(value = SaldoExportQueueHandler.HANDLER_NAME)
public class SaldoExportQueueHandler implements QueueHandler {

	private static final long serialVersionUID = -4270654186582309187L;

	public static final String HANDLER_NAME = "saldoExportWorkHandler";

	@Inject
	private SaldoExportIssueRepository saldoExportIssueRepository;

	@Inject
	private SaldoExportParamRepository saldoExportParamRepository;

	@Inject
	private SaldoExportService saldoExportService;

	@Inject
	private QueueProducer queueProducer;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		// TODO
		/*
		 * SaldoExportContext currentExportContext = event.getContext(SaldoExportContext.class); SaldoExportIssue
		 * currentExportIssue = currentExportContext.getSaldoExportIssue(); SaldoExportParam exportParam =
		 * saldoExportParamRepository.getParam();
		 * 
		 * scheduleNextIssue(exportParam.getNextExportDate(toLocalDateTime(currentExportIssue.getExportDate())));
		 * saldoExportService.export(currentExportIssue); createSuccessEvent(currentExportIssue, exportParam);
		 * 
		 * queueProducer.schedule(currentExportIssue, new Date(), SaldoSendingQueueHandler.HANDLER_NAME,
		 * currentExportContext);
		 */
		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		SaldoExportContext currentExportContext = event.getContext(SaldoExportContext.class);
		SaldoExportIssue currentExportIssue = currentExportContext.getSaldoExportIssue();

		createUnsuccessEvent(currentExportIssue, error);

		return QueueErrorResult.REJECT_EVENT;
	}

	private void scheduleNextIssue(LocalDateTime nextExportDate) {
		SaldoExportIssue nextExportIssue = saldoExportIssueRepository.createIssue(fromLocalDateTime(nextExportDate));
		SaldoExportContext nextExportContext = new SaldoExportContext(nextExportIssue);

		queueProducer.schedule(nextExportIssue, null, MEDIUM, nextExportIssue.getExportDate(),
				SaldoExportQueueHandler.HANDLER_NAME, nextExportContext);
	}

	private void createSuccessEvent(SaldoExportIssue exportIssue, SaldoExportParam exportParam) {
		PeriodUnit periodUnit = checkNotNull(exportParam.getPeriodUnit());
		CalculationType calculationType = checkNotNull(exportParam.getCalculationType());

		SaldoExportMessagesBundle messages = LocaleUtils.getMessages(SaldoExportMessagesBundle.class);
		saldoExportIssueRepository.createEvent(exportIssue, new Date(), EXPORT_DATA, SUCCESSFULLY,
				messages.saldoPeriod(periodUnit.toString(), calculationType.getName()));
	}

	private void createUnsuccessEvent(SaldoExportIssue exportIssue, QueueEventError error) {
		saldoExportIssueRepository.createEvent(exportIssue, new Date(), EXPORT_DATA, UNSUCCESSFUL,
				error.getErrorText());
	}

}
