package ru.argustelecom.box.env.pricing;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.argustelecom.box.env.dto.ConvertibleDto;
import ru.argustelecom.box.env.dto.DefaultDtoTranslator;
import ru.argustelecom.box.env.party.model.CustomerSegment;
import ru.argustelecom.system.inf.modelbase.Identifiable;

@Getter
@Setter
@EqualsAndHashCode(of = {"id"}, callSuper = false)
@AllArgsConstructor
@NoArgsConstructor
public class CustomerSegmentDto extends ConvertibleDto {
	private Long id;
	private String name;

	@Override
	public Class<? extends DefaultDtoTranslator<?, ?>> getTranslatorClass() {
		return CustomerSegmentDtoTranslator.class;
	}

	@Override
	public Class<? extends Identifiable> getEntityClass() {
		return CustomerSegment.class;
	}
}
