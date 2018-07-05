package ru.argustelecom.box.env.numerationpattern;

import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceLazyDataModel.NumerationSequenceSort;
import static ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.NumerationSequenceQuery;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import ru.argustelecom.box.env.EQConvertibleDtoFilterModel;
import ru.argustelecom.box.env.EQConvertibleDtoLazyDataModel;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence_;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class NumerationSequenceLazyDataModel extends
		EQConvertibleDtoLazyDataModel<NumerationSequence, NumerationSequenceDto, NumerationSequenceQuery, NumerationSequenceSort> {

	@Inject
	private NumerationSequenceListFilterModel numerationSequenceListFilterModel;

	@Inject
	private NumerationSequenceDtoTranslator numerationSequenceDtoTranslator;

	@PostConstruct
	private void postConstruct() {
		initPaths();
	}

	private void initPaths() {
		addPath(NumerationSequenceSort.name, query -> query.root().get(NumerationSequence_.name));
		addPath(NumerationSequenceSort.initialValue, query -> query.root().get(NumerationSequence_.initialValue));
		addPath(NumerationSequenceSort.increment, query -> query.root().get(NumerationSequence_.increment));
		addPath(NumerationSequenceSort.period, query -> query.root().get(NumerationSequence_.period));
		addPath(NumerationSequenceSort.capacity, query -> query.root().get(NumerationSequence_.capacity));
		addPath(NumerationSequenceSort.validTo, query -> query.root().get(NumerationSequence_.validTo));
		addPath(NumerationSequenceSort.currentValue, query -> query.root().get(NumerationSequence_.currentValue));
	}

	@Override
	protected Class<NumerationSequenceSort> getSortableEnum() {
		return NumerationSequenceSort.class;
	}

	@Override
	protected DefaultDtoTranslator<NumerationSequenceDto, NumerationSequence> getDtoTranslator() {
		return numerationSequenceDtoTranslator;
	}

	@Override
	protected EQConvertibleDtoFilterModel<NumerationSequenceQuery> getFilterModel() {
		return numerationSequenceListFilterModel;
	}

	public enum NumerationSequenceSort {
		name, initialValue, increment, period, capacity, validTo, currentValue
	}

	private static final long serialVersionUID = 5687165252208063648L;
}