package ru.argustelecom.box.env.measure;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.measure.model.MeasureUnit;

@Getter
@AllArgsConstructor
@EqualsAndHashCode(of = "id", callSuper = false)
public class MeasureUnitDto extends ConvertibleDto {

	private Long id;
	private String name;

	@Override
	public Class<MeasureUnit> getEntityClass() {
		return MeasureUnit.class;
	}

	@Override
	public Class<MeasureUnitDtoTranslator> getTranslatorClass() {
		return MeasureUnitDtoTranslator.class;
	}

}