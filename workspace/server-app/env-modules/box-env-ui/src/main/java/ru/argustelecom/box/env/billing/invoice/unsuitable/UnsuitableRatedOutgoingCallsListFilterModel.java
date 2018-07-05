package ru.argustelecom.box.env.billing.invoice.unsuitable;

import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.CALL_DATE_FROM;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.CALL_DATE_TO;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.RESOURCE_NUMBER;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.SERVICE;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.SUPPLIER;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.TARIFF;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsListViewState.UnsuitableRatedOutgoingCallsFilter.ZONE;

import java.util.Date;

import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.billing.invoice.model.UnsuitableRatedOutgoingCallsView.UnsuitableRatedOutgoingCallsViewQuery;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.party.model.PartyRole;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.box.env.telephony.tariff.model.TelephonyZone;

@Named("unsuitableRatedOutgoingCallsListFm")
public class UnsuitableRatedOutgoingCallsListFilterModel
		extends BaseEQConvertibleDtoFilterModel<UnsuitableRatedOutgoingCallsViewQuery> {

	@Inject
	private UnsuitableRatedOutgoingCallsListViewState unsuitableRatedOutgoingCallsListVs;

	@SuppressWarnings("unchecked")
	@Override
	public void buildPredicates(UnsuitableRatedOutgoingCallsViewQuery query) {
		unsuitableRatedOutgoingCallsListVs.getFilterMap().forEach((key, value) -> {
			switch (key) {
			case CALL_DATE_FROM:
				addPredicate(query.callDate().greaterOrEqualTo((Date) value));
				break;
			case CALL_DATE_TO:
				addPredicate(query.callDate().lessOrEqualTo((Date) value));
				break;
			case RESOURCE_NUMBER:
				addPredicate(query.resourceNumber().equal((String) value));
				break;
			case TARIFF:
				addPredicate(query.tariff().equal(((BusinessObjectDto<AbstractTariff>) value).getIdentifiable()));
				break;
			case SERVICE:
				addPredicate(query.service().equal(((BusinessObjectDto<Service>) value).getIdentifiable()));
				break;
			case SUPPLIER:
				addPredicate(query.supplier().equal(((BusinessObjectDto<PartyRole>) value).getIdentifiable()));
				break;
			case ZONE:
				addPredicate(query.zone().equal(((BusinessObjectDto<TelephonyZone>) value).getIdentifiable()));
				break;
			}
		});
	}

	@Override
	public java.util.function.Supplier<UnsuitableRatedOutgoingCallsViewQuery> entityQuerySupplier() {
		return UnsuitableRatedOutgoingCallsViewQuery::new;
	}

	private static final long serialVersionUID = 2797597415794705213L;
}
