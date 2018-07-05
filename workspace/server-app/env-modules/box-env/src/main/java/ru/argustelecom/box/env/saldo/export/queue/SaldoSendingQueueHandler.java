package ru.argustelecom.box.env.saldo.export.queue;

import static com.google.common.base.Preconditions.checkState;
import static ru.argustelecom.box.inf.queue.api.QueueProducer.Priority.MEDIUM;

import java.sql.Blob;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;

import com.google.common.collect.Lists;

import ru.argustelecom.box.env.billing.bill.PrefTableRepository;
import ru.argustelecom.box.env.message.MessageService;
import ru.argustelecom.box.env.message.mail.Attachment;
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
import ru.argustelecom.box.inf.queue.api.QueueProducer;
import ru.argustelecom.box.inf.queue.api.model.QueueEvent;
import ru.argustelecom.box.inf.queue.api.model.QueueEventError;
import ru.argustelecom.box.inf.queue.api.worker.QueueErrorResult;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandler;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlerBean;
import ru.argustelecom.box.inf.queue.api.worker.QueueHandlingResult;

@QueueHandlerBean
@Named(value = SaldoSendingQueueHandler.HANDLER_NAME)
public class SaldoSendingQueueHandler implements QueueHandler {

	private static final long serialVersionUID = 5206976584889434922L;

	public static final String HANDLER_NAME = "saldoSendingWorkHandler";

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
	private QueueProducer queueProducer;

	@Inject
	private PrefTableRepository prefTableRepository;

	@Override
	public QueueHandlingResult handleWork(QueueEvent event) throws Exception {
		SaldoExportContext context = event.getContext(SaldoExportContext.class);
		SaldoExportIssue saldoExportIssue = context.getSaldoExportIssue();
		Blob saldo = saldoExportIssue.getFile();
		checkState(saldo != null, "Не обнаружен файл для отправки");

		SaldoExportMessagesBundle messages = LocaleUtils.getMessages(SaldoExportMessagesBundle.class);

		SaldoExportParam param = saldoExportParamRepository.getParam();
		if (param.getEmailsForSuccess().isEmpty()) {
			saldoExportIssueRepository.createEvent(saldoExportIssue, new Date(), SaldoExportEventType.SEND_EXPORT_DATA,
					SaldoExportEventState.UNSUCCESSFUL, messages.noRecipientFound());
			return QueueHandlingResult.SUCCESS;
		}

		Attachment attachment = new Attachment(saldoExportIssue.getFileName(), saldo.getBinaryStream(), "text/plain");
		Map<String, Object> dataModel = new HashMap<String, Object>();
		dataModel.put(SALDO_EXPORT_ISSUE_PARAM, saldoExportIssue);
		String message = messageService.createMessage(MessageTemplate.SALDO_EXPORT_TEMPLATE_ID, dataModel);
		String senderName = prefTableRepository.getSenderName();
		mailService.sendMail(param.getEmailsForSuccess(), messages.saldoExport(), senderName, message,
				Lists.newArrayList(attachment));
		saldoExportIssueRepository.createEvent(saldoExportIssue, new Date(), SaldoExportEventType.SEND_EXPORT_DATA,
				SaldoExportEventState.SUCCESSFULLY, null);

		return QueueHandlingResult.SUCCESS;
	}

	@Override
	public QueueErrorResult handleError(QueueEvent event, QueueEventError error) throws Exception {
		SaldoExportContext context = event.getContext(SaldoExportContext.class);
		SaldoExportIssue saldoExportIssue = context.getSaldoExportIssue();

		saldoExportIssueRepository.createEvent(saldoExportIssue, new Date(), SaldoExportEventType.SEND_EXPORT_DATA,
				SaldoExportEventState.UNSUCCESSFUL, error.getErrorText());
		if (error.getAttemptsCount() < 5) {
			return QueueErrorResult.RETRY_LATER;
		} else {
			queueProducer.schedule(event.getQueue().getId(), null, MEDIUM, new Date(),
					ErrorInfoSendingQueueHandler.HANDLER_NAME, context);
			return QueueErrorResult.REJECT_EVENT;
		}
	}

}