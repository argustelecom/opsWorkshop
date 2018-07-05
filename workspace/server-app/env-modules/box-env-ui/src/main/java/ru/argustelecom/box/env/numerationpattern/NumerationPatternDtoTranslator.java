package ru.argustelecom.box.env.numerationpattern;

import ru.argustelecom.box.env.numerationpattern.model.NumerationPattern;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class NumerationPatternDtoTranslator {

	public NumerationPatternDto translate(NumerationPattern numerationPattern) {
		//@formatter:off
		return NumerationPatternDto.builder()
				.id(numerationPattern.getId())
				.className(numerationPattern.getClassName())
				.pattern(numerationPattern.getPattern())
				.build();
		//@formatter:on
	}
}
