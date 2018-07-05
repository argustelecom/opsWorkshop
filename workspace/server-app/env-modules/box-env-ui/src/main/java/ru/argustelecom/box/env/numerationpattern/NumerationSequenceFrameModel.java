package ru.argustelecom.box.env.numerationpattern;

import java.io.Serializable;

import javax.inject.Inject;

import lombok.Getter;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence.PeriodType;
import ru.argustelecom.system.inf.page.PresentationModel;

@PresentationModel
public class NumerationSequenceFrameModel implements Serializable {

	private static final long serialVersionUID = 9056454095456511024L;

	@Inject
	@Getter
	private NumerationSequenceLazyDataModel lazyDm;

	@Inject
	private NumerationSequenceAppService appService;

	public void delete(NumerationSequenceDto sequence) {
		appService.deleteNumerationSequence(sequence.getId());
	}

	public PeriodType[] getPeriodTypes() {
		return PeriodType.values();
	}
}
