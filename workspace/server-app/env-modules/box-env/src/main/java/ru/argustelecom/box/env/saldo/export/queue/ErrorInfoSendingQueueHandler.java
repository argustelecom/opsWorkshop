package ru.argustelecom.box.env.saldo.export.queue;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.billing.bill.PrefTableRepository;
import ru.argustelecom.box.env.message.MessageService;
import ru.argustelecom.box.env.message.mail.MailService;
import ru.argustelecom.box.env.message.model.MessageTemplate;
import ru.argustelecom.box.env.saldo.export.SaldoExportIssueRepository;
import ru.argustelecom.box.env.saldo.export.SaldoExportParamRepository;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportEventState;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportEventType;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportIssue;
import ru.argustelecom.box.env.saldo.export.model.SaldoExportParam;
import ru.argustelecom.box.env.saldo.nls.SaldoExportMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;

@QueueHandlerBean
@Named(value = ErrorInfoSendingQueueHandler.HANDLER_NAME)
public class ErrorInfoSendingQueueHandler implements QueueHandler {

	private static final long serialVersionUID = -2091443742153229891L;

	public static final String HANDLER_NAME = "errorInfoSendingWorkHandler";

	private static final String SALDO_EXPORT_ISSUE_PARAM = "saldoExportIssue";

	@Inject
	private SaldoExportIssueRepository saldoExportIssueRepository;

	@Inject
	private SaldoExportParamRepository saldoExportParamRepository;

	@Inject
	private MailService mailService;

	@Inject
	private MessageService messageService;

	@Inject
	private PrefTableRepository prefTableRepository;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		SaldoExportContext context = event.getContext(SaldoExportContext.class);
		SaldoExportIssue saldoExportIssue = context.getSaldoExportIssue();

		SaldoExportMessagesBundle messages = LocaleUtils.getMessages(SaldoExportMessagesBundle.class);

		SaldoExportParam param = saldoExportParamRepository.getParam();
		if (param.getEmailsForError().isEmpty()) {

			saldoExportIssueRepository.createEvent(saldoExportIssue, new Date(),
					SaldoExportEventType.SEND_ERROR_MESSAGE, SaldoExportEventState.UNSUCCESSFUL,
					messages.specifyMailList());
			return QueueHandlingResult.SUCCESS;
		}

		Map<String, Object> dataModel = new HashMap<>();
		dataModel.put(SALDO_EXPORT_ISSUE_PARAM, saldoExportIssue);
		String message = messageService.createMessage(MessageTemplate.SALDO_EXPORT_ERROR_TEMPLATE_ID, dataModel);
		String senderName = prefTableRepository.getSenderName();
		mailService.sendMail(param.getEmailsForError(), messages.saldoExportError(), senderName, message, null);

		saldoExportIssueRepository.createEvent(saldoExportIssue, new Date(), SaldoExportEventType.SEND_ERROR_MESSAGE,
				SaldoExportEventState.SUCCESSFULLY, messages.eventStateSuccessfully());

		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		SaldoExportContext context = event.getContext(SaldoExportContext.class);
		SaldoExportIssue saldoExportIssue = context.getSaldoExportIssue();

		SaldoExportMessagesBundle messages = LocaleUtils.getMessages(SaldoExportMessagesBundle.class);
		String description = messages.eventStateUnsuccessfully() + " " + error.getErrorText();
		saldoExportIssueRepository.createEvent(saldoExportIssue, new Date(), SaldoExportEventType.SEND_ERROR_MESSAGE,
				SaldoExportEventState.UNSUCCESSFUL, description);
		return QueueErrorResult.RETRY_LATER;
	}

}