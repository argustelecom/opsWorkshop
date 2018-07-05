package ru.argustelecom.box.env.numerationpattern;

import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.CURRENT_VALUE;
import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.NAME;
import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.PERIOD;
import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.VALID_FROM;
import static ru.argustelecom.box.env.numerationpattern.NumerationSequenceFrameState.NumerationSequenceFilter.VALID_TO;

import java.io.Serializable;
import java.util.Date;

import javax.inject.Named;

import lombok.Getter;
import lombok.Setter;
import ru.argustelecom.box.env.filter.FilterMapEntry;
import ru.argustelecom.box.env.filter.FilterViewState;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.PeriodType;
import ru.argustelecom.system.inf.page.PresentationModel;

@Named(value = "numerationSequenceFilterFs")
@PresentationModel
@Getter
@Setter
public class NumerationSequenceFrameState extends FilterViewState implements Serializable {

	private static final long serialVersionUID = -2367116669600923125L;

	@FilterMapEntry(NAME)
	private String name;
	@FilterMapEntry(PERIOD)
	private PeriodType period;
	@FilterMapEntry(VALID_FROM)
	private Date validFrom;
	@FilterMapEntry(VALID_TO)
	private Date validTo;
	@FilterMapEntry(CURRENT_VALUE)
	private Long currentValue;

	public static class NumerationSequenceFilter {
		public static final String NAME = "NAME";
		public static final String PERIOD = "PERIOD";
		public static final String VALID_FROM = "VALID_FROM";
		public static final String VALID_TO = "VALID_TO";
		public static final String CURRENT_VALUE = "CURRENT_VALUE";
	}
}
