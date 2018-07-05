package ru.argustelecom.box.env.billing.invoice.chargejob;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob.ChargeJobQuery;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJobState;
import ru.argustelecom.box.env.billing.invoice.model.JobDataType;

public class ChargeJobListFilterModel extends BaseEQConvertibleDtoFilterModel<ChargeJobQuery> {

	private static final long serialVersionUID = 2797597415794705213L;

	@Inject
	private ChargeJobListViewState chargeJobListVs;

	@Override
	public void buildPredicates(ChargeJobQuery query) {
		Map<String, Object> filterMap = chargeJobListVs.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
					case ChargeJobListViewState.ChargeJobFilter.MEDIATION_ID:
						addPredicate(query.mediationId().equal((String) filterEntry.getValue()));
						break;
					case ChargeJobListViewState.ChargeJobFilter.DATA_TYPE:
						addPredicate(query.dataType().equal((JobDataType) filterEntry.getValue()));
						break;
					case ChargeJobListViewState.ChargeJobFilter.STATE:
						addPredicate(query.state().equal((ChargeJobState) filterEntry.getValue()));
						break;
					case ChargeJobListViewState.ChargeJobFilter.VALID_FROM:
						addPredicate(query.creationDate().greaterOrEqualTo(((Date) filterEntry.getValue())));
						break;
					case ChargeJobListViewState.ChargeJobFilter.VALID_TO:
						addPredicate(query.creationDate().lessOrEqualTo((Date) filterEntry.getValue()));
						break;
					default:
						break;
				}
			}
		}
	}

	@Override
	public Supplier<ChargeJobQuery> entityQuerySupplier() {
		return ChargeJobQuery::new;
	}
}
