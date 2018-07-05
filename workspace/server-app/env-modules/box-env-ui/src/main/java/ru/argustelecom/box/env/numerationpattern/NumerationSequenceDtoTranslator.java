package ru.argustelecom.box.env.numerationpattern;

import javax.inject.Inject;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.numerationpattern.model.NumerationSequence;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class NumerationSequenceDtoTranslator implements DefaultDtoTranslator<NumerationSequenceDto, NumerationSequence> {

	@Inject
	private NumerationPatternRepository repository;

	public NumerationSequenceDto translate(NumerationSequence numerationSequence) {
		//@formatter:off
		return NumerationSequenceDto.builder()
				.id(numerationSequence.getId())
				.name(numerationSequence.getName())
				.initialValue(numerationSequence.getInitialValue())
				.increment(numerationSequence.getIncrement())
				.period(numerationSequence.getPeriod())
				.capacity(numerationSequence.getCapacity())
				.validTo(numerationSequence.getValidTo())
				.currentValue(numerationSequence.getCurrentValue())
				.canBeDeleted(repository.canBeDeleted(numerationSequence.getName()))
			.build();
		//@formatter:on
	}

}
