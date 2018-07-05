package ru.argustelecom.box.env.billing.bill;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.system.inf.page.PresentationModel;

import javax.inject.Inject;
import javax.inject.Named;
import java.io.Serializable;
import java.util.Date;

@Named("billRecalculationDm")
@PresentationModel
public class BillRecalculationDialogModel implements Serializable {

	@Inject
	private BillCardViewStateModel billCardViewStateModel;

	@Inject
	private BillAppService billAppService;

	@Getter
	@Setter
	private Date billDate;

	public void onRecalculate() {
		billAppService.recalculateBill(billCardViewStateModel.getReference(billCardViewStateModel.getBill()), billDate);
	}

	private static final long serialVersionUID = -3616640040006215283L;
}
