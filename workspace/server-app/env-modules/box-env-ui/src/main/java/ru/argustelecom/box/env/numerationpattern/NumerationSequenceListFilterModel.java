package ru.argustelecom.box.env.numerationpattern;

import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.CURRENT_VALUE;
import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.NAME;
import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.PERIOD;
import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.VALID_FROM;
import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.VALID_TO;
import static ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.NumerationSequenceQuery;
import static ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.PeriodType;

import java.util.Date;
import java.util.Map;
import java.util.function.Supplier;

import javax.inject.Inject;

import ru.argustelecom.box.env.BaseEQConvertibleDtoFilterModel;

public class NumerationSequenceListFilterModel extends BaseEQConvertibleDtoFilterModel<NumerationSequenceQuery> {

	@Inject
	private NumerationSequenceFrameState numerationSequenceFrameState;

	@Override
	public void buildPredicates(NumerationSequenceQuery query) {
		Map<String, Object> filterMap = numerationSequenceFrameState.getFilterMap();
		for (Map.Entry<String, Object> entry : filterMap.entrySet()) {
			switch (entry.getKey()) {
			case NAME:
				addPredicate(query.name().likeIgnoreCase((String) entry.getValue()));
				break;
			case PERIOD:
				addPredicate(query.period().equal((PeriodType) entry.getValue()));
				break;
			case VALID_FROM:
				addPredicate(query.validTo().greaterOrEqualTo((Date) entry.getValue()));
				break;
			case VALID_TO:
				addPredicate(query.validTo().lessOrEqualTo((Date) entry.getValue()));
				break;
			case CURRENT_VALUE:
				addPredicate(query.currentValue().equal((Long) entry.getValue()));
				break;
			default:
				break;
			}
		}
	}

	@Override
	public Supplier<NumerationSequenceQuery> entityQuerySupplier() {
		return NumerationSequenceQuery::new;
	}

	private static final long serialVersionUID = -5249323921696140512L;
}
