package ru.argustelecom.box.env.measure;

import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.measure.model.MeasureUnit;
import ru.argustelecom.box.inf.service.DtoTranslator;

@DtoTranslator
public class MeasureUnitDtoTranslator implements DefaultDtoTranslator<MeasureUnitDto, MeasureUnit> {

	@Override
	public MeasureUnitDto translate(MeasureUnit measureUnit) {
		return new MeasureUnitDto(measureUnit.getId(), measureUnit.getName());
	}

}