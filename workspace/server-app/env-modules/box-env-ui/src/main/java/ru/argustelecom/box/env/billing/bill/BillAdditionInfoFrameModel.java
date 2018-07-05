package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.dto.AdditionBillInfoDto;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "billAdditionInfoFm")
@PresentationModel
public class BillAdditionInfoFrameModel implements Serializable {

	@Inject
	private BillCardViewStateModel billCardViewStateModel;

	@Inject
	private BillAppService billAppService;

	@Getter
	private AdditionBillInfoDto additionBillInfoDto;

	public void preRender() {
		this.additionBillInfoDto = billCardViewStateModel.getBill() != null
				? billAppService.getAdditionBillInfoDtoFromBill(
						billCardViewStateModel.getReference(billCardViewStateModel.getBill()))
				: billAppService.getAdditionBillInfoDtoFromBillHistory(
						billCardViewStateModel.getReference(billCardViewStateModel.getBillHistoryItem()));
	}

	private static final long serialVersionUID = 5258698606857835113L;
}
