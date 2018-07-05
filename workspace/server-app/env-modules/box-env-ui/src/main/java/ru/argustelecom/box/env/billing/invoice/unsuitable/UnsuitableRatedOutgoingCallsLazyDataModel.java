package ru.argustelecom.box.env.billing.invoice.unsuitable;

import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.amount;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.callDate;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.duration;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.id;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.ratedUnit;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.resourceNumber;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.service;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.supplier;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.tariff;
import static ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort.zone;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.inject.Named;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.billing.invoice.model.UnsuitableRatedOutgoingCallsView;
import ru.argustelecom.box.env.billing.invoice.model.UnsuitableRatedOutgoingCallsView.UnsuitableRatedOutgoingCallsViewQuery;
import ru.argustelecom.box.env.billing.invoice.model.UnsuitableRatedOutgoingCallsView_;
import ru.argustelecom.box.env.billing.invoice.unsuitable.UnsuitableRatedOutgoingCallsLazyDataModel.UnsuitableRatedOutgoingCallsSort;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;

@Named("unsuitableRatedOutgoingCallsLazyDm")
public class UnsuitableRatedOutgoingCallsLazyDataModel extends
		EQConvertibleDtoLazyDataModel<UnsuitableRatedOutgoingCallsView, UnsuitableRatedOutgoingCallsViewDto, UnsuitableRatedOutgoingCallsViewQuery, UnsuitableRatedOutgoingCallsSort> {

	@Inject
	private UnsuitableRatedOutgoingCallsListFilterModel unsuitableRatedOutgoingCallsListFm;

	@Inject
	private UnsuitableRatedOutgoingCallsViewDtoTranslator unsuitableRatedOutgoingCallsViewDtoTr;

	@PostConstruct
	private void postConstruct() {
		initPathMap();
	}

	@Override
	protected EQConvertibleDtoFilterModel<UnsuitableRatedOutgoingCallsViewQuery> getFilterModel() {
		return unsuitableRatedOutgoingCallsListFm;
	}

	@Override
	protected Class<UnsuitableRatedOutgoingCallsSort> getSortableEnum() {
		return UnsuitableRatedOutgoingCallsSort.class;
	}

	@Override
	protected DefaultDtoTranslator<UnsuitableRatedOutgoingCallsViewDto, UnsuitableRatedOutgoingCallsView> getDtoTranslator() {
		return unsuitableRatedOutgoingCallsViewDtoTr;
	}

	private void initPathMap() {
		addPath(id, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.id));
		addPath(callDate, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.callDate));
		addPath(duration, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.duration));
		addPath(ratedUnit, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.ratedUnit));
		addPath(amount, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.amount));
		addPath(tariff, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.tariff));
		addPath(resourceNumber, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.resourceNumber));
		addPath(service, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.service));
		addPath(supplier, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.supplier));
		addPath(zone, query -> query.root().get(UnsuitableRatedOutgoingCallsView_.zone));
	}

	public enum UnsuitableRatedOutgoingCallsSort {
		id, callDate, duration, ratedUnit, amount, tariff, resourceNumber, service, supplier, zone
	}

	private static final long serialVersionUID = -1930104881617913325L;
}
