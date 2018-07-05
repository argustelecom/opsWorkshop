package ru.argustelecom.box.env.billing.bill;

import static com.google.common.base.Preconditions.checkNotNull;
import static ru.argustelecom.box.env.billing.bill.model.GroupingMethod.CONTRACT;
import static ru.argustelecom.box.env.billing.bill.model.GroupingMethod.PERSONAL_ACCOUNT;
import static ru.argustelecom.system.inf.utils.CDIHelper.lookupCDIBean;

import ru.argustelecom.box.env.billing.bill.model.GroupingMethod;

public final class BillGroupsSearchServiceUtils {
	private BillGroupsSearchServiceUtils() {
	}

	public static BillGroupsSearchService lookupSearchService(GroupingMethod groupingMethod) {
		checkNotNull(groupingMethod);

		return groupingMethod.equals(PERSONAL_ACCOUNT)
				? lookupCDIBean(BillGroupsSearchService.class, new BillGroupsSearcherLiteral(PERSONAL_ACCOUNT))
				: lookupCDIBean(BillGroupsByContractSearchService.class, new BillGroupsSearcherLiteral(CONTRACT));
	}
}
