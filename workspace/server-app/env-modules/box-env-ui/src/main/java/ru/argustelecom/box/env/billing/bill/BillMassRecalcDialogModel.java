package ru.argustelecom.box.env.billing.bill;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;
import org.primefaces.model.StreamedContent;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.box.env.billing.bill.nls.BillMessagesBundle;
import ru.argustelecom.box.inf.nls.LocaleUtils;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named("billMassRecalcDm")
@PresentationModel
public class BillMassRecalcDialogModel extends BillReportGenDialog implements Serializable {

	private static final long serialVersionUID = -1437297950546438030L;

	@Inject
	private BillAppService billAs;

	@Inject
	private BillReportDtoTranslator billReportDtoTr;

	@Getter
	private BillParamsDto billParams = new BillParamsDto();

	@Getter
	private List<Bill> bills = new ArrayList<>();

	@Getter
	@Setter
	private boolean needSendBills = true;

	@Getter
	@Setter
	private String senderName;
	@Getter
	@Setter
	private Date sendDate;

	@Getter
	private int billsWithoutEmailNumber;
	@Getter
	private int billsWithoutTemplateNumber;

	public void openMassRecalcDlg(String formId, String dlgId, String widgetVar) {
		RequestContext.getCurrentInstance().reset(formId);
		RequestContext.getCurrentInstance().update(format("%s-%s", formId, dlgId));
		RequestContext.getCurrentInstance().execute(format("PF('%s').show()", widgetVar));
	}

	public void findBills() {
		bills = billAs.findBills(billParams.getBillType().getId(), billParams.getBillPeriod().startDate(),
				billParams.getBillPeriod().endDate());
		if (needSendBills) {
			initBillErrorReportDtoList();
		}
	}

	public void recalc() {
		billAs.scheduleRecalcAndSendBills(bills.stream().map(Bill::getId).collect(toList()), billParams.getBillDate(),
				needSendBills, senderName, sendDate);
	}

	public void onCancel() {
		clear();
	}

	public StreamedContent onDownload() throws IOException {
		BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
		return downloadReport(messages.unsuitableBillsToSend(), messages.billNumbers());
	}

	public void initBillErrorReportDtoList() {
		billErrorReportDtoList = new ArrayList<>();

		List<Bill> billsWithoutEmail = bills.stream().filter(bill -> bill.getCustomer().getMainEmail() == null)
				.collect(toList());
		billsWithoutEmailNumber = billsWithoutEmail.size();

		List<Bill> billsWithoutTemplate = bills.stream().filter(bill -> bill.getTemplate() == null).collect(toList());
		billsWithoutTemplateNumber = billsWithoutTemplate.size();

		BillMessagesBundle messages = LocaleUtils.getMessages(BillMessagesBundle.class);
		Map<Bill, String> billsWithErrors = billsWithoutEmail.stream()
				.collect(Collectors.toMap(bill -> bill, bill -> messages.billDoesNotHaveEmail()));
		billsWithoutTemplate.forEach(billWithoutTemplate -> billsWithErrors.merge(billWithoutTemplate,
				messages.billDoesNotHaveTemplate(), (oldValue, newValue) -> oldValue + ", " + newValue));

		billsWithErrors.forEach((bill, errors) -> {
			BillReportDto reportRdo = billReportDtoTr.translate(bill);
			reportRdo.setError(errors);
			billErrorReportDtoList.add(reportRdo);
		});
	}

	private void clear() {
		billParams = new BillParamsDto();
		bills = null;
		needSendBills = true;
		senderName = null;
		sendDate = null;
		billErrorReportDtoList = new ArrayList<>();
		billsWithoutEmailNumber = 0;
		billsWithoutTemplateNumber = 0;
	}

	public enum Step {
		FIND("find"), CONFIRM("confirm");

		@Getter
		private String id;

		Step(String id) {
			this.id = id;
		}

		public static Step getStep(String id) {
			return Arrays.stream(values()).filter(step -> step.getId().equals(id)).findFirst()
					.orElseThrow(() -> new IllegalArgumentException("Unknown step id"));
		}
	}
}
