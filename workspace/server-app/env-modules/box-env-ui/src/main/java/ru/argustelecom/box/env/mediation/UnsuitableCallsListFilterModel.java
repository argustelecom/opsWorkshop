package ru.argustelecom.box.env.mediation;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.commodity.model.Service;
import ru.argustelecom.box.env.dto.BusinessObjectDto;
import ru.argustelecom.box.env.mediation.model.UnsuitableCall.UnsuitableCallQuery;
import ru.argustelecom.box.env.mediation.model.MediationError;
import ru.argustelecom.box.env.telephony.tariff.model.AbstractTariff;
import ru.argustelecom.system.inf.modelbase.Identifiable;

import static com.google.common.base.Preconditions.checkArgument;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.DATE_FROM;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.DATE_TO;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.ERROR;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.INCOMING_CHANNEL;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.CALLED_NUMBER;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.INCOMING_TRUNK;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.FOREIGN_ID;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.OUTGOING_CHANNEL;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.CALLING_NUMBER;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.OUTGOING_TRUNK;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.SERVICE;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsListViewState.UnsuitableCallsFilter.TARIFF;

public class UnsuitableCallsListFilterModel extends BaseEQConvertibleDtoFilterModel<UnsuitableCallQuery> {

	private static final long serialVersionUID = -2918513826866676305L;

	@Inject
	private UnsuitableCallsListViewState viewState;

	@Override
	@SuppressWarnings({ "unchecked", "ConstantConditions" })
	public void buildPredicates(UnsuitableCallQuery unsuitableCallQuery) {
		Map<String, Object> filterMap = viewState.getFilterMap();
		for (Map.Entry<String, Object> filterEntry : filterMap.entrySet()) {
			if (filterEntry != null) {
				switch (filterEntry.getKey()) {
					case ERROR:
						addPredicate(unsuitableCallQuery.getErrorType().equal((MediationError) filterEntry.getValue()));
						break;
					case FOREIGN_ID:
						addPredicate(unsuitableCallQuery.getForeignId().equal((String) filterEntry.getValue()));
						break;
					case DATE_FROM:
						addPredicate(unsuitableCallQuery.getCallDate().greaterOrEqualTo((Date) filterEntry.getValue()));
						break;
					case DATE_TO:
						addPredicate(unsuitableCallQuery.getCallDate().lessOrEqualTo((Date) filterEntry.getValue()));
						break;
					case CALLED_NUMBER:
						addPredicate(unsuitableCallQuery.getCalledNumber().equal((String) filterEntry.getValue()));
						break;
					case INCOMING_CHANNEL:
						addPredicate(unsuitableCallQuery.getIncomingChannel().equal((String) filterEntry.getValue()));
						break;
					case INCOMING_TRUNK:
						addPredicate(unsuitableCallQuery.getIncomingTrunk().equal((String) filterEntry.getValue()));
						break;
					case CALLING_NUMBER:
						addPredicate(unsuitableCallQuery.getCallingNumber().equal((String) filterEntry.getValue()));
						break;
					case OUTGOING_CHANNEL:
						addPredicate(unsuitableCallQuery.getOutgoingChannel().equal((String) filterEntry.getValue()));
						break;
					case OUTGOING_TRUNK:
						addPredicate(unsuitableCallQuery.getOutgoingTrunk().equal((String) filterEntry.getValue()));
						break;
					case SERVICE:
						addPredicate(unsuitableCallQuery.getService().equal((Service) cast(filterEntry.getValue())));
						break;
					case TARIFF:
						addPredicate(unsuitableCallQuery.getTariff().equal((AbstractTariff) cast(filterEntry.getValue())));
						break;
					default:
						break;
				}
			}
		}
	}

	private Identifiable cast(Object obj) {
		checkArgument(obj instanceof BusinessObjectDto);
		return ((BusinessObjectDto) obj).getIdentifiable();
	}

	@Override
	public Supplier<UnsuitableCallQuery> entityQuerySupplier() {
		return UnsuitableCallQuery::new;
	}
}
