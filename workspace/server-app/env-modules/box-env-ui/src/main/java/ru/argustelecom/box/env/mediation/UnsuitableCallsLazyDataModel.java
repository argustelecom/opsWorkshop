package ru.argustelecom.box.env.mediation;

import lombok.Setter;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.mediation.UnsuitableCallsListViewModel.UnsuitableCallsContext;
import ru.argustelecom.box.env.mediation.model.UnsuitableCall;
import ru.argustelecom.box.env.mediation.model.UnsuitableCall.UnsuitableCallQuery;
import ru.argustelecom.box.env.mediation.model.UnsuitableCall_;

import static java.util.Optional.ofNullable;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsLazyDataModel.UnsuitableCallsSort.*;
import static ru.argustelecom.box.env.mediation.UnsuitableCallsLazyDataModel.UnsuitableCallsSort;
public class UnsuitableCallsLazyDataModel
		extends EQConvertibleDtoLazyDataModel<UnsuitableCall, UnsuitableCallDto, UnsuitableCallQuery, UnsuitableCallsSort> {

	@Inject
	private UnsuitableCallDtoTranslator unsuitableCallDtoTr;

	@Inject
	private UnsuitableCallsListFilterModel filterModel;

	@Setter
	private UnsuitableCallsContext unsuitableCallsContext;

	@PostConstruct
	private void postConstruct() {
		initPathMap();
	}

	private void initPathMap() {
		addPath(id, query -> query.root().get(UnsuitableCall_.callId));
		addPath(errorType, query -> query.root().get(String.valueOf(UnsuitableCall_.errorType)));
		addPath(errorMsg, query -> query.root().get(String.valueOf(UnsuitableCall_.errorMsg)));
		addPath(callId, query -> query.root().get(UnsuitableCall_.callId));
		addPath(foreignId, query -> query.root().get(UnsuitableCall_.foreignId));
		addPath(exchange, query -> query.root().get(UnsuitableCall_.source));
		addPath(callDate, query -> query.root().get(UnsuitableCall_.callDate));
		addPath(duration, query -> query.root().get(UnsuitableCall_.duration));
		addPath(cdrUnit, query -> query.root().get(UnsuitableCall_.cdrUnit));
		addPath(rawCallDate, query -> query.root().get(UnsuitableCall_.rawCallDate));
		addPath(rawDuration, query -> query.root().get(UnsuitableCall_.rawDuration));
		addPath(releaseCode, query -> query.root().get(UnsuitableCall_.releaseCode));
		addPath(callingNumber, query -> query.root().get(UnsuitableCall_.callingNumber));
		addPath(outgoingChannel, query -> query.root().get(UnsuitableCall_.outgoingChannel));
		addPath(outgoingTrunk, query -> query.root().get(UnsuitableCall_.outgoingTrunk));
		addPath(service, query -> query.root().get(UnsuitableCall_.service));
		addPath(tariff, query -> query.root().get(UnsuitableCall_.tariff));
		addPath(calledNumber, query -> query.root().get(UnsuitableCall_.calledNumber));
		addPath(incomingChannel, query -> query.root().get(UnsuitableCall_.incomingChannel));
		addPath(incomingTrunk, query -> query.root().get(UnsuitableCall_.incomingTrunk));
		addPath(callDirection, query -> query.root().get(UnsuitableCall_.callDirection));
		addPath(incomingProvider, query -> query.root().get(UnsuitableCall_.incomingSupplier));
		addPath(outgoingProvider, query -> query.root().get(UnsuitableCall_.outgoingSupplier));
		addPath(identifiedBy, query -> query.root().get(UnsuitableCall_.identifiedBy));
	}

	@Override
	protected void prepare() {
		UnsuitableCallQuery entityQuery = filterModel.getEntityQuery(true);

		ofNullable(unsuitableCallsContext).ifPresent(context -> entityQuery.and(entityQuery.getProcessingStage()
				.equal(context.getStage())));

		filterModel.predicates(true);
		filterModel.buildPredicates(entityQuery);
		filterModel.applyPredicates(entityQuery);
	}

	@Override
	protected Class<UnsuitableCallsSort> getSortableEnum() {
		return UnsuitableCallsSort.class;
	}

	@Override
	protected DefaultDtoTranslator<UnsuitableCallDto, UnsuitableCall> getDtoTranslator() {
		return unsuitableCallDtoTr;
	}

	@Override
	protected EQConvertibleDtoFilterModel<UnsuitableCallQuery> getFilterModel() {
		return filterModel;
	}

	public enum UnsuitableCallsSort {
		id, errorType, errorMsg, callId, foreignId, exchange, callDate, duration, cdrUnit, releaseCode, callingNumber,
		outgoingChannel, outgoingTrunk, calledNumber, incomingChannel, incomingTrunk, service, tariff, callDirection,
		incomingProvider, outgoingProvider, identifiedBy, rawCallDate, rawDuration
	}

	private static final long serialVersionUID = 1479491966890562061L;
}
