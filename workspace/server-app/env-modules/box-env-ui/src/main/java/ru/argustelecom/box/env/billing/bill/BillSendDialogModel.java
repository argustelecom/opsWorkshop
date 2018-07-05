package ru.argustelecom.box.env.billing.bill;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.env.message.mail.SendingMailException;
import ru.argustelecom.box.env.party.nls.CustomerMessagesBundle;
import ru.argustelecom.box.env.stl.EmailAddress;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.Notification;
import ru.argustelecom.system.inf.exception.BusinessException;
import ru.argustelecom.system.inf.page.PresentationModel;

@Getter
@Setter
@PresentationModel
@Named("billSendDm")
public class BillSendDialogModel extends BillReportGenDialog implements Serializable {

	@Inject
	private BillSendingAppService billSendingAppService;

	@Inject
	private BillSendingInfoRepository billSendingInfoRepository;

	@Inject
	private PrefTableRepository prefTableRepository;

	@Inject
	private BillAppService billAppService;

	@Inject
	private BillReportDtoTranslator billReportDtoTranslator;

	private Runnable runAfterSending;

	private Long batchSize;
	private String senderName;
	private Long interval;
	private Date sendDate;
	private List<Long> unsentBillIds;
	private int billsWithoutEmailNumber;
	private int billsWithoutTemplateNumber;

	private Bill bill;

	public int getBillsToSentNumber() {
		if (unsentBillIds == null) {
			unsentBillIds = billSendingInfoRepository.findAllNotSentBillIds();
			return unsentBillIds.size();
		}
		return unsentBillIds.size();
	}

	public String getSenderName() {
		if (senderName == null) {
			return senderName = prefTableRepository.getSenderName();
		}
		return senderName;
	}

	public void onSend() {
		checkBillHasTemplate();
		checkCustomerHasMailEmail();
		BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
		try {
			String email = bill.getCustomer().getMainEmail().getValue().value();
			billSendingAppService.send(bill.getId(), senderName, email);
			runAfterSending.run();
			Notification.info(messages.billSent(), messages.billSuccessfullySent(bill.getDocumentNumber(), email));
		} catch (SendingMailException e) {
			Notification.error(messages.couldNotSendBill(), e.getMessage());
		}
		clearParams();
	}

	public void onMassSend() {
		unsentBillIds
				.removeAll(getUnsentBillReportDtos().stream().map(BillReportDto::getId).collect(Collectors.toList()));
		if (!unsentBillIds.isEmpty()) {
			billAppService.send(unsentBillIds, batchSize, senderName, interval, sendDate);
		}
		clearParams();
	}

	public List<BillReportDto> getUnsentBillReportDtos() {
		if (billErrorReportDtoList == null) {
			initUnsendBillReportErrors();
			return billErrorReportDtoList;
		}
		return billErrorReportDtoList;
	}

	public StreamedContent onDownload() throws IOException {

		BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);

		return downloadReport(messages.unsuitableBillsToSend(), messages.billNumbers());
	}

	public void onCancel() {
		clearParams();
	}

	private void clearParams() {
		batchSize = null;
		senderName = null;
		interval = null;
		sendDate = null;
		unsentBillIds = null;
	}

	private void checkCustomerHasMailEmail() {
		EmailAddress mainEmail = billAppService.findMainEmail(bill.getId());
		if (mainEmail == null) {
			CustomerMessagesBundle messages = LocaleUtils.getMessages(CustomerMessagesBundle.class);
			throw new BusinessException(messages.customerDoesNotHaveMailEmail());
		}
	}

	private void checkBillHasTemplate() {
		if (bill.getTemplate() == null) {
			BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
			throw new BusinessException(messages.billDoesNotHaveTemplate());
		}
	}

	private void initUnsendBillReportErrors() {
		billErrorReportDtoList = new ArrayList<>();

		List<Bill> billsWithoutEmail = billSendingInfoRepository.findUnsentBillsWhereCustomerDoesNotHaveMainEmail();
		billsWithoutEmailNumber = billsWithoutEmail.size();

		List<Bill> billsWithoutTemplate = billSendingInfoRepository.findUnsentBillsWithoutTemplate();
		billsWithoutTemplateNumber = billsWithoutTemplate.size();

		BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
		Map<Bill, String> billsWithErrors = billsWithoutEmail.stream()
				.collect(Collectors.toMap(bill -> bill, bill -> messages.billDoesNotHaveEmail()));
		billsWithoutTemplate.forEach(billWithoutTemplate -> billsWithErrors.merge(billWithoutTemplate,
				messages.billDoesNotHaveTemplate(), (oldValue, newValue) -> oldValue + ", " + newValue));

		billsWithErrors.entrySet().forEach(billWithErrors -> {
			BillReportDto reportRdo = billReportDtoTranslator.translate(billWithErrors.getKey());
			reportRdo.setError(billWithErrors.getValue());
			billErrorReportDtoList.add(reportRdo);
		});
	}

	private static final long serialVersionUID = 7132500950434741814L;

}
