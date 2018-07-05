package ru.argustelecom.box.env.billing.bill;

import java.io.Serializable;
import java.util.List;

import javax.inject.Inject;

import ru.argustelecom.box.env.billing.bill.model.BillAnalyticType;
import ru.argustelecom.box.env.billing.bill.model.SummaryBillAnalyticType;
import ru.argustelecom.box.inf.service.ApplicationService;

@ApplicationService
public class BillAnalyticTypeAppService implements Serializable {

	@Inject
	private BillAnalyticTypeRepository billAnalyticTypeRp;

	public List<SummaryBillAnalyticType> findAllSummaryBillAnalyticType() {
		return billAnalyticTypeRp.findAllSummaryBillAnalyticType();
	}

	public List<BillAnalyticType> findAllBillAnalyticTypes() {
		return billAnalyticTypeRp.findAllBillAnalyticTypes();
	}

	private static final long serialVersionUID = -6220715147794162908L;
}
