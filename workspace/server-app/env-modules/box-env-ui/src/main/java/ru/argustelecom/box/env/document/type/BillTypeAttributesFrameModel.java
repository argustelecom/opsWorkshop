package ru.argustelecom.box.env.document.type;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.billing.bill.BillTypeAppService;
import ru.argustelecom.box.env.billing.bill.model.BillPeriodType;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "billTypeAttributesFm")
@PresentationModel
public class BillTypeAttributesFrameModel extends DocumentTypeAttributesFrameModel<BillTypeDto> {

	private final static String PERIOD_FORMAT = "%s / %s";

	@Inject
	private BillTypeAppService billTypeAs;

	public void save() {
		BillTypeDto billType = getDocumentTypeDto();
		billTypeAs.save(billType.getId(), billType.getName(), billType.getDescription());
	}

	public String getPeriod() {

		BillPeriodType periodType = getDocumentTypeDto().getBillPeriodType();
		String period = getDocumentTypeDto().getBillPeriodType().getName();

		switch (periodType) {
		case CALENDARIAN:
			String unit = getDocumentTypeDto().getBillingPeriodUnit().toString();
			return String.format(PERIOD_FORMAT, period, unit);

		case CUSTOM:
			return period;

		default:
			return period;
		}
	}

	private static final long serialVersionUID = -2687588070043242428L;

}