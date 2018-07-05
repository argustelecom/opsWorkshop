package ru.argustelecom.box.env.billing.invoice.chargejob;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob.ChargeJobQuery;
import ru.argustelecom.box.env.billing.invoice.model.ChargeJob_;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class ChargeJobLazyDataModel extends EQConvertibleDtoLazyDataModel<ChargeJob, ChargeJobDto, ChargeJobQuery, ChargeJobLazyDataModel.ChargeJobSort> {

	@Inject
	private ChargeJobDtoTranslator chargeJobDtoTr;

	@Inject
	private ChargeJobListFilterModel chargeJobListFm;

	@PostConstruct
	private void postConstruct() {
		initPathMap();
	}

	@Override
	protected EQConvertibleDtoFilterModel<ChargeJobQuery> getFilterModel() {
		return chargeJobListFm;
	}

	@Override
	protected Class<ChargeJobSort> getSortableEnum() {
		return ChargeJobSort.class;
	}

	@Override
	protected DefaultDtoTranslator<ChargeJobDto, ChargeJob> getDtoTranslator() {
		return chargeJobDtoTr;
	}

	private void initPathMap() {
		addPath(ChargeJobSort.id, query -> query.root().get(ChargeJob_.mediationId));
		addPath(ChargeJobSort.dataType, query -> query.root().get(ChargeJob_.dataType));
		addPath(ChargeJobSort.state, query -> query.root().get(ChargeJob_.state));
		addPath(ChargeJobSort.creationDate, query -> query.root().get(ChargeJob_.creationDate));

	}

	public enum ChargeJobSort {
		id, dataType, state, creationDate
	}

	private static final long serialVersionUID = -6231464236378172168L;

}