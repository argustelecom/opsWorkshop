package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import lombok.Getter;
import ru.argustelecom.box.env.billing.bill.dto.BillHistoryItemDto;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "billHistoryFm")
@PresentationModel
public class BillHistoryFrameModel implements Serializable {

	@Inject
	private BillCardViewStateModel billCardViewStateModel;

	@Inject
	private BillAppService billAppService;

	@Getter
	private List<BillHistoryItemDto> billHistoryItemDtoList;

	public void preRender() {
		this.billHistoryItemDtoList = billCardViewStateModel.getBill() != null
				? billAppService.getBillHistoryItemDtosFromBill(
				billCardViewStateModel.getReference(billCardViewStateModel.getBill()))
				: billAppService.getBillHistoryItemDtosFromBillHistory(
				billCardViewStateModel.getReference(billCardViewStateModel.getBillHistoryItem()));
	}

	private static final long serialVersionUID = 7172913805396335595L;

}