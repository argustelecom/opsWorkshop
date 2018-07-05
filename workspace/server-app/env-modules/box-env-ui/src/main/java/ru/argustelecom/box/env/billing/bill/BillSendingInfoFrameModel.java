package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;
import java.util.Objects;

import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.model.Bill;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "billSendingInfoFm")
@PresentationModel
public class BillSendingInfoFrameModel implements Serializable {

	@Inject
	private BillSendingInfoDtoTranslator billSendingInfoDtoTr;

	private Bill bill;

	@Getter
	private BillSendingInfoDto billSendingInfo;

	public void preRender(Bill bill) {
		if (!Objects.equals(this.bill, bill)) {
			this.bill = bill;
			billSendingInfo = billSendingInfoDtoTr.translate(bill);
		}
	}

	public boolean billWasSent() {
		return billSendingInfo != null;
	}

	public Runnable runAfterSending() {
		return () -> {
			bill = null;
			RequestContext.getCurrentInstance().update("bill_sending_info_form");
		};
	}

	private static final long serialVersionUID = 6287503023450015720L;

}