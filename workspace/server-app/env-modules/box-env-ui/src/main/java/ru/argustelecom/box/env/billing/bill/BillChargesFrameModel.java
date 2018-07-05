package ru.argustelecom.box.env.billing.bill;

import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.dto.ChargesDto;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;

@Named(value = "billChargesFm")
@PresentationModel
public class BillChargesFrameModel implements Serializable {

	@Inject
	private BillCardViewStateModel billCardViewStateModel;

	@Inject
	private BillAppService billAppService;

	@Getter
	private ChargesDto chargesDto;

	public void preRender() {
		this.chargesDto =  billCardViewStateModel.getBill() != null
				? billAppService.getChargesDtoFromBill(
				billCardViewStateModel.getReference(billCardViewStateModel.getBill()))
				: billAppService.getChargesDtoFromBillHistory(
				billCardViewStateModel.getReference(billCardViewStateModel.getBillHistoryItem()));
	}

	private static final long serialVersionUID = 6875923257760384697L;
}
